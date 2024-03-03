import psutil
import platform
import subprocess
import os
import json
import time

while True:
    system_info = {}

    # CPU Information
    system_info["cpu"] = {
        "type": platform.processor(),
        "architecture": platform.architecture()[0]
    }

    machine = platform.machine().lower()
    if os.path.exists("/proc/device-tree"):
        system_info["cpu"]["architecture_type"] = "ARM"
    else:
        system_info["cpu"]["architecture_type"] = "Desktop"

    try:
        cpuinfo_output = subprocess.check_output(["cat", "/proc/cpuinfo"]).decode("utf-8")
        lines = cpuinfo_output.splitlines()

        for line in lines:
            if "Hardware" in line:
                hardware_line = line.split(":")
                if len(hardware_line) > 1:
                    system_info["cpu"]["hardware"] = hardware_line[1].strip()
            elif "model name" in line.lower():
                model_name_line = line.split(":")
                if len(model_name_line) > 1:
                    system_info["cpu"]["brand"] = model_name_line[1].strip()
    except subprocess.CalledProcessError:
        system_info["cpu"]["brand"] = "Unknown"

    # Check if the "brand" key exists in the system_info dictionary
    if "brand" not in system_info["cpu"]:
        # If "brand" is not found, set it to "Unknown"
        system_info["cpu"]["brand"] = "Unknown"

    # Set the "hardware" field to the value of "brand" if "Hardware" is not found
    if "hardware" not in system_info["cpu"]:
        # If "hardware" is not found, set it to the value of "brand" (or "Unknown" if "brand" is not found)
        system_info["cpu"]["hardware"] = system_info["cpu"].get("brand", "Unknown")


    system_info["cpu"]["usage"] = f"{psutil.cpu_percent(interval=1)}%"
    system_info["cpu"]["speed"] = f"{psutil.cpu_freq().current / 1000:.2f} GHz"

    # CPU Temperature (Alternative Method)
    try:
        with open("/sys/class/thermal/thermal_zone0/temp", "r") as temp_file:
            temperature = int(temp_file.read()) / 1000.0
        system_info["cpu"]["temperature"] = f"{temperature:.1f}"
    except FileNotFoundError:
        system_info["cpu"]["temperature"] = "N/A"

    # CPU Cores
    system_info["cpu"]["cores"] = {}
    core_percentages = psutil.cpu_percent(interval=1, percpu=True)
    for i, core_percent in enumerate(core_percentages):
        system_info["cpu"]["cores"][f"core_{i + 1}"] = f"{core_percent}%"

    # Storage Information (excluding loop devices and non-mounted partitions)
    storage_info = []
    lsblk_output = subprocess.check_output(["lsblk", "--output", "NAME,SIZE,TYPE,MOUNTPOINT,FSTYPE"]).decode("utf-8")
    lines = lsblk_output.strip().split("\n")[1:]  # Skip header
    for line in lines:
        parts = line.split()
        name = parts[0].replace("├─", "").replace("└─", "")  # Remove symbols
        size = parts[1] if len(parts) > 1 else "Unknown"  # Define size here
        if len(parts) >= 4 and parts[2] not in ("loop", "rom"):  # Exclude loop and rom devices
            # Modify size string
            if size != "Unknown":
                size_value = size[:-1]  # Get all but the last character
                size_unit = size[-1]  # Get the last character
                size = f"{size_value} {size_unit.upper()}B"
            device_info = {
                "name": name,
                "size": size,  # Use modified size here
                "type": parts[2] if len(parts) > 2 else "Unknown",
                "mountpoint": parts[3] if len(parts) > 3 else "Unknown",
                "fstype": parts[4] if len(parts) > 4 else "Unknown"
            }
            if device_info["mountpoint"] != "N/A" and device_info["fstype"] != "N/A":
                try:
                    usage = psutil.disk_usage(device_info["mountpoint"])
                    device_info["used"] = f"{usage.used / (1000 ** 3):.2f} GB"
                    device_info["free"] = f"{usage.free / (1000 ** 3):.2f} GB"
                    device_info["usage_percent"] = f"{usage.percent}%"
                except FileNotFoundError:
                    device_info["used"] = "N/A"
                    device_info["free"] = "N/A"
                    device_info["usage_percent"] = "0.0%"
            else:
                device_info["used"] = "N/A"
                device_info["free"] = "N/A"
                device_info["usage_percent"] = "0.0%"
            storage_info.append(device_info)
    system_info["storage"] = storage_info

    # RAM Information
    virtual_memory = psutil.virtual_memory()
    system_info["ram"] = {
        "total": f"{virtual_memory.total / (1024 ** 3):.2f} GB",
        "free": f"{virtual_memory.available / (1024 ** 3):.2f} GB",
        "used": f"{virtual_memory.used / (1024 ** 3):.2f} GB",
        "usage_percent": f"{int(virtual_memory.percent)}%"
    }

    # Function to get network speed using ifstat
    def get_network_speed(interface):
        try:
            # Run ifstat command and capture the output
            output = subprocess.check_output(["ifstat", "-i", interface, "-n", "-q", "1", "1"]).decode("utf-8")
            lines = output.split("\n")

            # Check if there are enough lines to parse
            if len(lines) >= 3:
                # Extract download and upload speeds from the third line
                values = lines[2].split()

                if len(values) == 2:
                    download_speed_kbps, upload_speed_kbps = map(float, values)
                
                    # Convert speeds to Mbps
                    download_speed_mbps = download_speed_kbps * 0.008
                    upload_speed_mbps = upload_speed_kbps * 0.008

                    return download_speed_mbps, upload_speed_mbps

        except subprocess.CalledProcessError:
            pass

        return None, None

    # Network Information
    network_info = {}
    for iface, stats in psutil.net_if_stats().items():
        # Skip interfaces with names starting with "veth"
        if iface.startswith("veth") or iface.startswith("lo") or iface.startswith("br-"):
            continue

        iface_info = {
            "is_up": stats.isup,
        }

        # Get network speed information for the interface
        download_speed_mbps, upload_speed_mbps = get_network_speed(iface)

        # Check if speed values are available
        if download_speed_mbps is not None and upload_speed_mbps is not None:
            iface_info["speed"] = f"↓{download_speed_mbps:.2f} Mbps ↑{upload_speed_mbps:.2f} Mbps"
        else:
            iface_info["speed"] = "N/A"  # Set to "N/A" if speed values are not available

        network_info[iface] = iface_info

    system_info["network"] = network_info

    # OS Information
    system_info["os"] = {
        "name": platform.system(),
        "distribution": subprocess.check_output(["lsb_release", "-d"]).decode("utf-8").split(":")[1].strip(),
        "kernel_version": platform.uname().release,
        "uptime": f"{psutil.boot_time():.2f} seconds"
    }

    # Save the JSON data to a file
    with open("system_info.json", "w") as json_file:
        json.dump(system_info, json_file, indent=4)

    # Sleep for 15 seconds before the next iteration
    time.sleep(15)

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
        system_info["cpu"]["brand"] = "Not Available"

    system_info["cpu"]["usage"] = f"{psutil.cpu_percent(interval=1)}%"
    system_info["cpu"]["speed"] = f"{psutil.cpu_freq().current / 1000:.2f} GHz"
    system_info["cpu"]["temperature"] = os.popen('vcgencmd measure_temp').readline().replace("temp=", "").replace("'C\n", "")

    # CPU Cores
    system_info["cpu"]["cores"] = {}
    core_percentages = psutil.cpu_percent(interval=1, percpu=True)
    for i, core_percent in enumerate(core_percentages):
        system_info["cpu"]["cores"][f"core_{i + 1}"] = f"{core_percent}%"

    # Storage Information (excluding loop devices)
    storage_info = []
    lsblk_output = subprocess.check_output(["lsblk", "--output", "NAME,SIZE,TYPE,MOUNTPOINT,FSTYPE"]).decode("utf-8")
    lines = lsblk_output.strip().split("\n")[1:]  # Skip header
    for line in lines:
        parts = line.split()
        name = parts[0].replace("├─", "").replace("└─", "")  # Remove symbols
        if len(parts) >= 4 and not name.startswith("loop"):  # Exclude loop devices
            device_info = {
                "name": name,
                "size": parts[1],
                "type": parts[2],
                "mountpoint": parts[3],
                "fstype": parts[4] if len(parts) >= 5 else "Unknown"
            }
            if parts[3] != "Not" and parts[4] != "Mounted":
                usage = psutil.disk_usage(parts[3])
                device_info["used"] = f"{usage.used / (1024 ** 3):.2f} GB"
                device_info["free"] = f"{usage.free / (1024 ** 3):.2f} GB"
                device_info["usage_percent"] = f"{usage.percent}%"
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
    with open("/home/pi/rss/system_info.json", "w") as json_file:
        json.dump(system_info, json_file, indent=4)

    # Sleep for 15 seconds before the next iteration
    time.sleep(15)

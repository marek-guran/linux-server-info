import psutil
import platform
import subprocess
import os
from lxml import etree
import time

while True:
    # Create an XML document
    root = etree.Element("system_information")

    # CPU Information
    cpu_info = etree.SubElement(root, "cpu")
    cpu_info.attrib["type"] = platform.processor()
    cpu_info.attrib["architecture"] = platform.architecture()[0]

    # Check if the CPU is ARM or desktop and if it's 32-bit or 64-bit
    machine = platform.machine().lower()
    if os.path.exists("/proc/device-tree"):
        cpu_info.attrib["architecture_type"] = "ARM"
    else:
        cpu_info.attrib["architecture_type"] = "Desktop"

    # Get CPU brand and name using system commands
    try:
        cpuinfo_output = subprocess.check_output(["cat", "/proc/cpuinfo"]).decode("utf-8")
        lines = cpuinfo_output.splitlines()

        for i in range(len(lines)):
            if "Hardware" in lines[i]:
                hardware_line = lines[i].split(":")
                if len(hardware_line) > 1:
                    hardware = hardware_line[1].strip()
                    cpu_info.attrib["hardware"] = hardware
            elif "model name" in lines[i].lower():
                model_name_line = lines[i].split(":")
                if len(model_name_line) > 1:
                    model_name = model_name_line[1].strip()
                    cpu_info.attrib["brand"] = model_name
    except subprocess.CalledProcessError:
        cpu_info.attrib["brand"] = "Not Available"

    cpu_percent = psutil.cpu_percent(interval=1)
    cpu_speed = psutil.cpu_freq().current / 1000  # in GHz
    cpu_temp = os.popen('vcgencmd measure_temp').readline().replace("temp=","").replace("'C\n","")

    etree.SubElement(cpu_info, "usage").text = f"{cpu_percent}%"
    etree.SubElement(cpu_info, "speed").text = f"{cpu_speed} GHz"
    etree.SubElement(cpu_info, "temperature").text = f"{cpu_temp}℃"

    # CPU Cores
    for i, core_percent in enumerate(psutil.cpu_percent(interval=1, percpu=True)):
        core = etree.SubElement(cpu_info, f"core_{i + 1}")
        etree.SubElement(core, "usage").text = f"{core_percent}%"
    
    # Storage Information (excluding loop devices)
    storage_info = etree.SubElement(root, "storage")
    lsblk_output = subprocess.check_output(["lsblk", "--output", "NAME,SIZE,TYPE,MOUNTPOINT,FSTYPE"]).decode("utf-8")
    lines = lsblk_output.strip().split("\n")[1:]  # Skip header
    for line in lines:
        parts = line.split()
        name = parts[0].replace("├─", "").replace("└─", "")  # Remove symbols
        if len(parts) >= 4 and not name.startswith("loop"):  # Exclude loop devices
            device = etree.SubElement(storage_info, "device")
            etree.SubElement(device, "name").text = name
            etree.SubElement(device, "size").text = parts[1]
            etree.SubElement(device, "type").text = parts[2]
            etree.SubElement(device, "mountpoint").text = parts[3]
            
            if len(parts) >= 5:
                etree.SubElement(device, "fstype").text = parts[4]
            else:
                etree.SubElement(device, "fstype").text = "Unknown"

            # Get disk usage information
            if parts[3] != "Not" and parts[4] != "Mounted":
                usage = psutil.disk_usage(parts[3])
                etree.SubElement(device, "used").text = f"{usage.used / (1024 ** 3):.2f} GB"
                etree.SubElement(device, "free").text = f"{usage.free / (1024 ** 3):.2f} GB"
                etree.SubElement(device, "usage_percent").text = f"{usage.percent}%"

    # RAM Information
    ram_info = etree.SubElement(root, "ram")
    virtual_memory = psutil.virtual_memory()
    ram_info.attrib["total"] = f"{virtual_memory.total / (1024 ** 3):.2f} GB"
    ram_info.attrib["free"] = f"{virtual_memory.available / (1024 ** 3):.2f} GB"
    ram_info.attrib["used"] = f"{virtual_memory.used / (1024 ** 3):.2f} GB"
    ram_info.attrib["usage_percent"] = f"{int(virtual_memory.percent)}%"

    # Network Information
    network_info = etree.SubElement(root, "network")
    for iface, stats in psutil.net_if_stats().items():
        iface_info = etree.SubElement(network_info, "interface")
        etree.SubElement(iface_info, "name").text = iface
        etree.SubElement(iface_info, "is_up").text = str(stats.isup)
        etree.SubElement(iface_info, "duplex").text = str(stats.duplex)
        etree.SubElement(iface_info, "speed").text = f"{stats.speed / 1e6} Mbps"

    # OS Information
    os_info = etree.SubElement(root, "os")
    os_info.attrib["name"] = platform.system()
    os_info.attrib["distribution"] = subprocess.check_output(["lsb_release", "-d"]).decode("utf-8").split(":")[1].strip()
    os_info.attrib["kernel_version"] = platform.uname().release
    os_info.attrib["uptime"] = f"{psutil.boot_time():.2f} seconds"

    # Convert the XML to a string and save it to a file
    xml_str = etree.tostring(root, pretty_print=True, encoding="utf-8")
    with open("/home/pi/rss/system_info.xml", "wb") as xml_file:
        xml_file.write(xml_str)

    # Sleep for 15 seconds before the next iteration
    time.sleep(15)

#!/bin/bash

# Detect the current username
current_user=$(whoami)

# Update and install required packages
sudo apt-get update
sudo apt-get install -y python3 python3-pip lsb-release util-linux ifstat git

# Install Python packages from requirements.txt and handle errors
pip3 install -r requirements.txt || { echo "Error: Failed to install Python requirements."; }

# Clone the GitHub repository
git clone https://github.com/marek-guran/linux-server-info
cd linux-server-info

# Install requirements inside linux-server-info directory
pip3 install -r requirements.txt || { echo "Error: Failed to install Python requirements inside linux-server-info directory."; }

echo "Successfully installed. Don't forget to setup service and web server."

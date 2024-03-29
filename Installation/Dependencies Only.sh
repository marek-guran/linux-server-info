#!/bin/bash

# Detect the current username
current_user=$(whoami)

# Update and install required packages
sudo apt-get update
sudo apt-get install -y python3 python3-pip lsb-release util-linux ifstat git

# Download files and create dir
mkdir /home/$current_user/linux-server-info
cd /home/$current_user/linux-server-info
wget https://raw.githubusercontent.com/marek-guran/linux-server-info/main/requirements.txt
sudo chmod -R 777 /home/$current_user/linux-server-info

# Install requirements inside linux-server-info directory
sudo pip3 install -r requirements.txt || { echo "Error: Failed to install Python requirements inside linux-server-info directory."; }

echo "Successfully installed. Don't forget to setup service and web server."

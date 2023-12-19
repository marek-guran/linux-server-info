#!/bin/bash

current_user=$(whoami)

# Remove the Docker container
sudo docker stop server-info
sudo docker rm server-info

# Stop and disable the Python script service
sudo systemctl stop server-info
sudo systemctl disable server-info
sudo rm /etc/systemd/system/server-info.service
sudo systemctl daemon-reload

# Delete dir of server-info
sudo rm -rf /home/$current_user/linux-server-info

echo "Docker webserver, linux-server-info directory and python script service removed."
echo "If you want to also remove docker, execute: sudo apt remove docker.io -y"
echo "Uninstalled successfully."

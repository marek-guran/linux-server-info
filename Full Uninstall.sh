#!/bin/bash

# Remove the Docker container
sudo docker stop server-info
sudo docker rm server-info

# Stop and disable the Python script service
sudo systemctl stop server-info
sudo systemctl disable server-info
sudo rm /etc/systemd/system/server-info.service
sudo systemctl daemon-reload

echo "Docker webserver and Python script service removed."
echo "If you want to also remove docker, execute: sudo apt remove docker.io -y"
echo "Uninstalled successfully."

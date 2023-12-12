#!/bin/bash

# Stop and disable the Python script service
sudo systemctl stop server-info
sudo systemctl disable server-info
sudo rm /etc/systemd/system/server-info.service
sudo systemctl daemon-reload

echo "Python script service removed."
echo "Uninstalled successfully."

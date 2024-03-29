#!/bin/bash

# Detect the current username
current_user=$(whoami)

# Update and install required packages
sudo apt-get update
sudo apt-get install -y python3 python3-pip lsb-release util-linux ifstat git

# Download files and create dir
mkdir /home/$current_user/linux-server-info
cd /home/$current_user/linux-server-info
wget https://raw.githubusercontent.com/marek-guran/linux-server-info/main/server-info.py
wget https://raw.githubusercontent.com/marek-guran/linux-server-info/main/requirements.txt
sudo chmod -R 777 /home/$current_user/linux-server-info

# Install requirements inside linux-server-info directory
sudo pip3 install -r requirements.txt || { echo "Error: Failed to install Python requirements inside linux-server-info directory."; }

# Install Docker if not already installed
sudo apt-get install docker.io -y

# Run Docker container with restart always, linking to /home/user/linux-server-info/
sudo docker run -d --restart=always -p 9002:8080 -v /home/$current_user/linux-server-info:/app/wwwroot/api --name server-info emgi2/linux-server-info

echo "WEB GUi installed and running on port 9002."

# Create the systemd service file dynamically
cat <<EOL | sudo tee /etc/systemd/system/server-info.service > /dev/null
[Unit]
Description=Linux Server Info
After=network.target

[Service]
WorkingDirectory=/home/$current_user/linux-server-info
ExecStart=/usr/bin/python3 /home/$current_user/linux-server-info/server-info.py
Restart=on-failure
RestartSec=5

[Install]
WantedBy=multi-user.target
EOL
sudo chmod -R 777 /etc/systemd/system/server-info.service
# Reload and enable/start the service
sudo systemctl daemon-reload
sudo systemctl enable server-info
sudo systemctl start server-info

echo "Python script is running as a service."
echo "Successfully installed."
<p align="center">
    <img src="https://img.shields.io/badge/-Linux-grey?logo=linux">
    <img src="https://img.shields.io/badge/Android-3DDC84?style=flat&logo=android&logoColor=white">
    <img src="https://img.shields.io/badge/-Kotlin-0095D5?logo=kotlin&logoColor=white">
    <img src="https://img.shields.io/badge/Python-3776AB?style=flat&logo=python&logoColor=white">
</p>

<p align="center">
  <a href="https://play.google.com/store/apps/details?id=com.marekguran.serverinfo" style="margin-right: 20px;">
        <img src="google-play-badge.png" width="300" alt="Server Info App">
  </a>
  <a href="https://github.com/marek-guran/linux-server-info/releases">
        <img src="github-badge.png" width="300" alt="GitHub Releases">
  </a>
</p>

# Automatic Installation (web server in docker by default uses port 9000)
Full Install
```
curl -sSL https://raw.githubusercontent.com/marek-guran/linux-server-info/main/Full%20Install.sh | bash
```

Dependencies + Service
```
curl -sSL https://raw.githubusercontent.com/marek-guran/linux-server-info/main/Dependencies%20%2B%20Service.sh | bash
```

Dependencies Only
```
curl -sSL https://raw.githubusercontent.com/marek-guran/linux-server-info/main/Dependencies%20Only.sh | bash
```

Full Uninstall
```
curl -sSL https://raw.githubusercontent.com/marek-guran/linux-server-info/main/Full%20Uninstall.sh | bash
```

Uninstall Service
```
curl -sSL https://raw.githubusercontent.com/marek-guran/linux-server-info/main/Uninstall%20Service.sh | bash
```
| Option             | Docker | Dependencies | Service | Requirements.txt |
|--------------------|--------|--------------|---------|------------------|
| Full Install       | ✅     | ✅           | ✅      | ✅               |
| Dependencies + Service | ❌ | ✅           | ✅      | ✅               |
| Dependencies only | ❌     | ✅           | ❌      | ✅               |

| Option           | Docker | Service | Docker Container |
|------------------|--------|---------|------------------------------|
| Full Uninstall   | ❌     | ✅      | ✅                           |
| Uninstall Service| ❌     | ✅      | ❌                           |
## Instalation (manual)
Download the ```requirements.txt```, then execute commands:
```sudo apt-get update && sudo apt-get install -y python3 python3-pip lsb-release util-linux ifstat && sudo pip3 install -r requirements.txt```
## Download the python file
Edit it for your paths (file output should be location of your web server www file location. You can use for example httpd as a web server.
## Download service file (if you want it to run as a service)
There just change your file locations and start it with:
```sudo systemctl start server-info```
and if you want it to start on each boot, use command:
```sudo systemctl enable server-info```
## Download app
You can download it from releases section or by Google Play Store. Once installed, head to settings and put there your web server address with full path to file. For example: ```http://10.0.1.1:9000/system_info.json```

<p align="center">
    <img src="https://img.shields.io/badge/-Linux-grey?logo=linux">
    <img src="https://img.shields.io/badge/Android-3DDC84?style=flat&logo=android&logoColor=white">
    <img src="https://img.shields.io/badge/-Kotlin-0095D5?logo=kotlin&logoColor=white">
    <img src="https://img.shields.io/badge/Python-3776AB?style=flat&logo=python&logoColor=white">
</p>

<p align="center">
  <a href="https://play.google.com/store/apps/details?id=com.marekguran.serverinfo" style="margin-right: 20px;">
        <img src="Github-Assets/google-play-badge.png" width="300" alt="Server Info App">
  </a>
  <a href="https://github.com/marek-guran/linux-server-info/releases">
        <img src="Github-Assets/github-badge.png" width="300" alt="GitHub Releases">
  </a>
</p>

#### This version needs SUDO privileges since it needs to save JSON file into RAM by mounting it as a tmpfs
## Monitor your server with this resources light python script.
After successful setup, you are able to monitor your server or anything what is running Debian based Linux by Android App, which is compatible with all Android 6+ devices including Chromebooks and Android TV. Or use WEB GUi that comes with this repository.

## WEB GUI Images

### Light Mode
<a href="https://github.com/marek-guran/linux-server-info/assets/26904790/3cf012c9-7920-49b5-aeea-c0a084a20e62">
    <img src="https://github.com/marek-guran/linux-server-info/assets/26904790/3cf012c9-7920-49b5-aeea-c0a084a20e62" width="320" height="180" alt="Snímka obrazovky (133)">
</a>
<a href="https://github.com/marek-guran/linux-server-info/assets/26904790/81ad57f4-5c4d-475f-a934-e25c0629ce7b">
    <img src="https://github.com/marek-guran/linux-server-info/assets/26904790/81ad57f4-5c4d-475f-a934-e25c0629ce7b" width="320" height="180" alt="Snímka obrazovky (134)">
</a>
<a href="https://github.com/marek-guran/linux-server-info/assets/26904790/2b963a6d-1e21-4c6a-98dd-a413bd5f6efe">
    <img src="https://github.com/marek-guran/linux-server-info/assets/26904790/2b963a6d-1e21-4c6a-98dd-a413bd5f6efe" width="320" height="180" alt="Snímka obrazovky (135)">
</a>

### Dark Mode
<a href="https://github.com/marek-guran/linux-server-info/assets/26904790/4e10cc9b-ed80-451d-a40c-eca54d11de2b">
    <img src="https://github.com/marek-guran/linux-server-info/assets/26904790/4e10cc9b-ed80-451d-a40c-eca54d11de2b" width="320" height="180" alt="Snímka obrazovky (136)">
</a>
<a href="https://github.com/marek-guran/linux-server-info/assets/26904790/7e4caa99-4c33-4a0c-80e7-bbe67b5a93ca">
    <img src="https://github.com/marek-guran/linux-server-info/assets/26904790/7e4caa99-4c33-4a0c-80e7-bbe67b5a93ca" width="320" height="180" alt="Snímka obrazovky (137)">
</a>
<a href="https://github.com/marek-guran/linux-server-info/assets/26904790/460246dc-0809-4ae0-a8d4-4c67d465b068">
    <img src="https://github.com/marek-guran/linux-server-info/assets/26904790/460246dc-0809-4ae0-a8d4-4c67d465b068" width="320" height="180" alt="Snímka obrazovky (138)">
</a>

### System and CPU Detection
<a href="https://github.com/marek-guran/linux-server-info/assets/26904790/92041285-89ac-463a-8219-6c556595f93a">
    <img src="https://github.com/marek-guran/linux-server-info/assets/26904790/92041285-89ac-463a-8219-6c556595f93a" width="320" height="180" alt="Snímka obrazovky (139)">
</a>
<a href="https://github.com/marek-guran/linux-server-info/assets/26904790/2da91114-7031-493b-8dbb-d562cb07fbb3">
    <img src="https://github.com/marek-guran/linux-server-info/assets/26904790/2da91114-7031-493b-8dbb-d562cb07fbb3" width="320" height="180" alt="Snímka obrazovky (140)">
</a>
<a href="https://github.com/marek-guran/linux-server-info/assets/26904790/2643eb50-d07b-49bf-bbcd-501b710faf5d">
    <img src="https://github.com/marek-guran/linux-server-info/assets/26904790/2643eb50-d07b-49bf-bbcd-501b710faf5d" width="320" height="180" alt="Snímka obrazovky (141)">
</a>



# Automatic Installation (web server in docker by default uses port 9002)
Full Install
```
curl -sSL https://raw.githubusercontent.com/marek-guran/linux-server-info/main/Installation/Full%20Install.sh | bash
```

Dependencies + Service
```
curl -sSL https://raw.githubusercontent.com/marek-guran/linux-server-info/main/Installation/Dependencies%20%2B%20Service.sh | bash
```

Dependencies Only
```
curl -sSL https://raw.githubusercontent.com/marek-guran/linux-server-info/main/Installation/Dependencies%20Only.sh | bash
```

Full Uninstall
```
curl -sSL https://raw.githubusercontent.com/marek-guran/linux-server-info/main/Installation/Full%20Uninstall.sh | bash
```

Uninstall Service
```
curl -sSL https://raw.githubusercontent.com/marek-guran/linux-server-info/main/Installation/Uninstall%20Service.sh | bash
```
| Option             | Docker + WEB Gui | Dependencies | Service | Requirements.txt |
|--------------------|--------|--------------|---------|------------------|
| Full Install       | ✅     | ✅           | ✅      | ✅               |
| Dependencies + Service | ❌ | ✅           | ✅      | ✅               |
| Dependencies only | ❌     | ✅           | ❌      | ✅               |

Web server will use ```/home/user/server-info/``` directory to publish json file for app and will be on port ```9002```. Will be used WEB GUi container and ```docker.io package```. You will be able to access it at: ```http://device-ip:9002/``` and api for Android App at: ```http://device-ip:9002/api```

| Option           | Docker | Service | WEB GUi | Linux Server Info Folder |
|------------------|--------|---------|---------|------------------------------|
| Full Uninstall   | ❌     | ✅      | ✅       | ✅                          |
| Uninstall Service| ❌     | ✅      | ❌       | ❌                          |

You can uninstall ```docker.io``` by ```sudo apt remove docker.io -y```. It is not removed automatically, since docker is used by many people.
## Updating
Updating is simple manual process. Just download and replace the python file and requirements.txt (By default these files are saved in your home directory in linux-server-info folder). 
And run commands: ```sudo pip3 install -r requirements.txt && sudo systemctl restart server-info```
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
You can download it from releases section or by Google Play Store. Once installed, head to settings and put there your web server address with full path to file. For example: ```http://10.0.1.1:9002/api```

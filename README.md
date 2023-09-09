![Linux Badge](https://img.shields.io/badge/-Linux-grey?logo=linux) ![Android Badge](https://img.shields.io/badge/Android-3DDC84?style=flat&logo=android&logoColor=white) ![Kotlin Badge](https://img.shields.io/badge/-Kotlin-0095D5?logo=kotlin&logoColor=white) ![Python Badge](https://img.shields.io/badge/Python-3776AB?style=flat&logo=python&logoColor=white) 

## Instalation
Download the ```requirements.txt```, then execute commands:
```sudo apt-get update && sudo apt-get install -y python3 lsb-release util-linux ifstat && pip3 install -r requirements.txt```
## Download the python file
Edit it for your paths (file output should be location of your web server www file location. You can use for example httpd as a web server.
## Download service file (if you want it to run as a service)
There just change your file locations and start it with:
```sudo systemctl start server-info```
and if you want it to start on each boot, use command:
```sudo systemctl enable server-info```
## Download app
For now you can download it from releases section. Once installed, head to settings and put there your web server address with full path to file. For example: ```http://10.0.1.1:9000/system_info.json```

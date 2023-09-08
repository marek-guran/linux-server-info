![Linux Badge](https://img.shields.io/badge/-Linux-grey?logo=linux) ![Android Badge](https://img.shields.io/badge/Android-3DDC84?style=flat&logo=android&logoColor=white) ![Kotlin Badge](https://img.shields.io/badge/-Kotlin-0095D5?logo=kotlin&logoColor=white) ![Python Badge](https://img.shields.io/badge/Python-3776AB?style=flat&logo=python&logoColor=white) 
# Work in progress
Documentation will be added once its done. (You are here too soon)
# Sneak Peak (now with notifications)
![Screenshot_20230908-145818_Pixel Launcher](https://github.com/marek-guran/linux-server-info/assets/26904790/2f5a6bc9-bda8-484f-b162-42ea0089048b)
![20230908_150014073](https://github.com/marek-guran/linux-server-info/assets/26904790/3172182b-3ef1-4f05-b327-ee47146ce00d)
![20230908_150014119](https://github.com/marek-guran/linux-server-info/assets/26904790/141ac3b9-dd9d-4ab5-a5c5-fedb64c683fd)


# If you want to test very alpha versions
## Install these packages
```sudo apt-get update && sudo apt-get install -y python3 lsb-release util-linux ifstat && pip3 install -r requirements.txt```
## Download the python file
Edit it for your paths (file output should be location off your web server www file location. You can use for example httpd as web server.
## Download service file (if you want it to run as a service)
There just change your file locations, start it with:
```sudo systemctl start server-info```
and if you want it to start on each boot, use command
```sudo systemctl enable server-info```
## Download the APP folder
Once downloaded, download and install android studio and change IP address of fragments.kt - for now each fragment needs manual override (they are in folder java, there will be better documentation for this once it's done). Default value the app is listening on is: `http://10.0.1.1:9000/system_info.json`
## Build app
You can build it by 2 ways: click play button and it will install on your device or click build/select apk and then just install it manually.

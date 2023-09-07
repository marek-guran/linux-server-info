# Work in progress
Documentation will be added once its done. (You are here too soon)
# Sneak Peak
![20230907_181409746](https://github.com/marek-guran/linux-server-info/assets/26904790/7ad7f24e-e40d-4fd9-9048-00f0f90e5e4b)
![20230907_181409749](https://github.com/marek-guran/linux-server-info/assets/26904790/f3fc92f4-300b-4220-835f-6d025aeb1ee3)

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

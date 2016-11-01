#!/bin/bash  

user="lama"
DIR="/home/$user/cloudSecurity/webserver/codes"

###### compile the program #####


cd $DIR;javac -d $DIR main/*.java;javac -d $DIR servlet/*.java;javac -d $DIR configuration/*.java;jar cfm cloudSecurityWeb.jar manifest.txt main/*.class configuration/*.class servlet/*.class;

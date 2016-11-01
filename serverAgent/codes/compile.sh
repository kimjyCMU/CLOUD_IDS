#!/bin/bash  

user="lama"
DIR="/home/$user/cloudSecurity/serverAgent/codes"

###### compile the program #####


cd $DIR;javac -d $DIR monitor/*.java;javac -d $DIR datatype/*.java;javac -d $DIR configuration/*.java;jar cfm cloudSecurityServer.jar manifest.txt datatype/*.class configuration/*.class monitor/*.class;

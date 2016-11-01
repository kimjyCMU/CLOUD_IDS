#!/bin/bash  

user="lama"
DIR="/home/$user/cloudSecurity/clientAgent/codes"

###### compile the program #####


cd $DIR;javac -d $DIR process/*.java;javac -d $DIR datatype/*.java;javac -d $DIR configuration/*.java;jar cfm cloudSecurity.jar manifest.txt datatype/*.class configuration/*.class process/*.class;

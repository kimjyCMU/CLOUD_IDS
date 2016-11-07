#!/bin/bash  

user="lama"
password="llama"

DIR="/home/$user/cloudSecurity"
FILE="cloudSecurity.jar"
###### run the program #####

cd $DIR; echo $password | sudo -S java -Djava.library.path=./lib -jar $FILE 

#!/bin/bash  

user="lama"
#password="llama"
password="theOne!penguinsFTW"
userR="root"

#DIR="/home/$user/cloudSecurity"
DIR="/home/cloudSecurity/clientAgent"

FILE="cloudSecurity.jar"
###### run the program #####

cd $DIR; echo $password | sudo -S java -Djava.library.path=./lib -jar $FILE 

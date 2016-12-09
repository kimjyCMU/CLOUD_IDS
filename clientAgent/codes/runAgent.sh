#!/bin/bash  

user="lama"
password="llama"
DIR="/home/$user/cloudSecurity"

#password="theOne!penguinsFTW"
#DIR="/home/cloudSecurity/clientAgent"

FILE="cloudSecurity.jar"
###### run the program #####

cd $DIR; echo $password | sudo -S java -Djava.library.path=./lib -jar $FILE 

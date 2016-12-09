#!/bin/bash  

password="jykim1230!"
DIR="/home/cloudSecurity"

FILE="cloudSecurity.jar"
###### run the program #####

#cd $DIR; echo $password | sudo -S java -Djava.library.path=./lib -jar $FILE 
cd $DIR; java -Djava.library.path=./lib -jar $FILE
#!/bin/bash  

user="lama"
DIR="/home/$user/cloudSecurity/clientAgent/codes"
FILE="cloudSecurity.jar"
###### run the program #####

cd $DIR; sudo java -Djava.library.path=./lib -jar $FILE 
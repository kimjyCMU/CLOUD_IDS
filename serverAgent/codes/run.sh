#!/bin/bash  

user="lama"
DIR="/home/$user/cloudSecurity/serverAgent/codes"

###### run the program #####

java -Djava.library.path=./lib -jar cloudSecurityServer.jar  


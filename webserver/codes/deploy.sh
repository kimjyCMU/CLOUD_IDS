#!/bin/bash  

user="lama"
sourceDIR="/home/$user/cloudSecurity/webserver/codes"
tomcatDIR="/usr/share/tomcat7"

# Copy the latest files to tomcat/lib
cd $sourceDIR
sudo cp cloudSecurityWeb.jar Config.txt lib/* $tomcatDIR/lib

# Restart tomcat7
sudo service tomcat7 restart

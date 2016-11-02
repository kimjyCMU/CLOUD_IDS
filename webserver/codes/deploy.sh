#!/bin/bash  

user="lama"
sourceDIR="/home/$user/cloudSecurity/webserver/codes"
tomcatDIR="/var/lib/tomcat7/webapps/ROOT/"

# Copy the latest files to tomcat/lib
cd $sourceDIR
sudo cp Config.txt $tomcatDIR/cloudSecurity
sudo cp lib/* $tomcatDIR/WEB-INF/lib
sudo cp cloudSecurityWeb.jar $tomcatDIR/WEB-INF/lib

# Restart tomcat7
sudo service tomcat7 restart

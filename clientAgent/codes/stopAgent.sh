#!/bin/bash  

FILE="cloudSecurity.jar"
password="llama"

y=`ps aux | grep $FILE | awk '{print $2}'`
echo $password | sudo -S kill -9 $y
y=`ps aux | grep run.sh`;kill -9 $y

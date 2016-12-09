#!/bin/bash  

FILE1="cloudSecurity.jar"
FILE2="runAgent.sh"
#password="jykim1230!"

y1=`ps aux | grep $FILE1 | awk '{print $2}'`
y2=`ps aux | grep $FILE2 | awk '{print $2}'`
#echo $password | sudo -S kill -9 $y1
#echo $password | sudo -S kill -9 $y2
kill -9 $y1
kill -9 $y2
#!/bin/bash  

y=`ps aux | grep cloudSecurityServer.jar | awk '{print $2}'`;kill -9 $y
y=`ps aux | grep run.sh | awk '{print $2}'`;kill -9 $y


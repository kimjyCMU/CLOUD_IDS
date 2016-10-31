#!/bin/bash  

user="lama"

###### Kill the program #####
DIR="/home/$user/cloudSecurity/clientAgent"
IPlist="$DIR/deploy/scripts/iplist.txt"
IPs=$(cat $IPlist) 
File="cloudSecurity.jar"

#certification established - need to install 'expect' 
for i in $IPs  
do   
echo "** Stop : "$i

ssh $user@$i "pkill -f '$File'" 
done  
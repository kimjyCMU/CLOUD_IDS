#!/bin/bash  

user="lama"
userR="root"

###### Run the program #####
DIR="/home/$user/cloudSecurity/clientAgent"
IPlist="$DIR/deploy/scripts/fedora/iplist.txt"
IPs=$(cat $IPlist) 

RemoteDIR="/home/cloudSecurity"
File="stopAgent.sh"

for i in $IPs  
do   
echo "** Run : "$i

ssh $userR@$i "$RemoteDIR/$File " &
done  

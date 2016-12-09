#!/bin/bash  

user="lama"
userR="root"

###### Run the program #####
DIR="/home/$user/cloudSecurity/clientAgent"
IPlist="$DIR/deploy/scripts/iplist.txt"
IPs=$(cat $IPlist) 

RemoteDIR="/home/$user/cloudSecurity"
#RemoteDIR="/home/cloudSecurity/clientAgent"
File="stopAgent.sh"

for i in $IPs  
do   
echo "** Run : "$i

ssh -t -t $user@$i "$RemoteDIR/$File " &
#ssh -t -t $userR@$i "$RemoteDIR/$File " &
done  

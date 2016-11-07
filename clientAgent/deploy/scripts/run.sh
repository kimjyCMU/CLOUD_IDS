#!/bin/bash  

user="lama"
password="llama"

###### Run the program #####
DIR="/home/$user/cloudSecurity/clientAgent"
IPlist="$DIR/deploy/scripts/iplist.txt"
IPs=$(cat $IPlist) 

RemoteDIR="/home/$user/cloudSecurity"
File="runAgent.sh"

for i in $IPs  
do   
echo "** Run : "$i

ssh -t -t $user@$i "$RemoteDIR/$File" &  

done

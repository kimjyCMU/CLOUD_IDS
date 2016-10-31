#!/bin/bash  

user="lama"

###### Run the program #####
DIR="/home/$user/cloudSecurity/clientAgent"
IPlist="$DIR/deploy/scripts/iplist.txt"
IPs=$(cat $IPlist) 

RemoteDIR="/home/$user/cloudSecurity"
File="cloudSecurity.jar"

for i in $IPs  
do   
echo "** Run : "$i

ssh $user@$i "cd $RemoteDIR;java -Djava.library.path=./lib -jar '$File' " &
done  
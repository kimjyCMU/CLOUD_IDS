#!/bin/bash  

user="lama"

###### Update with the latest files first
DIR="/home/$user/cloudSecurity/clientAgent"
cd $DIR/codes; 
cp -r cloudSecurity.jar Config.txt runAgent.sh stopAgent.sh lib/ $DIR/deploy/files/

###### Send the files to VMs
IPlist="$DIR/deploy/scripts/iplist.txt"
IPs=$(cat $IPlist)
  
for i in $IPs  
do   

echo "========================="
echo $i

ssh $user@$i "mkdir -p /home/$user/cloudSecurity"
scp -r $DIR/deploy/files/* $user@$i:/home/$user/cloudSecurity
#scp -r $DIR/deploy/files/cloudSecurity.jar $user@$i:/home/$user/cloudSecurity
done

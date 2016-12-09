#!/bin/bash  

user="lama"
userR="root"

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

# for Root
#ssh $userR@$i "mkdir -p /home/cloudSecurity/clientAgent"
#ssh $userR@$i "cd /usr/lib64; ln -s libpcap.so.1.5.3 libpcap.so.0.8"
#scp -r $DIR/deploy/files/* $userR@$i:/home/cloudSecurity/clientAgent
#scp -r $DIR/deploy/files/cloudSecurity.jar $userR@$i:/home/cloudSecurity/clientAgent
#scp -r $DIR/deploy/files/Config.txt $userR@$i:/home/cloudSecurity/clientAgent

# for lama
#ssh $user@$i "mkdir -p /home/$user/cloudSecurity"
scp -r $DIR/deploy/files/* $user@$i:/home/$user/cloudSecurity
#scp -r $DIR/deploy/files/cloudSecurity.jar $user@$i:/home/$user/cloudSecurity

done

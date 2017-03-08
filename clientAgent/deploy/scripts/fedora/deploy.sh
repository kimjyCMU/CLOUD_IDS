#!/bin/bash  

user="lama"
userR="root"

###### Update with the latest files first
DIR="/home/$user/cloudSecurity/clientAgent"
cd $DIR/codes; 
cp -r cloudSecurity.jar lib/ $DIR/deploy/files/fedora
cd $DIR/deploy/scripts/fedora;
cp runAgent.sh stopAgent.sh Config.txt $DIR/deploy/files/fedora

###### Send the files to VMs
IPlist="$DIR/deploy/scripts/fedora/iplist.txt"
IPs=$(cat $IPlist)
  
for i in $IPs  
do   

echo "========================="
echo $i

#ssh $userR@$i "mkdir -p /home/cloudSecurity"
scp -r $DIR/deploy/files/fedora/* $userR@$i:/home/cloudSecurity/
#scp -r $DIR/deploy/files/fedora/Config.txt $userR@$i:/home/cloudSecurity/
#ssh $userR@$i "cd /usr/lib64; ln -s libpcap.so.1.7.3 libpcap.so.0.8"
#ssh $userR@$i "mkdir -p /home/attacks/worm"
#scp pwd200.txt $userR@$i:/home/attacks/worm/ 
done

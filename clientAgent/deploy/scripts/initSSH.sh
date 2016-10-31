#!/bin/bash  
#!/usr/bin/expect

user="lama"
password="llama"

###### Copy authorized_keys to remote machines for auto SSH connection ##### 
DIR="/home/$user/cloudSecurity/clientAgent"
IPlist="$DIR/deploy/scripts/iplist.txt"
IPs=$(cat $IPlist) 

#certification established - need to install 'expect' 
for i in $IPs  
do   
expect -c "  
spawn ssh-copy-id -i /home/$user/.ssh/id_rsa.pub $user@$i  
   expect {  
       \"*yes/no*\" {send \"yes\r\"; exp_continue}  
       \"*password*\" {send \"$password\r\"; exp_continue}  
       \"*Password*\" {send \"$password\r\";}  
   }  
"  
done  
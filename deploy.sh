#!/bin/sh
 
#set -e

server=$1
buildDir=/opt/talool/builds

if [ -n "$2" ]; then
  warFile="$2"
else
 warFile=$(ls target/*SNAPSHOT.war)
fi

warFileName=$(basename $warFile) 

read -s -p "sudo pass on $server? " pass

ssh -t $server "echo $pass | sudo -S rm -rf $buildDir/$warFileName"

echo "\nDeploying $warFile..."
scp -p "$warFile" $server:$buildDir

ssh -t $server "echo $pass | sudo -S /opt/talool/builds/deploy.sh $warFileName"

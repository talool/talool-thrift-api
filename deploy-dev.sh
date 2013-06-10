#!/bin/sh

server=dev-api1

if [ -n "$1" ]; then
  warFile="$1"
else
 warFile=$(ls target/*SNAPSHOT.war | cut -d'/' -f2)
fi

read -s -p "sudo pass on $server? " pass

echo "\nDeploying $warFile..."
scp "$warFile" $server:/opt/talool/builds

ssh -t $server "echo $pass | sudo -S /opt/talool/builds/deploy-service-api.sh $warFile"

#!/bin/sh

warFile=talool-service-1.0.1-SNAPSHOT.war
server=dev-api1

scp target/talool-service-1.0.1-SNAPSHOT.war $server:/opt/talool/builds
ssh -t $server 'sudo /opt/talool/builds/deploy-service-api.sh $warFile'

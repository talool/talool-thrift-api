#!/bin/sh

#scp target/talool-website-1.0.0-SNAPSHOT.war talool:~/.


ssh -t talool 'sudo /etc/init.d/tomcat stop'
ssh -t talool 'sudo /etc/init.d/tomcat start'

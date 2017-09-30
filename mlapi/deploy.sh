#!/bin/bash

cd "${0%/*}"

remoteHost="usr_trafgar@opendatafr01f.be3.local"

remoteDir="/home/usr_trafgar/test"


sbt test
if [ $? == 0 ]
	then
		sbt pack
		rsync -avrc --delete \
		    --exclude tmp \
			--exclude logs \
			target/pack/ $remoteHost:$remoteDir
fi

#!/usr/bin/env bash

cd "${0%/*}"

remoteDir=/home/anthony/projects/popthecorn/mlapi
remoteHost="anthony@192.168.1.26"
portNumber=222

sbt test pack
		rsync -avrc --delete \
		    --exclude tmp \
		    --exclude-from ./deploy.sh \
		    -e "ssh -p $portNumber" \
			target/pack/ "$remoteHost":"$remoteDir"
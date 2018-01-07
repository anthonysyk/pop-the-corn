#!/usr/bin/env bash

cd "${0%/*}"

remoteDir=/home/anthony/projects/pop-the-corn/front
remoteHost="anthony@192.168.1.26"
portNumber=222

		rsync -avrc \
		    --exclude 'deploy.sh' \
		    -e "ssh -p $portNumber" \
			. "$remoteHost":"$remoteDir"
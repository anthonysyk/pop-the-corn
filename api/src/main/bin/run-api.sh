#!/usr/bin/env bash

remoteDir=/home/anthony/projects/pop-the-corn/api
port=9000

docker rmi popthecorn/api

cd "$remoteDir" && docker build -t popthecorn/api:latest .

docker container rm -f popthecorn-api

docker run -p "$port:$port" -d --name=popthecorn-api popthecorn/api

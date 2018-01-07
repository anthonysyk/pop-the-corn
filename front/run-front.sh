#!/usr/bin/env bash

remoteDir=/home/anthony/projects/pop-the-corn/front
port=8080

npm install

docker rmi popthecorn/front

cd "$remoteDir" && docker build -t popthecorn/front:latest .

docker container rm -f popthecorn-front

docker run -p "$port:$port" -d --name=popthecorn-front popthecorn/front

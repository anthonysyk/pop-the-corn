#!/usr/bin/env bash

remoteDir=/home/anthony/projects/popthecorn/front
port=8080

docker rmi popthecorn/front

cd "$remoteDir" && docker build -t popthecorn/front:latest .

docker container rm -f popthecorn-front

docker run -p "$port:$port" --network="host" -d --name=popthecorn-front popthecorn/front

docker images -q |xargs docker rmi
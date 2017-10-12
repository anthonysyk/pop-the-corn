#!/usr/bin/env bash

DockerImage=docker.elastic.co/elasticsearch/elasticsearch:5.6.3

startES(){
 echo "starting ES"
 if [ ! "$(docker ps -q -f name=elasticsearch)" ]; then
        if [ "$(docker ps -aq -f status=exited -f name=elasticsearch)" ]; then
            # cleanup
            docker rm elasticsearch
        fi
        # run your container
        docker run -d -p 9200:9200 --name elasticsearch -e "http.host=0.0.0.0" -e "transport.host=127.0.0.1" -e "xpack.security.enabled=false" $DockerImage
    fi
}

if [[ "$(docker images -q $DockerImage 2> /dev/null)" == "" ]]; then
    echo "image doesn't exist -- downloading image"
    docker pull $DockerImage
    startES
    else
    echo "image already exists"
    startES
fi


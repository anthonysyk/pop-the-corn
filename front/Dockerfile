FROM node

COPY . /home/pop-the-corn

# Using a non-privileged user:
WORKDIR /home/pop-the-corn

RUN [ "npm", "install" ]
CMD [ "npm", "run", "start-dev" ]

EXPOSE 8080


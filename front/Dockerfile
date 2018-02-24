FROM node

COPY . /home/popthecorn

# Using a non-privileged user:
WORKDIR /home/popthecorn

RUN [ "npm", "install" ]
CMD [ "npm", "run", "start-dev" ]

EXPOSE 8080


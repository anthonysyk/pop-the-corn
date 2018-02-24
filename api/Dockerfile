# Using an Alpine Linux based JDK image
FROM anapsix/alpine-java:8u131b11_jdk

COPY . /home/popthecorn

# Using a non-privileged user:
WORKDIR /home/popthecorn

ENTRYPOINT ["sh", "./bin/run-webservice", "--host", "0.0.0.0", "--port", "9000"]

EXPOSE 9000


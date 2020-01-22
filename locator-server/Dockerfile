FROM openjdk:8-jre-alpine

ARG JAR_FILE
ADD target/${JAR_FILE} /locator/locator.jar
ADD ./target/lib /locator/lib

COPY ./docker/entrypoint.sh /usr/local/bin/entrypoint.sh

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]

EXPOSE 8080
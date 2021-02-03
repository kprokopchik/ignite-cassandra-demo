FROM adoptopenjdk/openjdk11:jre-11.0.10_9-ubuntu

ARG JAR_FILE=build/libs/*.jar
ARG SERVER_PORT=8080

COPY ${JAR_FILE} app.jar

EXPOSE ${SERVER_PORT}

CMD java -jar /app.jar

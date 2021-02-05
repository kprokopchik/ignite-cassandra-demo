FROM adoptopenjdk/openjdk11:jre-11.0.10_9-ubuntu

ARG JAR_FILE=build/libs/*.jar
ARG SERVER_PORT=8080
ARG JMX_REMOTE_PORT=5005

ENV JAVA_TOOL_OPTIONS "-Dcom.sun.management.jmxremote.ssl=false \
 -Dcom.sun.management.jmxremote.authenticate=false \
 -Dcom.sun.management.jmxremote.port=${JMX_REMOTE_PORT} \
 -Dcom.sun.management.jmxremote.rmi.port=${JMX_REMOTE_PORT} \
 -Dcom.sun.management.jmxremote.host=0.0.0.0 \
 -Djava.rmi.server.hostname=0.0.0.0"

COPY ${JAR_FILE} app.jar

EXPOSE ${SERVER_PORT}

CMD java -jar /app.jar

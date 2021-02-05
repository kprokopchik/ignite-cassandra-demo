FROM adoptopenjdk/openjdk11:jre-11.0.10_9-ubuntu

ARG JAR_FILE=build/libs/*.jar
ARG SERVER_PORT=8081
ARG IGNITE_JETTY_PORT=8082
ARG JMX_REMOTE_PORT=5006

ENV JAVA_TOOL_OPTIONS "-Dcom.sun.management.jmxremote.ssl=false \
 -Dcom.sun.management.jmxremote.authenticate=false \
 -Dcom.sun.management.jmxremote.port=${JMX_REMOTE_PORT} \
 -Dcom.sun.management.jmxremote.rmi.port=${JMX_REMOTE_PORT} \
 -Dcom.sun.management.jmxremote.host=0.0.0.0 \
 -Djava.rmi.server.hostname=0.0.0.0"

COPY ${JAR_FILE} ignite.jar

#Apache Ignite ports:
#* 10800 - thin client connections
#* 11211 - for connect by internal protocol to the TCP server
#* 47100 - TcpCommunicationSpi
#* 47500 - TcpDiscoverySp
#* 48100 - sharedMemoryPort
#* 49128 - Remote Management

EXPOSE ${SERVER_PORT} ${IGNITE_JETTY_PORT} ${JMX_REMOTE_PORT} 10800 11211 47100 47100 47500 48100 49128

CMD java -DIGNITE_JETTY_PORT=${IGNITE_JETTY_PORT} -jar /ignite.jar
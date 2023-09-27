FROM amazoncorretto:21-al2023-jdk
MAINTAINER chris@weewelchie.org

COPY target/dynamo.sensordata-0.0.5-SNAPSHOT.jar dynamo.sensordata-0.0.5-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","dynamo.sensordata-0.0.5-SNAPSHOT.jar"]
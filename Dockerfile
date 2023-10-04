
FROM amazoncorretto:21-al2023-jdk
MAINTAINER chris@weewelchie.org
ARG BUILD_ARTIFACT=dynamo.sensordata-0.0.7-SNAPSHOT
ARG SPRING-PROFILE=DEV

CMD RUN ECHO ${BUILD_ARTIFACT}
CMD RUN ECHO ${SPRING-PROFILE}

COPY target/${BUILD_ARTIFACT}.jar ${BUILD_ARTIFACT}.jar
ENTRYPOINT ["java","-jar","dynamo.sensordata-0.0.7-SNAPSHOT.jar","--spring.profiles.active=PROD"]
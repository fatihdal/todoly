FROM openjdk:8-alpine
WORKDIR /usr/app
ARG DEPENDENCY=build
COPY ${DEPENDENCY}/libs/*.jar /usr/app
COPY ${DEPENDENCY}/resources/* /usr/app
CMD  ["java","-jar","todoly-0.0.1-SNAPSHOT.jar"]
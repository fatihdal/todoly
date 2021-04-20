FROM openjdk:8-alpine
COPY . /root/
WORKDIR /root
RUN rm -rf build
RUN ./gradlew clean build
CMD ["./gradlew","run"]
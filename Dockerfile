FROM java:8
COPY . /root/
WORKDIR /root
RUN rm -rf build
RUN ./gradlew clean build
CMD ["./gradlew","run"]
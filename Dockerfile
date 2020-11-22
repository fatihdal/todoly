FROM java:8
COPY . /root/
WORKDIR /root
RUN rm -rf build
RUN mkdir build
RUN javac src/main/java/dal/fatih/todoly/*.java -d build
CMD ["java","-cp", "build", "dal.fatih.todoly.App"]
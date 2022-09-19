FROM openjdk:11 as rabbitmq

ARG JAR_FILE=target/tweet-app-0.0.1-SNAPSHOT.jar 

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
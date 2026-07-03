FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/connect-hub.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","app.jar"]
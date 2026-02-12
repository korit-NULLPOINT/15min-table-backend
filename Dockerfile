FROM eclipse-temurin:17-jdk
ARG JAR_FILE=target/*-SNAPSHOT.jar
WORKDIR /app
COPY ${JAR_FILE} /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
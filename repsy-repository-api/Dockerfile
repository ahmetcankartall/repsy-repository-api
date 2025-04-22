FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

# Repsy Docker Registry için etiketleme bilgisi
# Docker build sırasında: docker build -t repsy.io/ahmetkrtl/repsy-repository-api:{versiyon} . 
FROM openjdk:11-jdk-slim
WORKDIR /app
COPY . /app
RUN ./gradlew :api:bootJar
EXPOSE 8080
CMD java $JAVA_OPTS -jar api/build/libs/api.jar

FROM openjdk:11-jdk-slim
WORKDIR /app
COPY . /app
RUN ./gradlew bootJar
EXPOSE 8080
CMD java -jar build/libs/snuttev-0.0.1.jar

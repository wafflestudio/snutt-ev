FROM eclipse-temurin:21-alpine
WORKDIR /app
COPY . /app
RUN --mount=type=secret,id=github_token GITHUB_TOKEN=$(cat /run/secrets/github_token) ./gradlew :api:bootJar
EXPOSE 8080
ENTRYPOINT java $JAVA_OPTS -jar api/build/libs/snuttev-api.jar

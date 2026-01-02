FROM gradle:8.8-jdk21 AS build
WORKDIR /workspace
COPY . .
RUN gradle clean bootJar -x test

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

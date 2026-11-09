FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/fantasy-distributed-1.0-SNAPSHOT.jar /app/game.jar
ENTRYPOINT ["java","-jar","/app/game.jar","--console"]

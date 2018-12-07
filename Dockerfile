FROM maven:3-jdk-12-alpine

COPY pom.xml .
COPY src/ src/
RUN mvn compile assembly:single

FROM openjdk:11-jre-slim
COPY --from=0 target/xcat-app-jar-with-dependencies.jar xcat-app.jar
COPY database.xml .

CMD [ "java", "-jar", "xcat-app.jar" ]

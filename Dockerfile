FROM maven:3-jdk-12-alpine

COPY pom.xml .
COPY src/ src/
RUN mvn compile assembly:single -q

FROM openjdk:11-jre-slim
RUN groupadd -r app && useradd --no-log-init -r -g app app
WORKDIR /app
COPY --from=0 --chown=app:app target/xcat-app-jar-with-dependencies.jar xcat-app.jar
COPY --chown=app:app . .
USER app:app

EXPOSE 4567

CMD [ "java", "-jar", "xcat-app.jar" ]

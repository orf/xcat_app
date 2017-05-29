# XCat example app

This is an example app that is vulnerable to xpath injection attacks.

Requires jdk8

### Build:

`mvn clean compile assembly:single`

### Run:

`java -jar target/xcat-app-jar-with-dependencies.jar`

### Hide xpath queries log:

Add `-Dorg.slf4j.simpleLogger.defaultLogLevel=warn` to the `java` cmd.
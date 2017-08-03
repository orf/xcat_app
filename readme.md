# XCat example app

[![Snap Status](https://build.snapcraft.io/badge/orf/xcat_app.svg)](https://build.snapcraft.io/user/orf/xcat_app)

This is an example app that is vulnerable to xpath injection attacks.

### Install

If you are using Linux you can use `snap` to install a completely sandboxed working version in a single command:

`snap install xcat-example-app`

Then start the server with the command `xcat-example-app`.

### Build:

Requires jdk8

`mvn clean compile assembly:single`

### Run:

`java -jar target/xcat-app-jar-with-dependencies.jar`

### Hide xpath queries log:

Add `-Dorg.slf4j.simpleLogger.defaultLogLevel=warn` to the `java` cmd.

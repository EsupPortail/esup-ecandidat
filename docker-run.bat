@echo off
docker run -p 8080:8080 -v /c/tmp/:/app/ -e JAVA_OPTS="-Dconfig.location=/app/config/application.properties" ecandidat:2.5.0
@echo off
docker run -p 8080:8080 -v /c/tmp/:/app/ -e JAVA_OPTS="-Dapp.home=/app/config/" ecandidat:2.5.0
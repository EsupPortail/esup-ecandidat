FROM tomcat:8.5.81-jre11-openjdk-slim-buster
COPY /target/ecandidat-2.5.0/ /usr/local/tomcat/webapps/ROOT
EXPOSE 8080
CMD ["catalina.sh", "run"]
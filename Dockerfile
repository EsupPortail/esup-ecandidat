FROM tomcat:8.5.81-jre11-openjdk-slim-buster
COPY /target/ecandidat-2.3.14/ /usr/local/tomcat/webapps/ecandidat
EXPOSE 8080
CMD ["catalina.sh", "run"]
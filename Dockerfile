FROM tomcat:8.5.82-jre11-openjdk-slim
COPY /target/ecandidat-*.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]
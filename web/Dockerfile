FROM openjdk:13-jdk-alpine
COPY web/target/web-*.jar search-api.jar
EXPOSE 8080
CMD java ${JAVA_OPTS} -jar search-api.jar
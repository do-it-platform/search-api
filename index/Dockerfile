FROM openjdk:13-jdk-alpine
COPY index/target/index-*.jar search-index.jar
EXPOSE 8080
CMD java ${JAVA_OPTS} -jar search-index.jar
FROM openjdk:17
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

RUN curl -o '/opt/elastic-apm-agent.jar' -L 'https://oss.sonatype.org/service/local/artifact/maven/redirect?r=releases&g=co.elastic.apm&a=elastic-apm-agent&v=LATEST'

ENTRYPOINT ["java", "-javaagent:/opt/elastic-apm-agent.jar", "-jar", "/app.jar"]
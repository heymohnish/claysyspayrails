FROM amazoncorretto:17-alpine-jdk
VOLUME /d/tmp
ARG JAR_FILE
ADD ${JAR_FILE} app.jar
EXPOSE 9090
USER root
COPY lib/dd-java-agent.jar $JAVA_HOME/lib
COPY lib/env.sh /usr/local/bin
ENV SERVICE_NAME {serviceName}
ENV SERVICE_VERSION {serviceVersion}
ENV CU ALL
ENV JAVA_OPTS="-XX:InitialRAMPercentage=40.0 -XX:MaxRAMPercentage=55.0 -Djava.security.egd=file:/dev/./urandom"
RUN apk --no-cache add curl
ENTRYPOINT ["/bin/sh", "-c" , "chmod 755 /usr/local/bin/env.sh && . /usr/local/bin/env.sh && exec java $JAVA_AGENT $JAVA_OPTS -jar /app.jar"]
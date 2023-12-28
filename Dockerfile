FROM eclipse-temurin:17
EXPOSE 4001
RUN mkdir /mirrorlog
COPY target/mirrorlog-*.jar /mirrorlog/mirrorlog.jar
WORKDIR /mirrorlog
ENTRYPOINT ["java", "-jar", "/mirrorlog/mirrorlog.jar"]
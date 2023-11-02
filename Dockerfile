FROM eclipse-temurin:17
EXPOSE 4357
RUN mkdir /mirrorlog
RUN mkdir /mirrorlog/config
COPY target/mirrorlog-*.jar /mirrorlog/mirrorlog.jar
COPY config/mirrorlog.conf.yml /mirrorlog/config/mirrorlog.conf.yml
WORKDIR /mirrorlog
ENTRYPOINT ["java", "-jar", "/mirrorlog/mirrorlog.jar"]
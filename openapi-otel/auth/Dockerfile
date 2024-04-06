FROM openjdk:11

ARG TIMEZONE="Asia/Seoul"

ARG DEBIAN_FROMTEND=noninteracive
ENV TZ=Asia/Seoul
RUN apt-get install -y tzdata
RUN cp /usr/share/zoneinfo/${TIMEZONE} /etc/localtime

RUN useradd -ms /bin/bash -u 1000 wbl

RUN mkdir -p /auth/run
RUN mkdir -p /auth/logs
RUN mkdir -p /auth/db
COPY auth-0.0.1.jar /app.jar
RUN chown -R wbl:wbl /auth
USER wbl

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","-Dspring.config.location=file:/auth/application.yml","-Duser.timezone=Asia/Seoul","/app.jar"]
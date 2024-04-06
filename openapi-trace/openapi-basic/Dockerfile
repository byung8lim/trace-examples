FROM openjdk:11

ARG TIMEZONE="Asia/Seoul"

ARG DEBIAN_FROMTEND=noninteracive
ENV TZ=Asia/Seoul
RUN apt-get install -y tzdata
RUN cp /usr/share/zoneinfo/${TIMEZONE} /etc/localtime

RUN useradd -ms /bin/bash -u 1001 wbl

RUN mkdir -p /openapi-basic/run
RUN mkdir -p /openapi-basic/logs
COPY openapi-basic-0.0.1.jar /app.jar
RUN chown -R wbl:wbl /openapi-basic
USER wbl

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","-Duser.timezone=Asia/Seoul","/app.jar"]

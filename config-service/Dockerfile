FROM java:8-jre
MAINTAINER Chris Turner <peavers@gmail.com>

ADD ./build/libs/config-service-1.0.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/config-service-1.0.jar"]

EXPOSE 8888

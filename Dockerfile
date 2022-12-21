FROM adoptopenjdk/openjdk11:alpine-jre

EXPOSE 8000

COPY target/cloud-storage-0.0.1-SNAPSHOT.jar app.jar

COPY logging/file.log.lck logging/file.log.lck

CMD ["java", "-jar", "app.jar"]
FROM amazoncorretto:17

WORKDIR /app

COPY ssutoday-0.0.1-SNAPSHOT.jar /app/

EXPOSE 80

ENTRYPOINT ["java","-jar","-Duser.timezone=Asia/Seoul","ssutoday-0.0.1-SNAPSHOT.jar","-Xmx4096m"]
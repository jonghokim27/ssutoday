FROM amazoncorretto:17

WORKDIR /app

COPY ssutoday-2.1.0-BETA.jar /app/

EXPOSE 80

ENTRYPOINT ["java","-jar","-Duser.timezone=Asia/Seoul","ssutoday-2.1.0-BETA.jar","-Xmx4096m"]
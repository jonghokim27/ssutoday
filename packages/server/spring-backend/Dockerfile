FROM amazoncorretto:17

WORKDIR /app

COPY ssutoday-2.2.0-RELEASE.jar /app/

EXPOSE 80

ENTRYPOINT ["java","-jar","-Duser.timezone=Asia/Seoul","ssutoday-2.2.0-RELEASE.jar","-Xmx4096m"]
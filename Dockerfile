FROM openjdk:8
EXPOSE 8080
ADD ./build/libs/challenge-*.jar /challenge.jar
ENTRYPOINT ["java", "-jar", "/challenge.jar"]
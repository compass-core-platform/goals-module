FROM openjdk:11
COPY goals-0.0.1-SNAPSHOT.jar /opt/
EXPOSE 8080
CMD ["java", "-jar", "/opt/goals-0.0.1-SNAPSHOT.jar"]

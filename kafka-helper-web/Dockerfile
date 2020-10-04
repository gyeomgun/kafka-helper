# Start with a base image containing Java runtime
FROM java:8

# Add Author info
LABEL maintainer="interhg@gmail.com"

# Add a volume to /tmp
VOLUME /tmp

# Make port 8081 available to the world outside this container
EXPOSE 8081

# The application's jar file
ARG JAR_FILE=build/libs/kafka-helper-web-1.0.0-SNAPSHOT.jar


# Add the application's jar to the container
ADD ${JAR_FILE} kafka-helper-web.jar

# Run the jar file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/kafka-helper-web.jar"]

FROM maven:3-openjdk-18-slim
EXPOSE 8080
WORKDIR /app
COPY ./ /app
RUN mvn clean package -DskipTests
ENTRYPOINT java -jar /app/target/*.jar
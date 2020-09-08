FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

ENV DB_URL=jdbc:mysql://ecs-training-test.cigyq6elyaqt.eu-west-1.rds.amazonaws.com:3306/ingredients
ENV DB_USER=admin
ENV DB_PASSWORD=kurosaki99


##Deploy etc with envaga loll reload new DB
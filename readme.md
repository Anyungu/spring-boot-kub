## Introduction
Microservices is a software architectural design where a 'single' application is made up of a collection of different services that can be deployed independently. Though complex to initially set up, it is my general opinion that microservices offer benefits that outweigh this initial complexity.

In this article we will describe a very simple apprach to start your kubernetes project.

### Why you should use microservices
1. Multiple points of failure
2. Independent service scalability
3. Right tool for each service
4. Easier service independent debugging
5. Easy continuous delivery

### Prerequisites
1. [Docker hub](https://hub.docker.com/) account
2. Java 8+
3. [AWS Free tier account](https://aws.amazon.com/)

## Getting Started
Install [Java SDK](https://www.java.com/en/)
Install [Maven](https://maven.apache.org/)
Install [Visual Studio Code IDE](https://code.visualstudio.com/download)
Set up a [MYSQL DB Instance in AWS](https://aws.amazon.com/getting-started/hands-on/create-mysql-db/) and ensure to use the cheaper t2.micro instance and enable public access. 

Ensure to set up the PATH environment variable and install the following VS code extensions: Spring Boot tools, Java extension pack, Maven for Java, Spring Boot Dashboard, Spring Boot Extension Pack

### Folder structure
Create a folder for your project with the following structure

```bash
├── .github
│   └── workflows
│       ├── depl-ingredients.yaml
│       ├── depl-manifests.yaml
│       └── depl-recipe.yaml
├── infra
│   └── k8s
│       ├── apps
│       │   ├── depl-ingredients.yaml
│       │   └── depl-recipe.yaml
│       └── ingress
│           └── depl-ingress.yaml
├── ingredients
└── recipe
```
    
The workflows folder will contain Github actions for deployment.
The infra folder will contain instructions for setting up the different Kubernetes resources.
The ingredients and the recipe will contain the microservices.

### Creating the SpringBoot project
Head over to [Spring Initialzr](https://start.spring.io/) and Generate 2 projects i.e recipe and ingredients with the following options: 
+ Language: Java
+ Project: Maven project
+ Spring Boot: 2.3.3
+ Packaging: JAR
+ Java: 8

Extract the genarated zipped files into the ingredients and recipe folders respectively

## The Ingredients microservice

In the ingredients folder add the following files and folders

```bash
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── chef
│   │   │           └── ingredients
│   │   │               ├── controllers
│   │   │               │   └── IngredientsController.java
│   │   │               ├── exceptions
│   │   │               │   ├── CustomException.java
│   │   │               │   └── CustomizedResponseEntityExceptionHandler.java
│   │   │               ├── IngredientsApplication.java
│   │   │               ├── models
│   │   │               │   └── Ingredients.java
│   │   │               ├── repos
│   │   │               │   └── IngredientsRepos.java
│   │   │               ├── requests
│   │   │               │   └── IngredientsRequest.java
│   │   │               ├── responses
│   │   │               │   ├── GeneralResponse.java
│   │   │               │   └── StatusResponse.java
│   │   │               └── services
│   │   │                   └── IngredientsService.java
│   │   └── resources
│   │       └── application.properties
│   └── test
│       └── java
│           └── com
│               └── chef
│                   └── ingredients
│                       └── IngredientsApplicationTests.java
```


## Required Dependencies

In the pom.xml add the following dependencies

```xml
// To enable Rest
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

// For Swagger Documentation
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-ui</artifactId>
    <version>1.2.32</version>
</dependency>

// To Enable MySQL Data
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

//To connect to MySQL
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
</dependency>

```

## Connection to the database

In the application.properties files add the below configuration properties.
We shall store some of these variables in the environment later on

```bash

spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}

# Enable Auto Updating of new tables and table columns
spring.jpa.hibernate.ddl-auto=update

# Specify the jdbc driver
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

#Direct hibernate to work with mysql 8
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQL8Dialect

#Configure Table and Column Naming strategies
spring.jpa.hibernate.naming.implicit-strategy=org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl


```

## Create the models

Spring Boot uses Entities to map a class to an Entity in the Database, in this case a table. Spring Boot will automatically create these tables and map these attributes to columns in the tables based on the @Entity, @Id, @Column annotations.

The GenerationType.IDENTITY is used to instruct the primary key to be table specific rather than project wide.

Create the Ingredients class in the Ingredients.java file as shown below using setters and getters to acheive encapsulation.

```java

package com.chef.ingredients.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "ingredient")
public class Ingredients {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

```

## Set up the Repositories

Repositories are Spring Boot's way of providing interfaces between the business logic and the database entities to enable simple and complex CRUD operations to the database.

Extend the JPA Repository to expose a set of relevant methods that will in the background generate relevant mysql queries as shown below.

Remember to pass in the model class name and the data type of the primary key field


```java

package com.chef.ingredients.repos;

import com.chef.ingredients.models.Ingredients;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientsRepos extends JpaRepository<Ingredients, Integer> {

}

```

## Configure Responses

To achieve uniformity in our responses, create 2 files responses/GeneralResponse.java and responses/StatusResponse.java as shown below.

The GeneralResponse class will have a dynamic data attribute to help it accomodate any type of data expected in a get method response.

The StatusResponse class will be focuses on responses that do not need to return data.

```java

package com.chef.ingredients.responses;

import java.time.LocalDateTime;

public class StatusResponse {

    private LocalDateTime time;
    private String message;
    private Integer code;

    public StatusResponse() {
        super();
    }

    public StatusResponse(LocalDateTime time, String message, Integer code) {
        super();
        this.time = time;
        this.message = message;
        this.code = code;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "StatusResponse [time=" + time + ", message=" + message + ", code=" + code + "]";
    }
}

```


```java

package com.chef.ingredients.responses;

import java.time.LocalDateTime;

public class GeneralResponse<T> {

    private LocalDateTime time;
    private String message;
    private Integer code;
    private T data;

    public GeneralResponse() {
        super();
    }

    public GeneralResponse(LocalDateTime time, String message, Integer code, T data) {
        super();
        this.time = time;
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "GeneralResponse [time=" + time + ", message=" + message + ", code=" + code + ", data=" + data + "]";
    }

}

```

## Configure Requests

It is not advisable to use Model classes in  making http requests since they may have fields such as dates etc.

To avoid this define a request class in the requests/IngredientsRequest.java as shown

```java

package com.chef.ingredients.requests;

public class IngredientsRequest {

    private String email;

    private String name;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}


```

## Customize Exceptions

Customize Exceptions by using the classes below in the exceptions folder.

This should be done to give Exceptions message flexibility.


```java
package com.chef.ingredients.exceptions;

@SuppressWarnings("serial")
public class CustomException extends RuntimeException {

    private Integer code;

    private String message;

    public CustomException(Integer code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}

```

Use this class to register CustomException as a valid project exception.

```java
package com.chef.ingredients.exceptions;

import java.time.LocalDateTime;

import com.chef.ingredients.responses.StatusResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<StatusResponse> handleExceptions(Exception ex, WebRequest request) {
        StatusResponse statusResponse = new StatusResponse(LocalDateTime.now().plusHours(3), ex.getMessage(), 500);
        return new ResponseEntity<>(statusResponse, new HttpHeaders(), HttpStatus.OK);
    }

    @ExceptionHandler(CustomException.class)
    public final ResponseEntity<StatusResponse> customExceptionHandling(CustomException ex,
            WebRequest request) {
        StatusResponse statusResponse = new StatusResponse(LocalDateTime.now().plusHours(3), ex.getMessage(),
                ex.getCode());
        return new ResponseEntity<>(statusResponse, new HttpHeaders(), HttpStatus.OK);
    }

}
```

## Create the business logic

With respect to MVC, separate the business logic into the services folder. 
Define the IngredientsService.java as shown below.

The @Autowired annotation is used to inject the Ingredients Repository as a dependency to the service class.
Repository Methods can then be accessed using the injected dependency.


```java

package com.chef.ingredients.services;

import java.util.List;
import java.util.Optional;

import com.chef.ingredients.exceptions.CustomException;
import com.chef.ingredients.models.Ingredients;
import com.chef.ingredients.repos.IngredientsRepos;
import com.chef.ingredients.requests.IngredientsRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IngredientsService {

    @Autowired
    private IngredientsRepos ingredientsRepos;

    public void createIngredient(IngredientsRequest ingredientsRequest) throws Exception {

        Ingredients ing = new Ingredients();
        ing.setEmail(ingredientsRequest.getEmail());
        ing.setName(ingredientsRequest.getName());

        ingredientsRepos.save(ing);

    }

    public void updateIngredient(Ingredients ingredients) throws Exception {

        Optional<Ingredients> findById = ingredientsRepos.findById(ingredients.getId());

        if (!findById.isPresent()) {
            throw new CustomException(401, "Not Found");
        }

        Ingredients ingredientsNew = findById.get();
        ingredientsNew.setEmail(ingredients.getEmail());
        ingredientsNew.setName(ingredients.getName());

        ingredientsRepos.save(ingredientsNew);

    }

    public void deleteIngredient(Integer id) throws Exception {

        Optional<Ingredients> findById = ingredientsRepos.findById(id);

        if (!findById.isPresent()) {
            throw new CustomException(401, "Not Found");
        }

        ingredientsRepos.deleteById(id);

    }

    public Ingredients getIngredient(Integer id) throws Exception {

        Optional<Ingredients> findById = ingredientsRepos.findById(id);

        if (!findById.isPresent()) {
            throw new CustomException(401, "Not Found");
        }

        return findById.get();
    }

    public List<Ingredients> getAllIngredient() throws Exception {

        List<Ingredients> findAll = ingredientsRepos.findAll();

        if (findAll.isEmpty()) {
            throw new CustomException(401, "Not Found");
        }

        return findAll;
    }

}


```


## Set up the controllers

The Rest controller defined by the @RestController is used to define the Rest endpoints that will be used to access the Rest API.

The @Crossorigin annotation is used to define cors.

Define it as shown below: 

```java

package com.chef.ingredients.controllers;

import java.time.LocalDateTime;
import java.util.List;

import com.chef.ingredients.models.Ingredients;
import com.chef.ingredients.requests.IngredientsRequest;
import com.chef.ingredients.responses.GeneralResponse;
import com.chef.ingredients.responses.StatusResponse;
import com.chef.ingredients.services.IngredientsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/ingredients/v1")
public class IngredientsController {

    @Autowired
    private IngredientsService ingredientsService;

    @PostMapping(path = "/create")
    public StatusResponse createIngredient(@RequestBody IngredientsRequest ingredientsRequest) throws Exception {

        ingredientsService.createIngredient(ingredientsRequest);

        StatusResponse stat = new StatusResponse(LocalDateTime.now().plusHours(3), "Created", 200);
        return stat;
    }

    @PutMapping(path = "/update")
    public StatusResponse updateIngredient(@RequestBody Ingredients ingredients) throws Exception {

        ingredientsService.updateIngredient(ingredients);

        StatusResponse stat = new StatusResponse(LocalDateTime.now().plusHours(3), "updated", 200);
        return stat;
    }

    @DeleteMapping(path = "/delete")
    public StatusResponse deleteIngredient(Integer id) throws Exception {

        ingredientsService.deleteIngredient(id);

        StatusResponse stat = new StatusResponse(LocalDateTime.now().plusHours(3), "deleted", 200);
        return stat;
    }

    @GetMapping(path = "/read")
    public GeneralResponse<Ingredients> readIngredient(Integer id) throws Exception {

        Ingredients ingredient = ingredientsService.getIngredient(id);

        GeneralResponse<Ingredients> gen = new GeneralResponse<Ingredients>(LocalDateTime.now().plusHours(3), "Found",
                200, ingredient);
        return gen;
    }

    @GetMapping(path = "/readAll")
    public GeneralResponse<List<Ingredients>> readAllIngredient() throws Exception {

        List<Ingredients> ingredient = ingredientsService.getAllIngredient();

        GeneralResponse<List<Ingredients>> gen = new GeneralResponse<List<Ingredients>>(
                LocalDateTime.now().plusHours(3), "Found", 200, ingredient);
        return gen;
    }

}

```

## Document the API

Use [springdoc-openapi](https://springdoc.org/) to autogenerate Swagger Open-api documentation to autogenerate REST Documentation and Any Data Models within the projects.

Ensure you added these dependencies in the pom.xml file

```xml

<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-ui</artifactId>
    <version>1.2.32</version>
</dependency>

```

## Run your Project

Set up your environment variables i.e the DB_URL, DB_USER, DB_PASSWORD locally.

While in the ingredients directory, use the following command to run your project:

```bash
mvn spring-boot:run

```

The project should run successfully.

## Dockerfile

Set up your dockerfile as shown below with the correct environment variables.

```bash
FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

ENV DB_URL=jdbc:mysql://<host>:3306/ingredients
ENV DB_USER=<name>
ENV DB_PASSWORD=<passs>


```

## Create your Kubernetes cluster

Head over to [AWS EKS]()

## Set up github

Besides Repositories, we shall use github actions to automatically deploy our project to an existing cluster.

Create a Github Repository.
Ensure you have an active dockerhub accounts

Navigate to the settings tab and create the following secrets:

+ AWS_ACCESS_KEY_ID
+ AWS_SECRET_ACCESS_KEY
+ CLUSTER_NAME
+ REGION_CODE
+ DOCKER_PASSWORD
+ DOCKER_USERNAME
+ DB_USER
+ DB_PASSWORD
+ DB_URL

## Create the kubernetes Deployment and Service Definition

The kuberentes architecture is made up of 
1. pod - An instance of the service
2. Deployment - collection of Pods
3. service - Define a deployment/pod as a service to enable communication.

Define the deployment and its corresponding service in the infra/k8s/apps/depl-ingredients.yaml file as shown below.


```yaml

apiVersion: apps/v1

kind: Deployment

metadata:
  name: ing
  labels:
    app: ing

spec:
  replicas: 2

  selector:
    matchLabels:
      app: ing

  template:
    metadata:
      labels:
        app: ing

    spec:
      containers:
        - name: ing
          image: anyungu/ing
          ports:
            - containerPort: 8080
      serviceAccountName: ing

---
apiVersion: v1
kind: Service
metadata:
  name: ing-srv

spec:
  type: NodePort
  selector:
    app: ing
  ports:
    - port: 80
      name: ing
      targetPort: 8080


```

## Define the Kubernetes ingress

Use the Nginx ingress controller to direct traffic:

Select the cloud appropriate command from this [link](https://kubernetes.github.io/ingress-nginx/deploy). This will be applied in our cluster.

Define rules for the ingress as shown below

```yaml

apiVersion: networking.k8s.io/v1beta1
kind: Ingress
metadata:
  name: k8s-learn-ingress
  annotations:
    kubernetes.io/ingress.class: nginx
    nginx.ingress.kubernetes.io/use-regex: "true"
spec:
  # tls:
  #   - hosts:
  #       - k8s24.lanthanion.com
  #     secretName: k8s-learn-tls
  rules:
    #  - host: k8s24.lanthanion.com
    - http:
        paths:
          - path: /ingredients/?(.*)
            backend:
              serviceName: ing
              servicePort: 80


```


## Github actions

### Github actions for deploying kubernetes manifestss

In the .github/workflows/depl-manifests file define the following workflow. 
This will be used to apply the kuberenetes yaml configs that we have written.

```yaml

name: depl-manifests

on:
  push:
    branches:
      - master
    paths:
      - infra/k8s/**

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v2

      - name: Configure AWS Credentials
        uses: aws-actions/configure-aws-credentials@v1
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: eu-west-1

      - name: Set up kubernetes context
        run: aws eks --region ${{ secrets.REGION_CODE }} update-kubeconfig --name ${{ secrets.CLUSTER_NAME }}

      - name: set up the Ingress controller
        run: kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v0.35.0/deploy/static/provider/aws/deploy.yaml

      - name: Delete webhook validation
        run: kubectl delete -A ValidatingWebhookConfiguration ingress-nginx-admission

      - name: Run manifests
        run: kubectl apply -f infra/k8s/ --recursive --kubeconfig /home/runner/.kube/config


```

### Github actions for redeploying the ingredients service

In the .github/workflows/depl-manifests file define the following workflow. 
This will be used to apply the kuberenetes yaml configs that we have written.

```yaml

name: depl-ingredients

on:
  push:
    branches:
      - master
    paths:
      - ingredients/**

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v2

      - name: Set up JDK 8
        uses: actions/setup-java@v1
        with:
          java-version: 8

      - name: Maven Package
        run: |
          cd ingredients
          mvn -B clean package -DskipTests

      - name: Build and push Docker images
        uses: docker/build-push-action@v1
        with:
          path: ingredients
          username: ${{ secrets.DOCKER_USERNAME }}
          password: ${{ secrets.DOCKER_PASSWORD }}
          repository: anyungu/ing
          tags: latest
        env:
          DB_URL: ${{secrets.DB_URL}}
          DB_USER: ${{secrets.DB_USER}}
          DB_PASSWORD: ${{secrets.DB_PASSWORD}}

      - name: Configure AWS Credentials
        uses: aws-actions/configure-aws-credentials@v1
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: eu-west-1

      - name: Set up kubernetes context
        run: aws eks --region ${{ secrets.REGION_CODE }} update-kubeconfig --name ${{ secrets.CLUSTER_NAME }}

      - name: Deploy
        run: |
          kubectl rollout restart deployment/ing

```

## Deployment and testing

### AWS CLI and kubectl

kubectl is the tool used to connect to a kubernetes context.

Download and install [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2-linux.html#cliv2-linux-install)

Configure AWS with command

```bash
 aws configure

```

Download and install [Kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/) and set up the EKS cluster context using this command

```bash
aws eks --region region-code update-kubeconfig --name cluster_name

```

You can now run apply, debug and delete commands to your cluster such as

```bash

kubectl get svc

kubectl get deployments

kubectl get pods

kubectl logs <pod-id>

kubectl delete pods --all --all-namespace

kubectl apply -f <path to yaml file>

```

### Test with Postman

Under the AWS EC2 Service, In the Load Balancer, you will find the Network load balancer created by ingress.

Use the provided DNS Name as the Gateway URL to your cluster.

You can send requests to your notifications service


## Conclusion and Remarks

This is a minimalist Microservices set up with kubernetes.

This Example is focused on AWS, however this can also be set up with other cloud providers.

In a production environment, more security considerations should be put in place.
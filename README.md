"In-memory data grids and caching technologies" course Capstone Project

![Java CI with Gradle](https://github.com/kprokopchik/ignite-cassandra-demo/workflows/Java%20CI%20with%20Gradle/badge.svg?branch=master)
 
# Setup local environment

## Requirements
* Docker
* JDK 11
* Available TCP ports (see description below)

## Used TCP ports
The following ports should be unused so applications could bind web services:
* `8080`: Demo Application
* `8181`, `8281`, `8381`: Apache Ignite Spring Boot with exposed Actuator endpoints
* `8182`, `8282`, `8382`: Apache Ignite REST API

## How to build and run
Run the following script to build project and run docker containers
```
./compose.sh
```
There will be created few docker containers:
* Cassandra
* Apache Ignite
* Demo Application

## Endpoints

### Apache Ignite
* http://localhost:8181/actuator/info - display cache stats
* http://localhost:8182/ignite?cmd=version - Apache Ignite [REST API][Ignite REST API]

### Demo Application
* Product REST API `http://localhost:8080/product/{product_id}?[useCache=true|false]`

Example:
http://localhost:8080/product/b6c0b6be-a69c-7229-3958-5baeac73c13d?useCache=true

Notice: you can use Swagger UI http://localhost:8080/swagger-ui.html

[Ignite REST API]: https://ignite.apache.org/docs/latest/restapi
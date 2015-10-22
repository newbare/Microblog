# Microblog

Microblog is a simple microblogging web application written in Java with Spring MVC.
This app uses the following frameworks/technologies:
* **Spring MVC 4.2.1**
* **Spring Security 4.0.2**
* **Spring Data JPA 1.8.2**
* **PostgreSQL 9.4**
* **Hibernate Validator 5.2.1**
* **Thymeleaf 2.1.5**
* **Twitter Bootstrap 3**
* **JUnit 4.11**
* **Mockito 1.10.19**

## How to build

The following tools are required:
* **JDK 1.8 or later**
* **Apache Maven**
* **Apache Tomcat or other servlet container**

All Spring configuration classes are located in `me.molchanoff.microblog.config` package.
You can change database connection options and access credentials in `RootConfig` class.

To build this app you have to run this in your console:
```
mvn package
```
After the build was successful, you should see a `Microblog.war` file inside newly created `target` subdirectory.
In order to deploy this app, you have to refer corresponding documentation of your servlet container/application server.

**Note:** This app uses Spring profiles for database connection:
* default - uses embedded H2 database
* production - uses PostgreSQL database
* test - uses embedded H2 database with test data `testdata.sql` script (for unit tests)

The active profile is specified in `spring.profiles.active` property in `config.properties`.
If no environment variable was specified, `default` profile is used.
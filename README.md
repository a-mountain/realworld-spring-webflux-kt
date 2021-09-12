# ![RealWorld Example App](spring-logo.png)

> ### Spring boot + WebFlux codebase containing real world examples (CRUD, auth, advanced patterns, etc) that adheres to the [RealWorld](https://github.com/gothinkster/realworld) spec and API.


This codebase was created to demonstrate a fully fledged fullstack application built with **Spring boot + WebFlux** including CRUD operations, authentication, routing, pagination, and more.

We've gone to great lengths to adhere to the **Spring boot + WebFlux** community styleguides & best practices.

For more information on how to this works with other frontends/backends, head over to the [RealWorld](https://github.com/gothinkster/realworld) repo.


# How it works
It uses Spring Reactive Stack: WebFlux + Spring Data Reactive MongoDB.  
It provides ability to handle concurrency with a small number of threads and scale with fewer hardware resources.  
- [WebFlux](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)  
- [MongoDB Reactive](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mongo.reactive)


## Database
It uses embedded MongoDB database for demonstration purposes.


## Basic approach
The quality & architecture of this Conduit implementation reflect something similar to an early stage startup's MVP: functionally complete & stable, but not unnecessarily over-engineered.


## Project structure
```
- api - web layer which contains enpoints and web specific dto.
- article - contains all features connected with articles.
- exceptions - exceptions and exception handlers.
- lib - helpers, system code
- security - security settings.
- user - contains all features connected with users.
- validation - custom validators and validation settings.
```
## Tests
- All endpoints covered with positive tests.
- Unit tests cover dangerous places (if statements, exceptions throwing...)


# Getting started
You need Java 16 installed.
```
./gradlew bootRun
```

# For Java Developers

It's [Java Version](https://github.com/a-mountain/realworld-spring-webflux) of RealWorld Implementation with same structure, technologies and design approach.  

- (+) Comparing to Java Kotlin Coroutines incredibly simplify and reduce the amount of code. (1409 vs 1009 lines)
- (-) Kotlin is not well supported by Spring as Java that entails some pitfalls and inconvenience.  

## Pitfalls

(Soon)

# ![RealWorld Example App](spring-logo.png)

> ### Spring boot + WebFlux codebase containing real world examples (CRUD, auth, advanced patterns, etc) that adheres to the [RealWorld](https://github.com/gothinkster/realworld) spec and API.


This codebase was created to demonstrate a fully fledged fullstack application built with **Spring boot + WebFlux**
including CRUD operations, authentication, routing, pagination, and more.

We've gone to great lengths to adhere to the **Spring boot + WebFlux** community styleguides & best practices.

For more information on how to this works with other frontends/backends, head over to
the [RealWorld](https://github.com/gothinkster/realworld) repo.

# How it works

It uses Spring Reactive Stack: WebFlux + Spring Data Reactive MongoDB.  
It provides ability to handle concurrency with a small number of threads and scale with fewer hardware resources.

- [WebFlux](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
- [MongoDB Reactive](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mongo.reactive)

## Database

It uses embedded MongoDB database for demonstration purposes.

## Basic approach

The quality & architecture of this Conduit implementation reflect something similar to an early stage startup's MVP:
functionally complete & stable, but not unnecessarily over-engineered.

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

It's [Java Version](https://github.com/a-mountain/realworld-spring-webflux) of RealWorld Implementation with same
structure, technologies and design approach.

- (+) Comparing to Java Kotlin Coroutines incredibly simplify and reduce the amount of code. (1404 vs 1009 lines)
- (-) Kotlin is not well supported by Spring as Java that entails some pitfalls and inconvenience.

# Pitfalls and features

### Default methods don't work.

- If default method name matches query pattern it will be replaced by default implementation.
- If default method name doesn't match query pattern QueryCreationException occurs.

To fix this use Extension methods:

```kotlin
// FAIL
interface PersonRepository : ReactiveMongoRepository<Person, String> {
    fun findByName(firstName: String): Mono<Person>
    fun findByNameOrFail(firstName: String): Mono<Person> = findByName(firstName)...
}

// OK
interface PersonRepository : ReactiveMongoRepository<Person, String> {
    fun findByName(firstName: String): Mono<Person>
}

fun PersonRepository.findByNameOrFail(firstName: String): Mono<Person> = findByName(firstName)...
```

### Suspend modifier isn`t supported

You still need to use Mono or Flux as return type.

```kotlin
// FAIL
interface PersonRepository : ReactiveMongoRepository<Person, String> {
    suspend fun findByName(firstName: String): Person
}

// OK
interface PersonRepository : ReactiveMongoRepository<Person, String> {
    fun findByName(firstName: String): Mono<Person>
}
```

### Property without backing field is not persisted in MongoDB

- First invocation of such method occurs exception.
- IDEA shows it as suggestion when you write method in spring data repository.


```kotlin
class Person(
    @Field("comments") private val _comments: MutableList<String> = ArrayList()
) {
    // it is not persisted, so you don't need to use @Transient annotation here
    val comments: List<String>
        get() = _comments
}
```

### Extension for Criteria

Spring provides utility method `where` that allows to use property reference in criteria, but it doesn't support @Field
annotation

It's better to use constants with field names.

```kotlin
class Person(@Field("full_name") val fullName: String) {
    companion object {
        const val FULL_NAME_FIELD = "full_name"
    }
}

// BAD
fun f1() {
    // Spring utility method
    where(Person::fullName)... // `fullName`
}

// GOOD
fun f1() {
    Criteria.where(Person.FULL_NAME_FIELD)... // `full_name`
}
```

It's also possible to write own version that accounts for @Field annotation

```kotlin
fun <T, V> whereProperty(property: KProperty1<T, V>): Criteria {
    val fieldName = getFieldNameFromFieldAnnotation(property) ?: return where(property)
    return Criteria.where(fieldName)
}

fun <T, V> getFieldNameFromFieldAnnotation(property: KProperty1<T, V>): String? {
    val javaField = property.javaField ?: return null
    val annotation = javaField.getAnnotation(Field::class.java) ?: return null
    return when {
        annotation.value.isNotEmpty() -> annotation.value
        annotation.name.isNotEmpty() -> annotation.name
        else -> null
    }
}

fun f() {
    whereProperty(Person::fullName)... // `full_name`
}
```

### Default parameters in controllers

Spring controller doesn't account for kotlin`s default parameters.

```kotlin
// BAD
@GetMapping
fun hello(@RequestParam name: String = "Maxim") = "Hello $name"

// GOOD
@GetMapping
fun hello(@RequestParam(defaultValue = "Maxim") name: String) = "Hello $name"
```
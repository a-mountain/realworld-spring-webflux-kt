package com.realworld.springmongo.lib

import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.where
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.javaField

/**
 * Creates a Criteria using a KProperty as key.
 * Unlike [org.springframework.data.mongodb.core.query.where] account for `name` from [Field] annotation.
 * Doesn't support nested fields.
 */
fun <T, V> whereProperty(property: KProperty1<T, V>): Criteria {
    val fieldName = getFieldNameFromFieldAnnotation(property) ?: return where(property)
    return Criteria.where(fieldName)
}

/**
 * Creates a Sort using a KProperty as key.
 * Doesn't support nested fields.
 */
fun <T, V> sortBy(property: KProperty1<T, V>): Sort {
    val fieldName = getFieldNameFromFieldAnnotation(property) ?: property.name
    return Sort.by(fieldName)
}

private fun <T, V> getFieldNameFromFieldAnnotation(property: KProperty1<T, V>): String? {
    val javaField = property.javaField ?: return null
    val annotation = javaField.getAnnotation(Field::class.java) ?: return null
    return when {
        annotation.value.isNotEmpty() -> annotation.value
        annotation.name.isNotEmpty() -> annotation.name
        else -> null
    }
}
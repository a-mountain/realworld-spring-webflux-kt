package com.realworld.springmongo.validation

import org.hibernate.validator.internal.constraintvalidators.bv.NotBlankValidator
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [NotBlankOrNullValidator::class])
annotation class NotBlankOrNull(
    val message: String = "{javax.validation.constraints.NotBlank.message}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

private class NotBlankOrNullValidator : ConstraintValidator<NotBlankOrNull, String?> {
    private val notBlankValidator = NotBlankValidator()

    override fun isValid(obj: String?, context: ConstraintValidatorContext): Boolean {
        return if (obj == null) {
            true
        } else {
            notBlankValidator.isValid(obj, context)
        }
    }
}
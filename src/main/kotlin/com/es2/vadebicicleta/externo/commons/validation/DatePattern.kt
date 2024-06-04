package com.es2.vadebicicleta.externo.commons.validation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.reflect.KClass

class DatePatternValidationClass : ConstraintValidator<DatePattern, String> {
    private lateinit var annotation: DatePattern

    override fun initialize(annotation: DatePattern) {
        this.annotation = annotation
    }

    override fun isValid(value: String, p1: ConstraintValidatorContext?): Boolean {
        val dateTimeFormatter =  try {
            DateTimeFormatter.ofPattern(annotation.pattern)
        } catch (e : IllegalArgumentException) {
            throw IllegalArgumentException("Formato data inválido. Consultar DateTimeFormatter.java para referência.")
        }

        return try {
            dateTimeFormatter.parse(value)
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }

}

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [DatePatternValidationClass::class])
@MustBeDocumented
annotation class DatePattern(
    val message: String = "Formato de data inválido.",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = [],
    val pattern: String
)

package org.kanur.microlog.components.validators

import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [FieldsMustMatchValidator::class])
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class FieldsMustMatch(
  val field: String,
  val target: String,
  val message: String = "Fields do not match",
  val groups: Array<KClass<*>> = [],
  val payload: Array<KClass<out Payload>> = []
)

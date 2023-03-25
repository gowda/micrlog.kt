package org.kanur.microlog.components.validators

import org.springframework.beans.BeanWrapperImpl
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class FieldsMustMatchValidator : ConstraintValidator<FieldsMustMatch, Any> {
  private lateinit var field: String
  private lateinit var target: String
  private lateinit var message: String

  override fun initialize(constraintAnnotation: FieldsMustMatch?) {
    if (constraintAnnotation != null) {
      field = constraintAnnotation.field
      target = constraintAnnotation.target
      message = constraintAnnotation.message
    }

    super.initialize(constraintAnnotation)
  }

  override fun isValid(value: Any?, context: ConstraintValidatorContext?): Boolean {
    try {
      val fieldValue = BeanWrapperImpl(value!!).getPropertyValue(field)
      val targetValue = BeanWrapperImpl(value).getPropertyValue(target)

      context?.disableDefaultConstraintViolation()
      context?.buildConstraintViolationWithTemplate(message)?.addPropertyNode(target)?.addConstraintViolation()

      return if (fieldValue == null) {
        targetValue == null
      } else {
        fieldValue.toString() == targetValue.toString()
      }
    } catch (ex: Exception) {
      throw ex
    }
  }
}

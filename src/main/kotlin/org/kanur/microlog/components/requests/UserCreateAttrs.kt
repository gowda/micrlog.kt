package org.kanur.microlog.components.requests

import org.kanur.microlog.components.validators.FieldsMustMatch
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

@FieldsMustMatch(field = "password", target = "passwordConfirmation", message = "Passwords must match")
data class UserCreateAttrs(
  @field:NotBlank(message = "Name cannot be blank")
  var name: String = "",
  @field:NotBlank(message = "Email cannot be blank")
  @field:Email
  var email: String = "",
  @field:NotBlank(message = "Password is required")
  var password: String = "",
  var passwordConfirmation: String = "",
)

package org.kanur.microlog.components.requests

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class UserCreateAttrs(
  @field:NotBlank(message = "Name cannot be blank")
  var name: String = "",
  @field:NotBlank(message = "Email cannot be blank")
  @field:Email
  var email: String = "",
)

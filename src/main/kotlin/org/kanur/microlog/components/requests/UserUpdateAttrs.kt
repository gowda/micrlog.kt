package org.kanur.microlog.components.requests

import javax.validation.constraints.NotBlank

data class UserUpdateAttrs(
  @field:NotBlank(message = "Name cannot be blank")
  var name: String = "",
) {
  fun isBlank(): Boolean {
    return name.isBlank()
  }
}

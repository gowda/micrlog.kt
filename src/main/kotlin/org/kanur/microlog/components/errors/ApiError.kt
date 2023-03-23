package org.kanur.microlog.components.errors

data class ApiError(
  var code: Int,
  var message: String?,
  var trace: MutableMap<String, Any> = mutableMapOf(),
)

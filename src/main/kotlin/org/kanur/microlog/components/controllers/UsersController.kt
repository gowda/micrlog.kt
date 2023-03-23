package org.kanur.microlog.components.controllers

import org.kanur.microlog.components.entities.User
import org.kanur.microlog.components.errors.ApiError
import org.kanur.microlog.components.requests.UserCreateAttrs
import org.kanur.microlog.components.requests.UserUpdateAttrs
import org.kanur.microlog.components.services.UsersService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.sql.SQLIntegrityConstraintViolationException
import javax.validation.Valid

@RestController
@RequestMapping("/users")
class UsersController {
  @Autowired
  private lateinit var service: UsersService

  @GetMapping
  fun index(): List<User> {
    return service.getAll()
  }

  @PostMapping
  fun create(@Valid @RequestBody attrs: UserCreateAttrs): User {
    return service.create(attrs)
  }

  @PutMapping("/{id}")
  fun update(@PathVariable("id") id: Long, @RequestBody attrs: UserUpdateAttrs?): User {
    println("update attrs: $attrs")
    return service.update(id, attrs)
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(NoSuchElementException::class)
  fun handleNoSuchElementException(ex: NoSuchElementException): ApiError {
    return ApiError(code = HttpStatus.NOT_FOUND.value(), message = "Not found")
  }

  @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
  @ExceptionHandler(HttpMessageNotReadableException::class)
  fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException): ApiError {
    return ApiError(code = HttpStatus.UNPROCESSABLE_ENTITY.value(), message = "Request body cannot be blank")
  }

  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  @ExceptionHandler(MethodArgumentNotValidException::class)
  fun handleRequestValidationFailure(ex: MethodArgumentNotValidException): ApiError {
    val errors: MutableMap<String, String> = mutableMapOf()
    ex.bindingResult.allErrors.forEach { error ->
      errors[(error as FieldError).field] = error.defaultMessage ?: "Message not found"
    }

    return ApiError(
      code = HttpStatus.UNPROCESSABLE_ENTITY.value(),
      message = "Validation failed",
      trace = errors as MutableMap<String, Any>
    )
  }

  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  @ExceptionHandler(DataIntegrityViolationException::class)
  fun handleConstraintViolationException(ex: DataIntegrityViolationException): ApiError {
    val rootCause = ex.rootCause as SQLIntegrityConstraintViolationException
    val match =
      Regex("Duplicate entry \'[^\']+\' for key \'users.*email.*\'").matchEntire(rootCause.message.orEmpty())
    return if (match !== null) {
      ApiError(
        code = HttpStatus.UNPROCESSABLE_ENTITY.value(),
        message = "Validation error",
        trace = mutableMapOf("email" to "Already registered")
      )
    } else {
      println("root cause: ${ex.rootCause}")
      ApiError(code = HttpStatus.UNPROCESSABLE_ENTITY.value(), message = "Unrecognized validation error")
    }
  }
}

package org.kanur.microlog.components.services

import org.kanur.microlog.components.entities.User
import org.kanur.microlog.components.repositories.UserRepository
import org.kanur.microlog.components.requests.UserCreateAttrs
import org.kanur.microlog.components.requests.UserUpdateAttrs
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
class UsersService {
  @Autowired
  private lateinit var repository: UserRepository

  fun getOneById(id: Long): User {
    return repository.findById(id).orElseThrow()
  }

  fun getAll(): List<User> {
    return repository.findAll()
  }

  fun create(attrs: UserCreateAttrs): User {
    return repository.save(User.from(attrs))
  }

  fun update(id: Long, attrs: UserUpdateAttrs?): User {
    val user = repository.findById(id).orElseThrow()
    return when (attrs) {
      null -> user
      else -> {
        if (attrs.isBlank()) user else {
          user.name = attrs.name
          repository.save(user)
        }
      }
    }
  }
}

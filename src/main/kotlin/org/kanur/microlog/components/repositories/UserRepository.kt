package org.kanur.microlog.components.repositories

import org.kanur.microlog.components.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
  fun findByEmail(email: String): User
}

package org.kanur.microlog.components.entities

import org.kanur.microlog.components.requests.UserCreateAttrs
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "users")
data class User(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", unique = true, nullable = false, updatable = false)
  val id: Long = -1,
  @field:NotBlank(message = "Name is required")
  @Column(name = "name", nullable = false, updatable = true)
  var name: String = "",
  @field:NotBlank(message = "Email is required")
  @Column(name = "email", nullable = false, updatable = true)
  var email: String = "",
) {
  companion object {
    fun from(attrs: UserCreateAttrs): User {
      return User(name = attrs.name, email = attrs.email)
    }
  }

  fun isBlank(): Boolean {
    return name.isBlank() && email.isBlank()
  }
}

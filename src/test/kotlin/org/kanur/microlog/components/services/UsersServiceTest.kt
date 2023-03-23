package org.kanur.microlog.components.services

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.kanur.microlog.components.entities.User
import org.kanur.microlog.components.repositories.UserRepository
import org.kanur.microlog.components.requests.UserCreateAttrs
import org.kanur.microlog.components.requests.UserUpdateAttrs
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import javax.sql.DataSource
import javax.transaction.Transactional
import javax.validation.ConstraintViolationException

@ExtendWith(SpringExtension::class)
@Transactional
@SpringBootTest
@TestPropertySource(
  properties = [
    "spring.jpa.hibernate.ddl-auto=validate",
    "spring.liquibase.change-log=classpath:db/changelog.xml",
  ]
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UsersServiceTest {
  @Autowired
  lateinit var dataSource: DataSource

  @Autowired
  lateinit var userRepository: UserRepository

  @Autowired
  lateinit var usersService: UsersService

  @Test
  fun contextLoads() {
    assertThat(dataSource).isNotNull
    assertThat(userRepository).isNotNull
    assertThat(usersService).isNotNull
  }

  @Test
  fun testGetAll_WhenNothingExistsInRepo() {
    assertThat(usersService.getAll()).isEmpty()
  }

  @Test
  fun testGetById_WhenNothingExistsInRepo() {
    assertThrows<NoSuchElementException> { usersService.getOneById(1L) }
  }

  @Test
  fun testCreate_WithBlankAttributes() {
    val exception = assertThrows<ConstraintViolationException> { usersService.create(UserCreateAttrs()) }

    assertThat(exception.constraintViolations.size).isEqualTo(2)
    exception.constraintViolations.find { it.propertyPath.toString() == "name" }.let {
      assertThat(it?.message).isEqualTo("Name is required")
    }
    exception.constraintViolations.find { it.propertyPath.toString() == "email" }.let {
      assertThat(it?.message).isEqualTo("Email is required")
    }
  }

  @Test
  fun testCreate_WithoutEmail() {
    val exception =
      assertThrows<ConstraintViolationException> { usersService.create(UserCreateAttrs(name = "Test name")) }

    assertThat(exception.constraintViolations.size).isEqualTo(1)
    exception.constraintViolations.find { it.propertyPath.toString() == "email" }.let {
      assertThat(it?.message).isEqualTo("Email is required")
    }
  }

  @Test
  fun testCreate_WithoutName() {
    val exception =
      assertThrows<ConstraintViolationException> { usersService.create(UserCreateAttrs(email = "user@example.test")) }

    assertThat(exception.constraintViolations.size).isEqualTo(1)
    exception.constraintViolations.find { it.propertyPath.toString() == "name" }.let {
      assertThat(it?.message).isEqualTo("Name is required")
    }
  }

  @Test
  fun testCreate_WithValidAttrs() {
    assertDoesNotThrow { usersService.create(UserCreateAttrs(email = "user@example.test", name = "Test user")) }

    val foundUser = userRepository.findByEmail("user@example.test")
    assertThat(foundUser.id).isNotNull
    assertThat(foundUser.name).isEqualTo("Test user")
  }

  @Test
  fun testUpdate_WhenIdNotValid() {
    assertThrows<NoSuchElementException> { usersService.update(1L, UserUpdateAttrs()) }
  }

  @Test
  fun testUpdate_WithNullForAttrs() {
    val testUser = userRepository.saveAndFlush(User(name = "Test user", email = "user@example.test"))
    assertDoesNotThrow { usersService.update(testUser.id, null) }

    val foundUser = userRepository.findByEmail("user@example.test")
    assertThat(foundUser.name).isEqualTo(testUser.name)
  }

  @Test
  fun testUpdate_WithBlankAttrs() {
    val testUser = userRepository.saveAndFlush(User(name = "Test user", email = "user@example.test"))
    assertDoesNotThrow { usersService.update(testUser.id, UserUpdateAttrs()) }

    val foundUser = userRepository.findByEmail("user@example.test")
    assertThat(foundUser.name).isEqualTo(testUser.name)
  }

  @Test
  fun testUpdate_WithValidAttrs() {
    val testUser = userRepository.saveAndFlush(User(name = "Test user", email = "user@example.test"))
    assertDoesNotThrow { usersService.update(testUser.id, UserUpdateAttrs(name = "Updated user name")) }

    val foundUser = userRepository.findByEmail("user@example.test")
    assertThat(foundUser.name).isEqualTo("Updated user name")
  }
}

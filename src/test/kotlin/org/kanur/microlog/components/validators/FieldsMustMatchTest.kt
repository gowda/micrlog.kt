package org.kanur.microlog.components.validators

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.validation.Validation
import javax.validation.Validator

class FieldsMustMatchTest {
  private lateinit var validator: Validator

  @FieldsMustMatch(field = "email", target = "emailConfirmation")
  data class TestData(val email: String?, val emailConfirmation: String?)

  @BeforeEach
  fun setUp() {
    validator = Validation.buildDefaultValidatorFactory().validator
  }
  @Test
  fun testNullFieldValue() {
    val data = TestData(email = null, emailConfirmation = null)
    val violations = validator.validate(data)

    assertThat(violations).isEmpty()
  }

  @Test
  fun testBlankFieldValue() {
    val data = TestData(email = "", emailConfirmation = null)
    val violations = validator.validate(data)

    violations.forEach {
      assertThat(it.propertyPath.toString()).isEqualTo("emailConfirmation")
      assertThat(it.message).isEqualTo("Fields do not match")
    }
  }

  @Test
  fun testNullTargetValue() {
    val data = TestData(email = "user@example.test", emailConfirmation = null)
    val violations = validator.validate(data)

    violations.forEach {
      assertThat(it.propertyPath.toString()).isEqualTo("emailConfirmation")
      assertThat(it.message).isEqualTo("Fields do not match")
    }
  }

  @Test
  fun testBlankTargetValue() {
    val data = TestData(email = "user@example.test", emailConfirmation = "")
    val violations = validator.validate(data)

    violations.forEach {
      assertThat(it.propertyPath.toString()).isEqualTo("emailConfirmation")
      assertThat(it.message).isEqualTo("Fields do not match")
    }
  }

  @Test
  fun testMatchingTargetValue() {
    val data = TestData(email = "user@example.test", emailConfirmation = "user@example.test")
    val violations = validator.validate(data)

    assertThat(violations).isEmpty()
  }

  @Test
  fun testMessage() {
    @FieldsMustMatch(message = "Emails do not match", field = "email", target = "emailConfirmation")
    data class MessageTestData(val email: String, val emailConfirmation: String)

    val data = MessageTestData(email = "user@example.test", emailConfirmation = "user@example")
    val violations = validator.validate(data)

    violations.forEach {
      assertThat(it.propertyPath.toString()).isEqualTo("emailConfirmation")
      assertThat(it.message).isEqualTo("Emails do not match")
    }
  }
}

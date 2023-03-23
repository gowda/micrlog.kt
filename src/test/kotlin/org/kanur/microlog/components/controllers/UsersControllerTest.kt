package org.kanur.microlog.components.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.kanur.microlog.components.entities.User
import org.kanur.microlog.components.services.UsersService
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@ExtendWith(SpringExtension::class)
@WebMvcTest(controllers = [UsersController::class])
class UsersControllerTest {
  @Autowired
  lateinit var mockMvc: MockMvc

  @Autowired
  lateinit var mapper: ObjectMapper

  @MockBean
  lateinit var usersService: UsersService

  @Test
  fun index() {
    mockMvc.perform(
      MockMvcRequestBuilders.get("/users").accept("application/json")
    ).andExpect(MockMvcResultMatchers.status().isOk)
      .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.content().string("[]"))
  }

  @Test
  fun testIndexWithKotlinDSL() {
    val testUser = User(id = 42, name = "Test user", email = "testuser@example.test")
    given(usersService.getAll()).willReturn(listOf(testUser))

    mockMvc.get("/users") {
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isOk() }
      content { contentType(MediaType.APPLICATION_JSON) }
      content { json(mapper.writeValueAsString(listOf(testUser))) }
    }
  }

  @Test
  fun testCreate_WithNoBody() {
    mockMvc.post("/users").andExpect {
      status { isUnprocessableEntity() }
      content { contentType(MediaType.APPLICATION_JSON) }
      content { json(mapper.writeValueAsString(mapOf("message" to "Request body cannot be blank"))) }
    }
  }

  @Test
  fun testCreate_WithEmptyBody() {
    mockMvc.post("/users") {
      content = ""
      contentType = MediaType.APPLICATION_JSON
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isUnprocessableEntity() }
      content { contentType(MediaType.APPLICATION_JSON) }
      content { json(mapper.writeValueAsString(mapOf("message" to "Request body cannot be blank"))) }
    }
  }

  @Test
  fun testCreate_WithEmptyJson() {
    mockMvc.post("/users") {
      content = mapper.writeValueAsString(mapOf<String, String>())
      contentType = MediaType.APPLICATION_JSON
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isUnprocessableEntity() }
      content { contentType(MediaType.APPLICATION_JSON) }
      content {
        json(
          mapper.writeValueAsString(
            mapOf(
              "code" to 422,
              "message" to "Validation failed",
              "trace" to mapOf("name" to "Name cannot be blank", "email" to "Email cannot be blank")
            )
          )
        )
      }
    }
  }

  @Test
  fun testCreate_WithoutEmail() {
    mockMvc.post("/users") {
      content = mapper.writeValueAsString(mapOf("name" to "Test name"))
      contentType = MediaType.APPLICATION_JSON
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isUnprocessableEntity() }
      content { contentType(MediaType.APPLICATION_JSON) }
      content {
        json(
          mapper.writeValueAsString(
            mapOf(
              "code" to 422,
              "message" to "Validation failed",
              "trace" to mapOf("email" to "Email cannot be blank")
            )
          )
        )
      }
    }
  }

  @Test
  fun testCreate_WithoutName() {
    mockMvc.post("/users") {
      content = mapper.writeValueAsString(mapOf("email" to "testuser@example.test"))
      contentType = MediaType.APPLICATION_JSON
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isUnprocessableEntity() }
      content { contentType(MediaType.APPLICATION_JSON) }
      content {
        json(
          mapper.writeValueAsString(
            mapOf(
              "code" to 422,
              "message" to "Validation failed",
              "trace" to mapOf("name" to "Name cannot be blank")
            )
          )
        )
      }
    }
  }

  @Test
  fun testUpdate_WhenUserDoesNotExist() {
    val testUser = User(id = 42, name = "Test user", email = "testuser@example.test")
    given(
      usersService.update(
        Mockito.eq(testUser.id),
        Mockito.argThat { _ -> true })
    ).willThrow(NoSuchElementException())

    mockMvc.put("/users/${testUser.id}").andExpect {
      status { isNotFound() }
      content { contentType(MediaType.APPLICATION_JSON) }
      content { json(mapper.writeValueAsString(mapOf("message" to "Not found"))) }
    }
  }

  @Test
  fun testUpdate_WithNoBody() {
    val testUser = User(id = 42, name = "Test user", email = "testuser@example.test")
    given(
      usersService.update(Mockito.eq(testUser.id), Mockito.argThat { arg -> arg == null })
    ).willReturn(testUser)

    mockMvc.put("/users/${testUser.id}").andExpect {
      status { isOk() }
      content { contentType(MediaType.APPLICATION_JSON) }
      content { json(mapper.writeValueAsString(testUser)) }
    }
  }

  @Test
  fun testUpdate_WithEmptyJson() {
    val testUser = User(id = 42, name = "Test user", email = "testuser@example.test")
    given(
      usersService.update(Mockito.eq(testUser.id), Mockito.argThat { arg -> arg?.name == "" })
    ).willReturn(testUser)

    mockMvc.put("/users/${testUser.id}") {
      content = mapper.writeValueAsString(mapOf<String, String>())
      contentType = MediaType.APPLICATION_JSON
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isOk() }
      content { contentType(MediaType.APPLICATION_JSON) }
      content { json(mapper.writeValueAsString(testUser)) }
    }
  }

  @Test
  fun testUpdate_WithNameInJson() {
    val testUser = User(id = 42, name = "Test user", email = "testuser@example.test")
    given(usersService.getOneById(Mockito.eq(testUser.id))).willReturn(testUser)
    given(
      usersService.update(Mockito.eq(testUser.id), Mockito.argThat { arg -> arg?.name == "Updated test name" })
    ).willReturn(testUser.copy(name = "Updated test name"))

    mockMvc.put("/users/${testUser.id}") {
      content = mapper.writeValueAsString(mapOf("name" to "Updated test name"))
      contentType = MediaType.APPLICATION_JSON
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isOk() }
      content { contentType(MediaType.APPLICATION_JSON) }
      content { json(mapper.writeValueAsString(testUser.copy(name = "Updated test name"))) }
    }
  }
}

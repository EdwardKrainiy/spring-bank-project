package com.itech.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.itech.utils.literal.PropertySourceClasspath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@TestPropertySource(locations = PropertySourceClasspath.APPLICATION_TEST_PROPERTIES_CLASSPATH)
@Transactional
class AccountControllerIntegrationTest {

  @Autowired WebApplicationContext context;

  @Autowired MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
  }

  @WithMockUser(username = "EdvardKrainiy", authorities = "MANAGER")
  @Test
  @Sql(value = PropertySourceClasspath.CREATE_USERS_SQL_CLASSPATH)
  @Sql(value = PropertySourceClasspath.CREATE_ACCOUNTS_SQL_CLASSPATH)
  void givenAccounts_whenGetAccounts_thenStatus200() throws Exception {

    String expectedJson =
        "[{\"Id\":1,\"Username\":\"EdvardKrainiy\",\"Amount\":100.0,\"Currency\":\"PLN\",\"IBAN\":\"number1\"},"
            + "{\"Id\":2,\"Username\":\"user\",\"Amount\":200.0,\"Currency\":\"EUR\",\"IBAN\":\"number2\"},"
            + "{\"Id\":3,\"Username\":\"user\",\"Amount\":300.0,\"Currency\":\"GBP\",\"IBAN\":\"number3\"}]";

    mockMvc
        .perform(get("/api/accounts").contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(result -> assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus()))
        .andExpect(result -> assertEquals(expectedJson, result.getResponse().getContentAsString()));
  }

  @Test
  @Sql(value = PropertySourceClasspath.CREATE_USERS_SQL_CLASSPATH)
  @Sql(value = PropertySourceClasspath.CREATE_ACCOUNTS_SQL_CLASSPATH)
  void unauthorizedUser_whenGetAccounts_thenStatus403() throws Exception {

    mockMvc
        .perform(get("/api/accounts").contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(
            result -> assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus()));
  }

  @WithMockUser(username = "EdvardKrainiy", authorities = "MANAGER")
  @Test
  @Sql(value = PropertySourceClasspath.CREATE_USERS_SQL_CLASSPATH)
  @Sql(value = PropertySourceClasspath.CREATE_ACCOUNTS_SQL_CLASSPATH)
  void givenAccounts_whenGetAccountById_thenStatus200() throws Exception {

    String expectedJson =
        "{\"Id\":3,\"Username\":\"user\",\"Amount\":300.0,\"Currency\":\"GBP\",\"IBAN\":\"number3\"}";

    mockMvc
        .perform(get("/api/accounts/{accountId}", 3).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(result -> assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus()))
        .andExpect(result -> assertEquals(expectedJson, result.getResponse().getContentAsString()));
  }

  @WithMockUser(username = "EdvardKrainiy", authorities = "MANAGER")
  @Test
  @Sql(value = PropertySourceClasspath.CREATE_USERS_SQL_CLASSPATH)
  void emptyAccounts_whenGetAccountById_thenStatus404() throws Exception {

    String expectedJson = "{\"Code\":404," + "\"Errors\":[\"Account not found!\"]}";

    mockMvc
        .perform(get("/api/accounts/{accountId}", 3))
        .andDo(print())
        .andExpect(
            result -> assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus()))
        .andExpect(result -> assertEquals(expectedJson, result.getResponse().getContentAsString()));
  }

  @WithMockUser(username = "EdvardKrainiy", authorities = "MANAGER")
  @Test
  @Sql(value = PropertySourceClasspath.CREATE_USERS_SQL_CLASSPATH)
  @Sql(value = PropertySourceClasspath.CREATE_ACCOUNTS_SQL_CLASSPATH)
  void emptyAccounts_whenGetAccountById_andAccountWithIdNotExists_thenStatus404() throws Exception {

    String expectedJson = "{\"Code\":404," + "\"Errors\":[\"Account not found!\"]}";

    mockMvc
        .perform(get("/api/accounts/{accountId}", 4))
        .andDo(print())
        .andExpect(
            result -> assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus()))
        .andExpect(result -> assertEquals(expectedJson, result.getResponse().getContentAsString()));
  }

  @Test
  @Sql(value = PropertySourceClasspath.CREATE_USERS_SQL_CLASSPATH)
  void unauthorizedUser_whenGetAccountById_thenStatus403() throws Exception {

    mockMvc
        .perform(get("/api/accounts/{accountId}", 3))
        .andDo(print())
        .andExpect(
            result -> assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus()));
  }

  @WithMockUser(username = "EdvardKrainiy", authorities = "MANAGER")
  @Test
  @Sql(value = PropertySourceClasspath.CREATE_USERS_SQL_CLASSPATH)
  void authorizedUser_whenCreateAccountCreationRequest_thenStatus201() throws Exception {

    String expectedCreatedUser = "{\"Amount\":100.0,\"Currency\":\"PLN\"}";
    String expectedResponse = "1";

    mockMvc
        .perform(
            post("/api/accounts")
                .content(expectedCreatedUser)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(
            result -> assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus()))
        .andExpect(
            result -> assertEquals(expectedResponse, result.getResponse().getContentAsString()));
  }

  @WithMockUser(username = "EdvardKrainiy", authorities = "MANAGER")
  @Test
  @Sql(value = PropertySourceClasspath.CREATE_USERS_SQL_CLASSPATH)
  void authorizedUser_whenCreateAccountCreationRequest_andFieldsAreInvalid_thenStatus400()
      throws Exception {

    String expectedCreatedUser = "{\"Amount\":test1,\"Currency\":\"test2\"}";

    mockMvc
        .perform(
            post("/api/accounts")
                .content(expectedCreatedUser)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(
            result ->
                assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()));
  }

  @Test
  void unauthorizedUser_whenCreateAccountCreationRequest_thenStatus403() throws Exception {

    String expectedCreatedUser = "{\"Amount\":200,\"Currency\":\"EUR\"}";

    mockMvc
        .perform(
            post("/api/accounts")
                .content(expectedCreatedUser)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(
            result -> assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus()));
  }

  @WithMockUser(username = "EdvardKrainiy", authorities = "MANAGER")
  @Test
  @Sql(value = PropertySourceClasspath.CREATE_USERS_SQL_CLASSPATH)
  @Sql(value = PropertySourceClasspath.CREATE_ACCOUNTS_SQL_CLASSPATH)
  void authorizedUser_whenUpdateAccount_thenStatus204() throws Exception {

    String expectedCreatedUser = "{\"Amount\":10.0,\"Currency\":\"EUR\"}";
    String expectedResponse =
        "{\"Id\":3,\"Username\":\"user\",\"Amount\":10.0,\"Currency\":\"GBP\",\"IBAN\":\"number3\"}";

    mockMvc
        .perform(
            put("/api/accounts/3")
                .content(expectedCreatedUser)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(
            result -> assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus()))
        .andExpect(
            result -> assertEquals(expectedResponse, result.getResponse().getContentAsString()));
  }

  @WithMockUser(username = "EdvardKrainiy", authorities = "MANAGER")
  @Test
  @Sql(value = PropertySourceClasspath.CREATE_USERS_SQL_CLASSPATH)
  @Sql(value = PropertySourceClasspath.CREATE_ACCOUNTS_SQL_CLASSPATH)
  void authorizedUser_whenUpdateAccount_andFieldsAreInvalid_thenStatus400() throws Exception {

    String expectedCreatedUser = "{\"Amount\":test1,\"Currency\":\"test2\"}";

    mockMvc
        .perform(
            put("/api/accounts/3")
                .content(expectedCreatedUser)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(
            result ->
                assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()));
  }

  @Test
  @Sql(value = PropertySourceClasspath.CREATE_USERS_SQL_CLASSPATH)
  @Sql(value = PropertySourceClasspath.CREATE_ACCOUNTS_SQL_CLASSPATH)
  void unauthorizedUser_whenUpdateAccount_thenStatus403() throws Exception {

    String expectedCreatedUser = "{\"Amount\":4,\"Currency\":\"PLN\"}";

    mockMvc
        .perform(
            put("/api/accounts/3")
                .content(expectedCreatedUser)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(
            result -> assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus()));
  }

  @WithMockUser(username = "EdvardKrainiy", authorities = "MANAGER")
  @Test
  @Sql(value = PropertySourceClasspath.CREATE_USERS_SQL_CLASSPATH)
  @Sql(value = PropertySourceClasspath.CREATE_ACCOUNTS_SQL_CLASSPATH)
  void authorizedUser_whenUpdateAccount_andAccountWithIdNotExists_thenStatus404() throws Exception {

    String expectedCreatedUser = "{\"Amount\":4,\"Currency\":\"PLN\"}";
    String expectedErrorMessage = "{\"Code\":404,\"Errors\":[\"Account not found!\"]}";

    mockMvc
        .perform(
            put("/api/accounts/100")
                .content(expectedCreatedUser)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(
            result -> assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus()))
        .andExpect(
            result ->
                assertEquals(expectedErrorMessage, result.getResponse().getContentAsString()));
  }

  @WithMockUser(username = "EdvardKrainiy", authorities = "MANAGER")
  @Test
  @Sql(value = PropertySourceClasspath.CREATE_USERS_SQL_CLASSPATH)
  @Sql(value = PropertySourceClasspath.CREATE_ACCOUNTS_SQL_CLASSPATH)
  void authorizedUser_whenDeleteAccount_thenStatus204() throws Exception {
    mockMvc
        .perform(delete("/api/accounts/3").contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(
            result ->
                assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus()));
  }

  @WithMockUser(username = "EdvardKrainiy", authorities = "MANAGER")
  @Test
  @Sql(value = PropertySourceClasspath.CREATE_USERS_SQL_CLASSPATH)
  @Sql(value = PropertySourceClasspath.CREATE_ACCOUNTS_SQL_CLASSPATH)
  void authorizedUser_whenDeleteAccount_andAccountWithIdNotExists_thenStatus404() throws Exception {
    String expectedErrorMessage = "{\"Code\":404,\"Errors\":[\"Account not found!\"]}";

    mockMvc
        .perform(delete("/api/accounts/4").contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(
            result -> assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus()))
        .andExpect(
            result ->
                assertEquals(expectedErrorMessage, result.getResponse().getContentAsString()));
  }

  @Test
  @Sql(value = PropertySourceClasspath.CREATE_USERS_SQL_CLASSPATH)
  @Sql(value = PropertySourceClasspath.CREATE_ACCOUNTS_SQL_CLASSPATH)
  void unauthorizedUser_whenDeleteAccount_thenStatus403() throws Exception {

    String expectedErrorMessage = "Access Denied";

    mockMvc
        .perform(put("/api/accounts/3").contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(
            result -> assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus()))
        .andExpect(
            result -> assertEquals(expectedErrorMessage, result.getResponse().getErrorMessage()));
  }
}

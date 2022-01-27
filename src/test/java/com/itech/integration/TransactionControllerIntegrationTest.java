package com.itech.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class TransactionControllerIntegrationTest {

  @Autowired WebApplicationContext context;

  @Autowired MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
  }

  @WithMockUser(username = "EdvardKrainiy", authorities = "MANAGER")
  @Test
  @Sql(value = PropertySourceClasspath.CREATE_USERS_SQL_CLASSPATH)
  @Sql(value = PropertySourceClasspath.CREATE_TRANSACTIONS_SQL_CLASSPATH)
  void givenTransactions_whenGetTransactions_thenStatus200() throws Exception {

    String expectedJson =
        "[{\"Id\":1,\"UserId\":1,\"IssuedAt\":\"2022-01-11T23:58:07.858+00:00\",\"Status\":\"REJECTED\",\"Operations\":[]},"
            + "{\"Id\":2,\"UserId\":2,\"IssuedAt\":\"2020-01-11T00:00:00.858+00:00\",\"Status\":\"CREATED\",\"Operations\":[]},"
            + "{\"Id\":3,\"UserId\":2,\"IssuedAt\":\"2025-01-11T00:00:00.858+00:00\",\"Status\":\"IN_PROGRESS\",\"Operations\":[]}]";

    mockMvc
        .perform(get("/api/transactions").contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(result -> assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus()))
        .andExpect(result -> assertEquals(expectedJson, result.getResponse().getContentAsString()));
  }

  @Test
  @Sql(value = PropertySourceClasspath.CREATE_USERS_SQL_CLASSPATH)
  @Sql(value = PropertySourceClasspath.CREATE_TRANSACTIONS_SQL_CLASSPATH)
  void unauthorizedUser_whenGetTransactions_thenStatus403() throws Exception {

    mockMvc
        .perform(get("/api/transactions").contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(
            result -> assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus()));
  }

  @WithMockUser(username = "EdvardKrainiy", authorities = "MANAGER")
  @Test
  @Sql(value = PropertySourceClasspath.CREATE_USERS_SQL_CLASSPATH)
  @Sql(value = PropertySourceClasspath.CREATE_TRANSACTIONS_SQL_CLASSPATH)
  void givenTransactions_whenGetTransactionById_thenStatus200() throws Exception {

    String expectedJson =
        "{\"Id\":1,\"UserId\":1,\"IssuedAt\":\"2022-01-11T23:58:07.858+00:00\",\"Status\":\"REJECTED\",\"Operations\":[]}";
    mockMvc
        .perform(get("/api/transactions/1").contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(result -> assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus()))
        .andExpect(result -> assertEquals(expectedJson, result.getResponse().getContentAsString()));
  }

  @Test
  @Sql(value = PropertySourceClasspath.CREATE_USERS_SQL_CLASSPATH)
  @Sql(value = PropertySourceClasspath.CREATE_TRANSACTIONS_SQL_CLASSPATH)
  void unauthorizedUser_whenGetTransactionById_thenStatus403() throws Exception {

    mockMvc
        .perform(get("/api/transactions/1").contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(
            result -> assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus()));
  }

  @WithMockUser(username = "EdvardKrainiy", authorities = "MANAGER")
  @Test
  @Sql(value = PropertySourceClasspath.CREATE_USERS_SQL_CLASSPATH)
  @Sql(value = PropertySourceClasspath.CREATE_TRANSACTIONS_SQL_CLASSPATH)
  void givenTransactions_whenGetTransactionById_andTransactionWithIdNotExists_thenStatus404()
      throws Exception {

    String expectedExceptionMessage = "Transaction not found!";

    mockMvc
        .perform(get("/api/transactions/400").contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(
            result -> assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus()))
        .andExpect(
            result ->
                assertTrue(
                    result.getResponse().getContentAsString().contains(expectedExceptionMessage)));
  }

  @WithMockUser(username = "EdvardKrainiy", authorities = "MANAGER")
  @Test
  @Sql(value = PropertySourceClasspath.CREATE_USERS_SQL_CLASSPATH)
  void emptyTransactions_whenGetTransactionById_thenStatus404() throws Exception {

    String expectedJson = "{\"Code\":404," + "\"Errors\":[\"Transaction not found!\"]}";
    mockMvc
        .perform(get("/api/transactions/3").contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(
            result -> assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus()))
        .andExpect(result -> assertEquals(expectedJson, result.getResponse().getContentAsString()));
  }

  @WithMockUser(username = "EdvardKrainiy", authorities = "MANAGER")
  @Test
  @Sql(value = PropertySourceClasspath.CREATE_USERS_SQL_CLASSPATH)
  @Sql(value = PropertySourceClasspath.CREATE_ACCOUNTS_SQL_CLASSPATH)
  void authorizedUser_whenCreateTransactionCreationRequest_andValuesAreInvalid_thenStatus400()
      throws Exception {
    String expectedCreatedTransactionRequest =
        "{\"Operations\":[{\"AccountNumber\":\"test2\",\"Amount\":\"test2\",\"OperationType\":\"test2\"},{\"AccountNumber\":\"test1\",\"Amount\":\"test1\",\"OperationType\":\"test1\"}]}";

    mockMvc
        .perform(
            post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(expectedCreatedTransactionRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(
            result ->
                assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()));
  }

  @WithMockUser(username = "EdvardKrainiy", authorities = "MANAGER")
  @Test
  @Sql(value = PropertySourceClasspath.CREATE_USERS_SQL_CLASSPATH)
  void authorizedUser_whenCreateTransactionCreationRequest_thenStatus201() throws Exception {
    String expectedCreatedTransactionRequest =
        "{\"Operations\":[{\"AccountNumber\":\"number1\",\"Amount\":\"1\",\"OperationType\":\"CREDIT\"},"
            + "{\"AccountNumber\":\"number2\",\"Amount\":\"1\",\"OperationType\":\"DEBIT\"}]}";

    mockMvc
        .perform(
            post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(expectedCreatedTransactionRequest)
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(
            result -> assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus()));
  }

  @Test
  @Sql(value = PropertySourceClasspath.CREATE_USERS_SQL_CLASSPATH)
  void unauthorizedUser_whenCreateTransactionCreationRequest_thenStatus403() throws Exception {
    String expectedCreatedTransactionRequest =
        "{\"Operations\":[{\"AccountNumber\":\"test2\",\"Amount\":\"1\",\"OperationType\":\"DEBIT\"},{\"AccountNumber\":\"test1\",\"Amount\":\"1\",\"OperationType\":\"CREDIT\"}]}";

    mockMvc
        .perform(
            post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(expectedCreatedTransactionRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(
            result -> assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus()));
  }
}

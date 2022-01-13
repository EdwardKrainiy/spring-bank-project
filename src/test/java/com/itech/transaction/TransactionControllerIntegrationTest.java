package com.itech.transaction;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@PropertySource("classpath:properties/exception.properties")
@Transactional
public class TransactionControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(username = "EdvardKrainiy")
    @Test
    @Sql(value = "classpath:db/test/create_users.sql")
    @Sql(value = "classpath:db/test/create_transactions.sql")
    public void givenTransactions_whenGetTransactions_thenStatus200() throws Exception {

        String expectedJson = "[{\"Id\":1,\"UserId\":null,\"IssuedAt\":\"2022-01-11T23:58:07.858+00:00\",\"Status\":\"REJECTED\",\"Operations\":[]}," +
                "{\"Id\":2,\"UserId\":null,\"IssuedAt\":\"2020-01-11T00:00:00.858+00:00\",\"Status\":\"CREATED\",\"Operations\":[]}," +
                "{\"Id\":3,\"UserId\":null,\"IssuedAt\":\"2025-01-11T00:00:00.858+00:00\",\"Status\":\"IN_PROGRESS\",\"Operations\":[]}]";

        mockMvc.perform(get("/api/transactions").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(result -> assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus()))
                .andExpect(result -> assertEquals(expectedJson, result.getResponse().getContentAsString()));
    }

    @WithMockUser(username = "EdvardKrainiy")
    @Test
    @Sql(value = "classpath:db/test/create_users.sql")
    public void emptyTransactions_whenGetTransactions_thenStatus404() throws Exception {

        String expectedJson = "{\"Code\":404," +
                "\"Errors\":[\"Transaction not found!\"]}";

        mockMvc.perform(get("/api/transactions").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(result -> assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus()))
                .andExpect(result -> assertEquals(expectedJson, result.getResponse().getContentAsString()));
    }

    @Test
    @Sql(value = "classpath:db/test/create_transactions.sql")
    @Sql(value = "classpath:db/test/create_users.sql")
    public void unauthorizedUser_whenGetTransactions_thenStatus403() throws Exception {

        String expectedErrorMessage = "Access Denied";

        mockMvc.perform(get("/api/transactions").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(result -> assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus()))
                .andExpect(result -> assertEquals(expectedErrorMessage, result.getResponse().getErrorMessage()));
    }

    @WithMockUser(username = "EdvardKrainiy")
    @Test
    @Sql(value = "classpath:db/test/create_transactions.sql")
    @Sql(value = "classpath:db/test/create_users.sql")
    public void givenTransactions_whenGetTransactionById_thenStatus200() throws Exception {

        String expectedJson = "{\"Id\":1,\"UserId\":null,\"IssuedAt\":\"2022-01-11T23:58:07.858+00:00\",\"Status\":\"REJECTED\",\"Operations\":[]}";
        mockMvc.perform(get("/api/transactions/1/").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(result -> assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus()))
                .andExpect(result -> assertEquals(expectedJson, result.getResponse().getContentAsString()));
    }

    @Test
    @Sql(value = "classpath:db/test/create_transactions.sql")
    @Sql(value = "classpath:db/test/create_users.sql")
    public void unauthorizedUser_whenGetTransactionById_thenStatus403() throws Exception {

        String expectedErrorMessage = "Access Denied";
        mockMvc.perform(get("/api/transactions/1/").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(result -> assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus()))
                .andExpect(result -> assertEquals(expectedErrorMessage, result.getResponse().getErrorMessage()));
    }

    @WithMockUser(username = "EdvardKrainiy")
    @Test
    @Sql(value = "classpath:db/test/create_transactions.sql")
    @Sql(value = "classpath:db/test/create_users.sql")
    public void givenTransactions_whenGetTransactionById_andTransactionWithIdNotExists_thenStatus404() throws Exception {

        String expectedJson = "{\"Code\":404," +
                "\"Errors\":[\"Transaction not found!\"]}";
        mockMvc.perform(get("/api/transactions/4/").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(result -> assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus()))
                .andExpect(result -> assertEquals(expectedJson, result.getResponse().getContentAsString()));
    }

    @WithMockUser(username = "EdvardKrainiy")
    @Test
    @Sql(value = "classpath:db/test/create_users.sql")
    public void emptyTransactions_whenGetTransactionById_thenStatus404() throws Exception {

        String expectedJson = "{\"Code\":404," +
                "\"Errors\":[\"Transaction not found!\"]}";
        mockMvc.perform(get("/api/transactions/3/").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(result -> assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus()))
                .andExpect(result -> assertEquals(expectedJson, result.getResponse().getContentAsString()));
    }

    @WithMockUser(username = "EdvardKrainiy")
    @Test
    @Sql(value = "classpath:db/test/create_users.sql")
    public void authorizedUser_whenCreateTransactionCreationRequest_thenStatus201() throws Exception {
        String expectedCreatedTransactionRequest = "{\"Operations\":[{\"AccountNumber\":\"test2\",\"Amount\":\"1\",\"OperationType\":\"DEBIT\"},{\"AccountNumber\":\"test1\",\"Amount\":\"1\",\"OperationType\":\"CREDIT\"}]}";

        String expectedCreationType = "\"CreationType\":\"TRANSACTION\"}";

        String expectedResponse = "{\"Id\":1,\"UserId\":1,\"Payload\":\"{\\\"Operations\\\":[" +
                "{\\\"AccountNumber\\\":\\\"test1\\\",\\\"Amount\\\":1.0,\\\"OperationType\\\":\\\"CREDIT\\\"}," +
                "{\\\"AccountNumber\\\":\\\"test2\\\",\\\"Amount\\\":1.0,\\\"OperationType\\\":\\\"DEBIT\\\"}]}\",\"Status\":\"IN_PROGRESS\",\"CreatedId\":null";
        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON)
                        .content(expectedCreatedTransactionRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(result -> assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus()))
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).contains(expectedResponse))
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).contains(expectedCreationType));

    }

    @WithMockUser(username = "EdvardKrainiy")
    @Test
    @Sql(value = "classpath:db/test/create_users.sql")
    public void authorizedUser_whenCreateTransactionCreationRequest_andValuesAreInvalid_thenStatus400() throws Exception {
        String expectedCreatedTransactionRequest = "{\"Operations\":[{\"AccountNumber\":\"test2\",\"Amount\":\"test2\",\"OperationType\":\"test2\"},{\"AccountNumber\":\"test1\",\"Amount\":\"test1\",\"OperationType\":\"test1\"}]}";

        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON)
                        .content(expectedCreatedTransactionRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(result -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()));
    }

    @Test
    @Sql(value = "classpath:db/test/create_users.sql")
    public void unauthorizedUser_whenCreateTransactionCreationRequest_thenStatus403() throws Exception {
        String expectedCreatedTransactionRequest = "{\"Operations\":[{\"AccountNumber\":\"test2\",\"Amount\":\"1\",\"OperationType\":\"DEBIT\"},{\"AccountNumber\":\"test1\",\"Amount\":\"1\",\"OperationType\":\"CREDIT\"}]}";

        String expectedErrorMessage = "Access Denied";

        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON)
                        .content(expectedCreatedTransactionRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(result -> assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus()))
                .andExpect(result -> assertEquals(expectedErrorMessage, result.getResponse().getErrorMessage()));
    }
}

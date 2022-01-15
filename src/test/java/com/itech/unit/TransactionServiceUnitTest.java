package com.itech.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itech.model.dto.request.CreationRequestDto;
import com.itech.model.dto.transaction.TransactionDto;
import com.itech.model.entity.*;
import com.itech.model.enumeration.CreationType;
import com.itech.model.enumeration.Currency;
import com.itech.model.enumeration.Role;
import com.itech.model.enumeration.Status;
import com.itech.repository.*;
import com.itech.service.transaction.TransactionService;
import com.itech.service.transaction.TransactionServiceUtil;
import com.itech.service.transaction.impl.TransactionServiceImpl;
import com.itech.utils.JsonEntitySerializer;
import com.itech.utils.JwtDecoder;
import com.itech.utils.exception.EntityNotFoundException;
import com.itech.utils.exception.ValidationException;
import com.itech.utils.mapper.operation.OperationDtoMapperImpl;
import com.itech.utils.mapper.request.RequestDtoMapperImpl;
import com.itech.utils.mapper.transaction.TransactionDtoMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {
        TransactionServiceImpl.class,
        ObjectMapper.class,
        OperationDtoMapperImpl.class,
        TransactionDtoMapperImpl.class,
        TransactionServiceUtil.class,
        JsonEntitySerializer.class,
        RequestDtoMapperImpl.class,
        JwtDecoder.class})
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:properties/exception.properties")
@TestPropertySource(locations = "classpath:properties/jwt.properties")
@TestPropertySource(locations = "classpath:properties/mail.properties")
@TestPropertySource(locations = "classpath:properties/scheduler.properties")
@TestPropertySource(locations = "classpath:properties/security.properties")
@TestPropertySource(locations = "classpath:application.properties")
class TransactionServiceUnitTest {
    @Value("${exception.accounts.are.empty}")
    private String accountsAreEmptyExceptionText;
    @Value("${exception.account.not.found}")
    private String accountNotFoundExceptionText;
    @Value("${exception.operations.are.empty}")
    private String operationsAreEmptyExceptionText;
    @Value("${exception.incorrect.request.structure}")
    private String incorrectRequestStructureExceptionText;
    @Value("${exception.currencies.are.not.same}")
    private String currenciesAreNotSameExceptionText;
    @Value("${exception.credit.is.more.than.stored}")
    private String creditIsMoreThanStoredExceptionText;
    @Value("${exception.creation.requests.not.found}")
    private String creationRequestNotFoundExceptionText;
    @Value("${exception.creation.request.expired}")
    private String creationRequestIsExpiredExceptionText;
    @Value("${exception.transaction.creation.request.with.id.not.found}")
    private String transactionCreationRequestWithThoseIdNotFoundExceptionText;
    @MockBean
    private AccountRepository accountRepository;
    @MockBean
    private CreationRequestRepository creationRequestRepository;
    @MockBean
    private OperationRepository operationRepository;
    @MockBean
    private TransactionRepository transactionRepository;
    @MockBean
    private UserRepository userRepository;
    @SpyBean
    private TransactionService transactionService;

    @WithMockUser(username = "user")
    @Test
    void givenTransactions_whenGetTransactionById_withUserIdEqualsIdOfAuthorizedUser_thenCheckUsernameOfObtainedTransactionIsEqualsUsernameOfAuthorizedUser() {
        User authorizedUser = new User("user", "user", "mail1@mail.ru", Role.USER);
        authorizedUser.setId(1L);

        when(userRepository.findUserByUsername("user")).thenReturn(Optional.of(authorizedUser));
        when(transactionRepository.findTransactionByIdAndUser(1L, authorizedUser)).thenReturn(Optional.of(new Transaction(1L, authorizedUser, LocalDateTime.now(), Status.IN_PROGRESS, null)));

        TransactionDto foundTransactionByUserIdEqualsIdOfAuthorizedUser = transactionService.findTransactionById(1L);
        assertThat(foundTransactionByUserIdEqualsIdOfAuthorizedUser.getUserId()).isEqualTo(authorizedUser.getId());
    }

    @WithMockUser(username = "user")
    @Test
    void givenTransactions_whenGetTransactions_thenCheckUsernameOfObtainedTransactionsIsEqualsUsernameOfAuthorizedUser() {
        User authorizedUser = new User("user", "user", "mail1@mail.ru", Role.USER);
        authorizedUser.setId(1L);
        when(userRepository.findUserByUsername("user")).thenReturn(Optional.of(authorizedUser));

        when(transactionRepository.findTransactionsByUser(authorizedUser)).thenReturn(new ArrayList<>(Arrays.asList(
                new Transaction(1L, authorizedUser, LocalDateTime.now(), Status.IN_PROGRESS, null),
                new Transaction(2L, authorizedUser, LocalDateTime.now(), Status.REJECTED, null),
                new Transaction(3L, authorizedUser, LocalDateTime.now(), Status.CREATED, null))));
        List<TransactionDto> foundTransactionsWhichUserIdEqualsIdOfAuthorizedUser = transactionService.findAllTransactions();

        for (TransactionDto transactionDto : foundTransactionsWhichUserIdEqualsIdOfAuthorizedUser) {
            assertThat(transactionDto.getUserId()).isEqualTo(authorizedUser.getId());
        }
    }

    @WithMockUser(username = "user")
    @Test
    void givenAccounts_whenCreateTransaction_thenCompareResults() {
        User anyUser = new User("user", "user", "mail1@mail.ru", Role.USER);
        anyUser.setId(1L);
        String payloadOfTransactionCreationRequest = "{\"Operations\":[" +
                "{\"AccountNumber\":\"number1\",\"Amount\":\"1\",\"OperationType\":\"CREDIT\"}," +
                "{\"AccountNumber\":\"number2\",\"Amount\":\"1\",\"OperationType\":\"DEBIT\"}]}";

        String transactionCreationRequestJson = "{\"Id\":1,\"UserId\":1,\"Payload\":\"{\\\"Operations\\\":[" +
                "{\\\"AccountNumber\\\":\\\"number1\\\",\\\"Amount\\\":1.0,\\\"OperationType\\\":\\\"CREDIT\\\"}," +
                "{\\\"AccountNumber\\\":\\\"number2\\\",\\\"Amount\\\":1.0,\\\"OperationType\\\":\\\"DEBIT\\\"}]}\"," +
                "\"Status\":\"IN_PROGRESS\",\"CreatedId\":null,\"IssuedAt\":\"2022-01-15T17:58:55.4132802\",\"CreationType\":\"TRANSACTION\"}";

        String expectedOperationsAndStatusResult = "status=CREATED, operations=[" +
                "OperationDto(id=null, accountId=2, transactionId=null, amount=1.0, operationType=DEBIT), " +
                "OperationDto(id=null, accountId=1, transactionId=null, amount=1.0, operationType=CREDIT)])";

        String expectedUserId = "userId=1";

        List<Account> allAccounts = new ArrayList<>(Arrays.asList(
                new Account(1L, anyUser, 200, Currency.PLN, "number1"),
                new Account(2L, anyUser, 200, Currency.PLN, "number2")));

        CreationRequest requestToCreateAndReject = new CreationRequest(1L, anyUser, payloadOfTransactionCreationRequest, Status.IN_PROGRESS, null, null, CreationType.TRANSACTION);

        when(creationRequestRepository.findCreationRequestById(1L)).thenReturn(Optional.of(requestToCreateAndReject));
        when(userRepository.findById(1L)).thenReturn(Optional.of(anyUser));
        when(accountRepository.findAll()).thenReturn(allAccounts);
        when(accountRepository.findAccountByAccountNumber("number1")).thenReturn(Optional.ofNullable(allAccounts.get(0)));
        when(accountRepository.findAccountByAccountNumber("number2")).thenReturn(Optional.ofNullable(allAccounts.get(1)));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);
        when(operationRepository.save(any(Operation.class))).thenAnswer(i -> i.getArguments()[0]);

        assertTrue(transactionService.createTransaction(transactionCreationRequestJson).toString().contains(expectedOperationsAndStatusResult));
        assertTrue(transactionService.createTransaction(transactionCreationRequestJson).toString().contains(expectedUserId));
    }

    @WithMockUser(username = "user")
    @Test
    void emptyAccounts_whenCreateTransaction_thenEntityNotFound() {
        User anyUser = new User("user", "user", "mail1@mail.ru", Role.USER);
        anyUser.setId(1L);
        String payloadOfTransactionCreationRequest = "{\"Operations\":[" +
                "{\"AccountNumber\":\"number1\",\"Amount\":\"1\",\"OperationType\":\"CREDIT\"}," +
                "{\"AccountNumber\":\"number2\",\"Amount\":\"1\",\"OperationType\":\"DEBIT\"}]}";

        String transactionCreationRequestJson = "{\"Id\":1,\"UserId\":1,\"Payload\":\"{\\\"Operations\\\":[" +
                "{\\\"AccountNumber\\\":\\\"number1\\\",\\\"Amount\\\":1.0,\\\"OperationType\\\":\\\"CREDIT\\\"}," +
                "{\\\"AccountNumber\\\":\\\"number2\\\",\\\"Amount\\\":1.0,\\\"OperationType\\\":\\\"DEBIT\\\"}]}\"," +
                "\"Status\":\"IN_PROGRESS\",\"CreatedId\":null,\"IssuedAt\":\"2022-01-15T17:58:55.4132802\",\"CreationType\":\"TRANSACTION\"}";

        CreationRequest requestToCreateAndReject = new CreationRequest(1L, anyUser, payloadOfTransactionCreationRequest, Status.IN_PROGRESS, null, null, CreationType.TRANSACTION);

        when(creationRequestRepository.findCreationRequestById(1L)).thenReturn(Optional.of(requestToCreateAndReject));
        when(userRepository.findById(1L)).thenReturn(Optional.of(anyUser));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);
        when(operationRepository.save(any(Operation.class))).thenAnswer(i -> i.getArguments()[0]);

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                transactionService.createTransaction(transactionCreationRequestJson));

        String expectedMessage = accountsAreEmptyExceptionText;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @WithMockUser(username = "user")
    @Test
    void givenAccounts_andAccountOnOperationNotExists_whenCreateTransaction_thenEntityNotFound() {
        User anyUser = new User("user", "user", "mail1@mail.ru", Role.USER);
        anyUser.setId(1L);
        String payloadOfTransactionCreationRequest = "{\"Operations\":[" +
                "{\"AccountNumber\":\"number1\",\"Amount\":\"1\",\"OperationType\":\"CREDIT\"}," +
                "{\"AccountNumber\":\"number2\",\"Amount\":\"1\",\"OperationType\":\"DEBIT\"}]}";

        String transactionCreationRequestJson = "{\"Id\":1,\"UserId\":1,\"Payload\":\"{\\\"Operations\\\":[" +
                "{\\\"AccountNumber\\\":\\\"otherNumber1\\\",\\\"Amount\\\":1.0,\\\"OperationType\\\":\\\"CREDIT\\\"}," +
                "{\\\"AccountNumber\\\":\\\"otherNumber2\\\",\\\"Amount\\\":1.0,\\\"OperationType\\\":\\\"DEBIT\\\"}]}\"," +
                "\"Status\":\"IN_PROGRESS\",\"CreatedId\":null,\"IssuedAt\":\"2022-01-15T17:58:55.4132802\",\"CreationType\":\"TRANSACTION\"}";

        List<Account> allAccounts = new ArrayList<>(Arrays.asList(
                new Account(1L, anyUser, 200, Currency.PLN, "number1"),
                new Account(2L, anyUser, 200, Currency.PLN, "number2")));

        CreationRequest requestToCreateAndReject = new CreationRequest(1L, anyUser, payloadOfTransactionCreationRequest, Status.IN_PROGRESS, null, null, CreationType.TRANSACTION);

        when(creationRequestRepository.findCreationRequestById(1L)).thenReturn(Optional.of(requestToCreateAndReject));
        when(userRepository.findById(1L)).thenReturn(Optional.of(anyUser));
        when(accountRepository.findAll()).thenReturn(allAccounts);
        when(accountRepository.findAccountByAccountNumber("number1")).thenReturn(Optional.ofNullable(allAccounts.get(0)));
        when(accountRepository.findAccountByAccountNumber("number2")).thenReturn(Optional.ofNullable(allAccounts.get(1)));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);
        when(operationRepository.save(any(Operation.class))).thenAnswer(i -> i.getArguments()[0]);

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                transactionService.createTransaction(transactionCreationRequestJson));

        String expectedMessage = accountNotFoundExceptionText;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @WithMockUser(username = "user")
    @Test
    void givenAccounts_andEmptyOperations_whenCreateTransaction_thenEntityNotFound() {
        User anyUser = new User("user", "user", "mail1@mail.ru", Role.USER);
        anyUser.setId(1L);
        String payloadOfTransactionCreationRequest = "{\"Operations\":[" +
                "{\"AccountNumber\":\"number1\",\"Amount\":\"1\",\"OperationType\":\"CREDIT\"}," +
                "{\"AccountNumber\":\"number2\",\"Amount\":\"1\",\"OperationType\":\"DEBIT\"}]}";

        String transactionCreationRequestJson = "{\"Id\":1,\"UserId\":1,\"Payload\":\"{\\\"Operations\\\":[]}\"," +
                "\"Status\":\"IN_PROGRESS\",\"CreatedId\":null,\"IssuedAt\":\"2022-01-15T17:58:55.4132802\",\"CreationType\":\"TRANSACTION\"}";

        List<Account> allAccounts = new ArrayList<>(Arrays.asList(
                new Account(1L, anyUser, 200, Currency.PLN, "number1"),
                new Account(2L, anyUser, 200, Currency.PLN, "number2")));

        CreationRequest requestToCreateAndReject = new CreationRequest(1L, anyUser, payloadOfTransactionCreationRequest, Status.IN_PROGRESS, null, null, CreationType.TRANSACTION);

        when(creationRequestRepository.findCreationRequestById(1L)).thenReturn(Optional.of(requestToCreateAndReject));
        when(userRepository.findById(1L)).thenReturn(Optional.of(anyUser));
        when(accountRepository.findAll()).thenReturn(allAccounts);
        when(accountRepository.findAccountByAccountNumber("number1")).thenReturn(Optional.ofNullable(allAccounts.get(0)));
        when(accountRepository.findAccountByAccountNumber("number2")).thenReturn(Optional.ofNullable(allAccounts.get(1)));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);
        when(operationRepository.save(any(Operation.class))).thenAnswer(i -> i.getArguments()[0]);

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                transactionService.createTransaction(transactionCreationRequestJson));

        String expectedMessage = operationsAreEmptyExceptionText;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @WithMockUser(username = "user")
    @Test
    void givenAccounts_andInvalidTransactionStructureWithTwoDebits_whenCreateTransaction_thenEntityNotFound() {
        User anyUser = new User("user", "user", "mail1@mail.ru", Role.USER);
        anyUser.setId(1L);
        String payloadOfTransactionCreationRequest = "{\"Operations\":[" +
                "{\"AccountNumber\":\"number1\",\"Amount\":\"1\",\"OperationType\":\"DEBIT\"}," +
                "{\"AccountNumber\":\"number2\",\"Amount\":\"1\",\"OperationType\":\"DEBIT\"}]}";

        String transactionCreationRequestJson = "{\"Id\":1,\"UserId\":1,\"Payload\":\"{\\\"Operations\\\":[" +
                "{\\\"AccountNumber\\\":\\\"number1\\\",\\\"Amount\\\":1.0,\\\"OperationType\\\":\\\"DEBIT\\\"}," +
                "{\\\"AccountNumber\\\":\\\"number2\\\",\\\"Amount\\\":1.0,\\\"OperationType\\\":\\\"DEBIT\\\"}]}\"," +
                "\"Status\":\"IN_PROGRESS\",\"CreatedId\":null,\"IssuedAt\":\"2022-01-15T17:58:55.4132802\",\"CreationType\":\"TRANSACTION\"}";

        List<Account> allAccounts = new ArrayList<>(Arrays.asList(
                new Account(1L, anyUser, 200, Currency.PLN, "number1"),
                new Account(2L, anyUser, 200, Currency.PLN, "number2")));

        CreationRequest requestToCreateAndReject = new CreationRequest(1L, anyUser, payloadOfTransactionCreationRequest, Status.IN_PROGRESS, null, null, CreationType.TRANSACTION);

        when(creationRequestRepository.findCreationRequestById(1L)).thenReturn(Optional.of(requestToCreateAndReject));
        when(userRepository.findById(1L)).thenReturn(Optional.of(anyUser));
        when(accountRepository.findAll()).thenReturn(allAccounts);
        when(accountRepository.findAccountByAccountNumber("number1")).thenReturn(Optional.ofNullable(allAccounts.get(0)));
        when(accountRepository.findAccountByAccountNumber("number2")).thenReturn(Optional.ofNullable(allAccounts.get(1)));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);
        when(operationRepository.save(any(Operation.class))).thenAnswer(i -> i.getArguments()[0]);

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                transactionService.createTransaction(transactionCreationRequestJson));

        String expectedMessage = incorrectRequestStructureExceptionText;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @WithMockUser(username = "user")
    @Test
    void givenAccounts_andTransactionBetweenTwoAccountsWithDifferentCurrencies_whenCreateTransaction_thenEntityNotFound() {
        User anyUser = new User("user", "user", "mail1@mail.ru", Role.USER);
        anyUser.setId(1L);
        String payloadOfTransactionCreationRequest = "{\"Operations\":[" +
                "{\"AccountNumber\":\"number1\",\"Amount\":\"1\",\"OperationType\":\"DEBIT\"}," +
                "{\"AccountNumber\":\"number2\",\"Amount\":\"1\",\"OperationType\":\"CREDIT\"}]}";

        String transactionCreationRequestJson = "{\"Id\":1,\"UserId\":1,\"Payload\":\"{\\\"Operations\\\":[" +
                "{\\\"AccountNumber\\\":\\\"number1\\\",\\\"Amount\\\":1.0,\\\"OperationType\\\":\\\"DEBIT\\\"}," +
                "{\\\"AccountNumber\\\":\\\"number2\\\",\\\"Amount\\\":1.0,\\\"OperationType\\\":\\\"CREDIT\\\"}]}\"," +
                "\"Status\":\"IN_PROGRESS\",\"CreatedId\":null,\"IssuedAt\":\"2022-01-15T17:58:55.4132802\",\"CreationType\":\"TRANSACTION\"}";

        List<Account> allAccounts = new ArrayList<>(Arrays.asList(
                new Account(1L, anyUser, 200, Currency.EUR, "number1"),
                new Account(2L, anyUser, 200, Currency.PLN, "number2")));

        CreationRequest requestToCreateAndReject = new CreationRequest(1L, anyUser, payloadOfTransactionCreationRequest, Status.IN_PROGRESS, null, null, CreationType.TRANSACTION);

        when(creationRequestRepository.findCreationRequestById(1L)).thenReturn(Optional.of(requestToCreateAndReject));
        when(userRepository.findById(1L)).thenReturn(Optional.of(anyUser));
        when(accountRepository.findAll()).thenReturn(allAccounts);
        when(accountRepository.findAccountByAccountNumber("number1")).thenReturn(Optional.ofNullable(allAccounts.get(0)));
        when(accountRepository.findAccountByAccountNumber("number2")).thenReturn(Optional.ofNullable(allAccounts.get(1)));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);
        when(operationRepository.save(any(Operation.class))).thenAnswer(i -> i.getArguments()[0]);

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                transactionService.createTransaction(transactionCreationRequestJson));

        String expectedMessage = currenciesAreNotSameExceptionText;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @WithMockUser(username = "user")
    @Test
    void givenAccounts_andTransactionWhereCreditAmountIsMoreThanStoredOnThisAccount_whenCreateTransaction_thenChangeAccountAmountException() {
        User anyUser = new User("user", "user", "mail1@mail.ru", Role.USER);
        anyUser.setId(1L);
        String payloadOfTransactionCreationRequest = "{\"Operations\":[" +
                "{\"AccountNumber\":\"number1\",\"Amount\":\"5\",\"OperationType\":\"DEBIT\"}," +
                "{\"AccountNumber\":\"number2\",\"Amount\":\"5\",\"OperationType\":\"CREDIT\"}]}";

        String transactionCreationRequestJson = "{\"Id\":1,\"UserId\":1,\"Payload\":\"{\\\"Operations\\\":[" +
                "{\\\"AccountNumber\\\":\\\"number1\\\",\\\"Amount\\\":5.0,\\\"OperationType\\\":\\\"DEBIT\\\"}," +
                "{\\\"AccountNumber\\\":\\\"number2\\\",\\\"Amount\\\":5.0,\\\"OperationType\\\":\\\"CREDIT\\\"}]}\"," +
                "\"Status\":\"IN_PROGRESS\",\"CreatedId\":null,\"IssuedAt\":\"2022-01-15T17:58:55.4132802\",\"CreationType\":\"TRANSACTION\"}";

        List<Account> allAccounts = new ArrayList<>(Arrays.asList(
                new Account(1L, anyUser, 200, Currency.EUR, "number1"),
                new Account(2L, anyUser, 1, Currency.EUR, "number2")));

        CreationRequest requestToCreateAndReject = new CreationRequest(1L, anyUser, payloadOfTransactionCreationRequest, Status.IN_PROGRESS, null, null, CreationType.TRANSACTION);

        when(creationRequestRepository.findCreationRequestById(1L)).thenReturn(Optional.of(requestToCreateAndReject));
        when(userRepository.findById(1L)).thenReturn(Optional.of(anyUser));
        when(accountRepository.findAll()).thenReturn(allAccounts);
        when(accountRepository.findAccountByAccountNumber("number1")).thenReturn(Optional.ofNullable(allAccounts.get(0)));
        when(accountRepository.findAccountByAccountNumber("number2")).thenReturn(Optional.ofNullable(allAccounts.get(1)));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);
        when(operationRepository.save(any(Operation.class))).thenAnswer(i -> i.getArguments()[0]);

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                transactionService.createTransaction(transactionCreationRequestJson));

        String expectedMessage = creditIsMoreThanStoredExceptionText;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @WithMockUser(username = "user")
    @Test
    void givenAccounts_andExpiredTransaction_whenCreateTransaction_thenValidationException() {
        User anyUser = new User("user", "user", "mail1@mail.ru", Role.USER);
        anyUser.setId(1L);
        String payloadOfTransactionCreationRequest = "{\"Operations\":[" +
                "{\"AccountNumber\":\"number1\",\"Amount\":\"5\",\"OperationType\":\"DEBIT\"}," +
                "{\"AccountNumber\":\"number2\",\"Amount\":\"5\",\"OperationType\":\"CREDIT\"}]}";

        String transactionCreationRequestJson = "{\"Id\":1,\"UserId\":1,\"Payload\":\"{\\\"Operations\\\":[" +
                "{\\\"AccountNumber\\\":\\\"number1\\\",\\\"Amount\\\":5.0,\\\"OperationType\\\":\\\"DEBIT\\\"}," +
                "{\\\"AccountNumber\\\":\\\"number2\\\",\\\"Amount\\\":5.0,\\\"OperationType\\\":\\\"CREDIT\\\"}]}\"," +
                "\"Status\":\"IN_PROGRESS\",\"CreatedId\":null,\"IssuedAt\":\"2022-01-15T17:58:55.4132802\",\"CreationType\":\"TRANSACTION\"}";

        List<Account> allAccounts = new ArrayList<>(Arrays.asList(
                new Account(1L, anyUser, 200, Currency.EUR, "number1"),
                new Account(2L, anyUser, 200, Currency.EUR, "number2")));

        CreationRequest requestToCreateAndReject = new CreationRequest(1L, anyUser, payloadOfTransactionCreationRequest, Status.IN_PROGRESS, null, null, CreationType.TRANSACTION);

        when(creationRequestRepository.findCreationRequestById(1L)).thenReturn(Optional.of(requestToCreateAndReject));
        when(userRepository.findById(1L)).thenReturn(Optional.of(anyUser));
        when(accountRepository.findAll()).thenReturn(allAccounts);
        when(accountRepository.findAccountByAccountNumber("number1")).thenReturn(Optional.ofNullable(allAccounts.get(0)));
        when(accountRepository.findAccountByAccountNumber("number2")).thenReturn(Optional.ofNullable(allAccounts.get(1)));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction(1L, anyUser, LocalDateTime.now().minusMonths(3), Status.IN_PROGRESS, null));
        when(operationRepository.save(any(Operation.class))).thenAnswer(i -> i.getArguments()[0]);

        Exception exception = assertThrows(ValidationException.class, () ->
                transactionService.createTransaction(transactionCreationRequestJson));

        String expectedMessage = creationRequestIsExpiredExceptionText;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @WithMockUser(username = "user")
    @Test
    void emptyTransactionCreationRequests_andAnyRole_whenGetTransactionCreationRequests_thenThrowsEntityNotFoundException() {
        User authorizedUser = new User("user", "user", "mail1@mail.ru", Role.MANAGER);
        authorizedUser.setId(1L);

        when(userRepository.findUserByUsername("user")).thenReturn(Optional.of(authorizedUser));
        when(creationRequestRepository.findCreationRequestsByCreationTypeAndUser(CreationType.ACCOUNT, authorizedUser)).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                transactionService.findTransactionCreationRequests());

        String expectedMessage = creationRequestNotFoundExceptionText;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @WithMockUser(username = "user")
    @Test
    void givenTransactionCreationRequests_andUserRole_whenGetTransactionCreationRequests_thenCheckCreationTypeAndIdOfRequest() {
        User authorizedUser = new User("user", "user", "mail1@mail.ru", Role.USER);
        authorizedUser.setId(1L);

        when(userRepository.findUserByUsername("user")).thenReturn(Optional.of(authorizedUser));
        when(creationRequestRepository.findCreationRequestsByCreationTypeAndUser(CreationType.TRANSACTION, authorizedUser)).thenReturn(Arrays.asList(
                new CreationRequest(1L, authorizedUser, "payload", Status.IN_PROGRESS, null, LocalDateTime.now(), CreationType.TRANSACTION),
                new CreationRequest(2L, authorizedUser, "payload2", Status.CREATED, null, LocalDateTime.now(), CreationType.TRANSACTION),
                new CreationRequest(3L, authorizedUser, "payload3", Status.REJECTED, null, LocalDateTime.now(), CreationType.TRANSACTION)
        ));

        for (CreationRequestDto request : transactionService.findTransactionCreationRequests()) {
            assertThat(request.getCreationType()).isEqualTo(CreationType.TRANSACTION.name());
            assertThat(request.getUserId()).isEqualTo(authorizedUser.getId());
        }
    }

    @WithMockUser(username = "user")
    @Test
    void givenTransactionCreationRequests_andUserRole_whenGetTransactionCreationRequestById_thenCheckCreationTypeAndIdOfRequest() {
        User authorizedUser = new User("user", "user", "mail1@mail.ru", Role.USER);
        authorizedUser.setId(1L);

        when(userRepository.findUserByUsername("user")).thenReturn(Optional.of(authorizedUser));
        when(creationRequestRepository.findCreationRequestsByCreationTypeAndIdAndUser(CreationType.TRANSACTION, 1L, authorizedUser)).thenReturn(
                Optional.of(new CreationRequest(1L, authorizedUser, "payload3", Status.REJECTED, null, LocalDateTime.now(), CreationType.TRANSACTION)));

        assertThat(transactionService.findTransactionCreationRequestById(1L).getCreationType()).isEqualTo(CreationType.TRANSACTION.name());
        assertThat(transactionService.findTransactionCreationRequestById(1L).getUserId()).isEqualTo(authorizedUser.getId());
    }

    @WithMockUser(username = "user")
    @Test
    void givenTransactionCreationRequests_andManagerRole_whenGetTransactionCreationRequestById_thenCheckCreationTypeAndIdOfRequest() {
        User authorizedUser = new User("user", "user", "mail1@mail.ru", Role.MANAGER);
        authorizedUser.setId(1L);

        when(userRepository.findUserByUsername("user")).thenReturn(Optional.of(authorizedUser));
        when(creationRequestRepository.findCreationRequestsByCreationTypeAndId(CreationType.TRANSACTION, 1L)).thenReturn(
                Optional.of(new CreationRequest(1L, authorizedUser, "payload3", Status.REJECTED, null, LocalDateTime.now(), CreationType.TRANSACTION)));

        assertThat(transactionService.findTransactionCreationRequestById(1L).getCreationType()).isEqualTo(CreationType.TRANSACTION.name());
    }

    @WithMockUser(username = "user")
    @Test
    void givenTransactionCreationRequests_andAnyRole_whenGetTransactionCreationRequestById_andRequestWithThisIdNotExists_thenEntityNotFoundException() {
        User authorizedUser = new User("user", "user", "mail1@mail.ru", Role.USER);
        authorizedUser.setId(1L);

        when(userRepository.findUserByUsername("user")).thenReturn(Optional.of(authorizedUser));
        when(creationRequestRepository.findCreationRequestsByCreationTypeAndIdAndUser(CreationType.TRANSACTION, 1L, authorizedUser)).thenReturn(
                Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                transactionService.findTransactionCreationRequestById(1L));

        String expectedMessage = transactionCreationRequestWithThoseIdNotFoundExceptionText;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}

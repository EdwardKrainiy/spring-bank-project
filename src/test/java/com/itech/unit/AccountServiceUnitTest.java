package com.itech.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itech.model.dto.account.AccountDto;
import com.itech.model.dto.account.AccountUpdateDto;
import com.itech.model.dto.request.CreationRequestDto;
import com.itech.model.entity.Account;
import com.itech.model.entity.CreationRequest;
import com.itech.model.entity.User;
import com.itech.model.enumeration.CreationType;
import com.itech.model.enumeration.Currency;
import com.itech.model.enumeration.Role;
import com.itech.model.enumeration.Status;
import com.itech.repository.AccountRepository;
import com.itech.repository.CreationRequestRepository;
import com.itech.repository.UserRepository;
import com.itech.service.account.AccountService;
import com.itech.service.account.impl.AccountServiceImpl;
import com.itech.service.mail.EmailService;
import com.itech.service.mail.impl.EmailServiceImpl;
import com.itech.utils.IbanGenerator;
import com.itech.utils.JsonEntitySerializer;
import com.itech.utils.JwtDecoder;
import com.itech.utils.exception.EntityNotFoundException;
import com.itech.utils.exception.ValidationException;
import com.itech.utils.mapper.account.AccountDtoMapperImpl;
import com.itech.utils.mapper.request.RequestDtoMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {AccountServiceImpl.class,
        AccountDtoMapperImpl.class,
        IbanGenerator.class,
        JsonEntitySerializer.class,
        JwtDecoder.class,
        JsonEntitySerializer.class,
        RequestDtoMapperImpl.class,
        EmailServiceImpl.class,
        ObjectMapper.class,
        JavaMailSenderImpl.class})
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:properties/exception.properties")
@TestPropertySource(locations = "classpath:properties/jwt.properties")
@TestPropertySource(locations = "classpath:properties/mail.properties")
@TestPropertySource(locations = "classpath:properties/scheduler.properties")
@TestPropertySource(locations = "classpath:properties/security.properties")
@TestPropertySource(locations = "classpath:application.properties")
class AccountServiceUnitTest {
    @Captor
    ArgumentCaptor<String> emailCaptor;
    @Captor
    ArgumentCaptor<String> titleCaptor;
    @Captor
    ArgumentCaptor<String> messageCaptor;
    @Value("${exception.id.of.logged.user.not.equals.id.of.account}")
    private String idOfLoggedUserNotEqualsIdOfAccountExceptionText;
    @Value("${exception.account.creation.request.with.id.not.found}")
    private String creationRequestWithIdNotFoundExceptionText;
    @Value("${exception.account.creation.requests.not.found}")
    private String accountCreationRequestsNotFoundExceptionText;
    @Value("${mail.approve.message}")
    private String approveMessage;
    @Value("${exception.user.email.not.found}")
    private String userEmailNotFoundExceptionText;
    @Value("${mail.rejected.message.title}")
    private String rejectedMessageTitleText;
    @Value("${mail.reject.message}")
    private String rejectMessage;
    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private IbanGenerator ibanGenerator;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private EmailService emailService;

    @MockBean
    private CreationRequestRepository creationRequestRepository;

    @SpyBean
    private AccountService accountService;

    @WithMockUser(username = "user")
    @Test
    void givenAccounts_andUserRole_whenGetAccountById_withUserIdEqualsIdOfAuthorizedUser_thenCheckUsernameOfObtainedAccountIsEqualsUsernameOfAuthorizedUser() {
        User authorizedUser = new User("user", "user", "mail1@mail.ru", Role.USER);
        authorizedUser.setId(1L);

        when(userRepository.findUserByUsername("user")).thenReturn(Optional.of(authorizedUser));
        when(accountRepository.findAccountByIdAndUser(2L, authorizedUser)).thenReturn(Optional.of(new Account(2L, authorizedUser, 0, Currency.EUR, "number1")));

        AccountDto foundAccountByUserIdEqualsIdOfAuthorizedUser = accountService.findAccountByAccountId(2L);
        assertThat(foundAccountByUserIdEqualsIdOfAuthorizedUser.getUsername()).isEqualTo("user");
    }

    @WithMockUser(username = "user")
    @Test
    void givenAccounts_andUserRole_whenGetAccounts_thenCheckUsernameOfObtainedAccountsIsEqualsUsernameOfAuthorizedUser() {
        User authorizedUser = new User("user", "user", "mail1@mail.ru", Role.USER);
        authorizedUser.setId(1L);
        when(userRepository.findUserByUsername("user")).thenReturn(Optional.of(authorizedUser));

        when(accountRepository.findAccountsByUser(authorizedUser)).thenReturn(new ArrayList<>(Arrays.asList(
                new Account(1L, authorizedUser, 0, Currency.PLN, "number1"),
                new Account(2L, authorizedUser, 200, Currency.GBP, "number2"),
                new Account(3L, authorizedUser, 300, Currency.PLN, "number3"))));

        List<AccountDto> foundAccountsWhichUserIdEqualsIdOfAuthorizedUser = accountService.findAllAccounts();

        for (AccountDto accountDto : foundAccountsWhichUserIdEqualsIdOfAuthorizedUser) {
            assertThat(accountDto.getUsername()).isEqualTo("user");
        }
    }

    @WithMockUser(username = "user")
    @Test
    void givenAccounts_andUserRole_whenUpdateAccount_withUserIdNotEqualsIdOfUser_thenThrowsValidationException() {
        User authorizedUser = new User("user", "user", "mail1@mail.ru", Role.USER);
        User anotherUser = new User("user2", "user2", "mail2@mail.ru", Role.USER);
        authorizedUser.setId(1L);
        anotherUser.setId(2L);

        AccountUpdateDto accountToUpdate = new AccountUpdateDto(0);
        when(userRepository.findUserByUsername("user")).thenReturn(Optional.of(authorizedUser));
        when(accountRepository.findAccountById(2L)).thenReturn(Optional.of(new Account(2L, anotherUser, 0, Currency.PLN, "number1")));

        Exception exception = assertThrows(ValidationException.class, () ->
                accountService.updateAccount(accountToUpdate, 2L));

        String expectedMessage = idOfLoggedUserNotEqualsIdOfAccountExceptionText;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @WithMockUser(username = "user")
    @Test
    void givenAccounts_andUserRole_whenDeleteAccount_withUserIdNotEqualsIdOfUser_thenThrowsEntityNotFoundException() {
        User authorizedUser = new User("user", "user", "mail1@mail.ru", Role.USER);
        User anotherUser = new User("user2", "user2", "mail2@mail.ru", Role.USER);
        authorizedUser.setId(1L);
        anotherUser.setId(2L);

        when(userRepository.findUserByUsername("user")).thenReturn(Optional.of(authorizedUser));
        when(accountRepository.findAccountById(2L)).thenReturn(Optional.of(new Account(2L, anotherUser, 0, Currency.PLN, "number1")));

        Exception exception = assertThrows(ValidationException.class, () ->
                accountService.deleteAccountByAccountId(2L));

        String expectedMessage = idOfLoggedUserNotEqualsIdOfAccountExceptionText;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @WithMockUser(username = "user")
    @Test
    void givenAccountCreationRequests_andManagerRole_whenGetAccountCreationRequestById_thenCheckUserIdOfCreationRequestEqualsIdOfAuthorizedUser() {
        User authorizedUser = new User("user", "user", "mail1@mail.ru", Role.MANAGER);
        authorizedUser.setId(1L);

        when(userRepository.findUserByUsername("user")).thenReturn(Optional.of(authorizedUser));
        when(creationRequestRepository.findCreationRequestsByCreationTypeAndId(CreationType.ACCOUNT, 1L)).thenReturn(Optional.of(new CreationRequest(1L, authorizedUser, "payload", Status.IN_PROGRESS, null, LocalDateTime.now(), CreationType.ACCOUNT)));

        assertThat(accountService.findAccountCreationRequestById(1L).getUserId()).isEqualTo(authorizedUser.getId());
    }

    @WithMockUser(username = "user")
    @Test
    void emptyAccountCreationRequests_andManagerRole_whenGetAccountCreationRequestById_thenThrowsEntityNotFoundException() {
        User authorizedUser = new User("user", "user", "mail1@mail.ru", Role.MANAGER);
        authorizedUser.setId(1L);

        when(userRepository.findUserByUsername("user")).thenReturn(Optional.of(authorizedUser));

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                accountService.findAccountCreationRequestById(1L));

        String expectedMessage = creationRequestWithIdNotFoundExceptionText;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @WithMockUser(username = "user")
    @Test
    void givenAccountCreationRequests_andUserRole_whenGetAccountCreationRequestById_thenCheckUserIdOfCreationRequestEqualsIdOfAuthorizedUser() {
        User authorizedUser = new User("user", "user", "mail1@mail.ru", Role.USER);
        authorizedUser.setId(1L);

        when(userRepository.findUserByUsername("user")).thenReturn(Optional.of(authorizedUser));
        when(creationRequestRepository.findCreationRequestsByCreationTypeAndIdAndUser(CreationType.ACCOUNT, 1L, authorizedUser)).thenReturn(Optional.of(new CreationRequest(1L, authorizedUser, "payload", Status.IN_PROGRESS, null, LocalDateTime.now(), CreationType.ACCOUNT)));

        assertThat(accountService.findAccountCreationRequestById(1L).getUserId()).isEqualTo(authorizedUser.getId());
    }

    @WithMockUser(username = "user")
    @Test
    void emptyAccountCreationRequests_andUserRole_whenGetAccountCreationRequestById_thenThrowsEntityNotFoundException() {
        User authorizedUser = new User("user", "user", "mail1@mail.ru", Role.USER);
        authorizedUser.setId(1L);

        when(userRepository.findUserByUsername("user")).thenReturn(Optional.of(authorizedUser));

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                accountService.findAccountCreationRequestById(1L));

        String expectedMessage = creationRequestWithIdNotFoundExceptionText;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @WithMockUser(username = "user")
    @Test
    void givenAccountCreationRequests_andManagerRole_whenGetAccountCreationRequests_thenCheckCreationType() {
        User authorizedUser = new User("user", "user", "mail1@mail.ru", Role.MANAGER);
        authorizedUser.setId(1L);

        when(userRepository.findUserByUsername("user")).thenReturn(Optional.of(authorizedUser));
        when(creationRequestRepository.findCreationRequestsByCreationType(CreationType.ACCOUNT)).thenReturn(Arrays.asList(
                new CreationRequest(1L, authorizedUser, "payload", Status.IN_PROGRESS, null, LocalDateTime.now(), CreationType.ACCOUNT),
                new CreationRequest(2L, authorizedUser, "payload2", Status.CREATED, 2L, LocalDateTime.now(), CreationType.ACCOUNT),
                new CreationRequest(3L, authorizedUser, "payload3", Status.REJECTED, null, LocalDateTime.now(), CreationType.ACCOUNT)
        ));

        for (CreationRequestDto request : accountService.findAccountCreationRequests()) {
            assertThat(request.getCreationType()).isEqualTo(CreationType.ACCOUNT.name());
        }
    }

    @WithMockUser(username = "user")
    @Test
    void emptyAccountCreationRequests_andAnyRole_whenGetAccountCreationRequests_thenThrowsEntityNotFoundException() {
        User authorizedUser = new User("user", "user", "mail1@mail.ru", Role.MANAGER);
        authorizedUser.setId(1L);

        when(userRepository.findUserByUsername("user")).thenReturn(Optional.of(authorizedUser));
        when(creationRequestRepository.findCreationRequestsByCreationType(CreationType.ACCOUNT)).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                accountService.findAccountCreationRequests());

        String expectedMessage = accountCreationRequestsNotFoundExceptionText;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @WithMockUser(username = "user")
    @Test
    void givenAccountCreationRequests_andUserRole_whenGetAccountCreationRequests_thenCheckCreationTypeAndIdOfRequest() {
        User authorizedUser = new User("user", "user", "mail1@mail.ru", Role.USER);
        authorizedUser.setId(1L);

        when(userRepository.findUserByUsername("user")).thenReturn(Optional.of(authorizedUser));
        when(creationRequestRepository.findCreationRequestsByCreationTypeAndUser(CreationType.ACCOUNT, authorizedUser)).thenReturn(Arrays.asList(
                new CreationRequest(1L, authorizedUser, "payload", Status.IN_PROGRESS, null, LocalDateTime.now(), CreationType.ACCOUNT),
                new CreationRequest(2L, authorizedUser, "payload2", Status.CREATED, 2L, LocalDateTime.now(), CreationType.ACCOUNT),
                new CreationRequest(3L, authorizedUser, "payload3", Status.REJECTED, null, LocalDateTime.now(), CreationType.ACCOUNT)
        ));

        for (CreationRequestDto request : accountService.findAccountCreationRequests()) {
            assertThat(request.getCreationType()).isEqualTo(CreationType.ACCOUNT.name());
            assertThat(request.getUserId()).isEqualTo(authorizedUser.getId());
        }
    }

    @WithMockUser(username = "user")
    @Test
    void emptyAccountCreationRequests_andManagerRole_whenApproveAccountCreationRequest_thenThrowsEntityNotFoundException() {
        User anyUser = new User("user", "user", "mail1@mail.ru", Role.USER);
        anyUser.setId(1L);

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                accountService.approveAccountCreationRequest(1L));

        String expectedMessage = creationRequestWithIdNotFoundExceptionText;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @WithMockUser(username = "user")
    @Test
    void givenAccountCreationRequests_andManagerRole_whenApproveAccountCreationRequest_andAccountWithAccountNumberExists_thenCheckEmailMessageStructure() {
        User anyUser = new User("user", "user", "ekrayniy@inbox.ru", Role.USER);
        anyUser.setId(1L);

        when(creationRequestRepository.findCreationRequestsByIdAndStatusAndCreationType(1L, Status.IN_PROGRESS, CreationType.ACCOUNT)).thenReturn(
                Optional.of(new CreationRequest(1L, anyUser, "{\"Amount\":200.0,\"Currency\":\"EUR\"}", Status.IN_PROGRESS, null, LocalDateTime.now(), CreationType.ACCOUNT)));
        when(ibanGenerator.generateIban(Currency.EUR.getCountryCode())).thenReturn("number1");
        String accountNumber = ibanGenerator.generateIban(Currency.EUR.getCountryCode());
        Account accountToSave = new Account(1L, anyUser, 0, Currency.PLN, accountNumber);
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArguments()[0]);
        when(accountRepository.findAccountByAccountNumber(accountNumber)).thenReturn(Optional.of(accountToSave));
        doNothing().when(emailService).sendEmail("ekrayniy@inbox.ru", "Request approved. Id of created account: 1", approveMessage);

        accountService.approveAccountCreationRequest(1L);
        verify(emailService).sendEmail(emailCaptor.capture(), titleCaptor.capture(), messageCaptor.capture());

        assertThat(emailCaptor.getValue()).isEqualTo("ekrayniy@inbox.ru");
        assertThat(titleCaptor.getValue()).isEqualTo("Request approved. Id of created account: 1");
        assertThat(messageCaptor.getValue()).isEqualTo(approveMessage);
    }

    @WithMockUser(username = "user")
    @Test
    void givenAccountCreationRequests_andManagerRole_whenApproveAccountCreationRequest_andAccountWithAccountNumberNotExists_thenCheckEmailMessageStructure() {
        User anyUser = new User("user", "user", "ekrayniy@inbox.ru", Role.USER);
        anyUser.setId(1L);

        when(creationRequestRepository.findCreationRequestsByIdAndStatusAndCreationType(1L, Status.IN_PROGRESS, CreationType.ACCOUNT)).thenReturn(
                Optional.of(new CreationRequest(1L, anyUser, "{\"Amount\":200.0,\"Currency\":\"EUR\"}", Status.IN_PROGRESS, null, LocalDateTime.now(), CreationType.ACCOUNT)));
        when(ibanGenerator.generateIban(Currency.EUR.getCountryCode())).thenReturn("number1");
        String accountNumber = ibanGenerator.generateIban(Currency.EUR.getCountryCode());
        Account accountToSave = new Account(1L, anyUser, 0, Currency.PLN, accountNumber);
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArguments()[0]);
        when(accountRepository.findAccountByAccountNumber(accountNumber)).thenReturn(Optional.empty()).thenReturn(Optional.of(accountToSave));
        doNothing().when(emailService).sendEmail("ekrayniy@inbox.ru", "Request approved. Id of created account: 1", approveMessage);

        accountService.approveAccountCreationRequest(1L);
        verify(emailService).sendEmail(emailCaptor.capture(), titleCaptor.capture(), messageCaptor.capture());

        assertThat(emailCaptor.getValue()).isEqualTo("ekrayniy@inbox.ru");
        assertThat(titleCaptor.getValue()).isEqualTo("Request approved. Id of created account: 1");
        assertThat(messageCaptor.getValue()).isEqualTo(approveMessage);
    }

    @WithMockUser(username = "user")
    @Test
    void givenAccountCreationRequests_andManagerRole_whenApproveAccountCreationRequest_andUserEmailNotExists_thenThrowsEntityNotFoundException() {
        User anyUser = new User("user", "user", null, Role.USER);
        anyUser.setId(1L);

        when(creationRequestRepository.findCreationRequestsByIdAndStatusAndCreationType(1L, Status.IN_PROGRESS, CreationType.ACCOUNT)).thenReturn(
                Optional.of(new CreationRequest(1L, anyUser, "{\"Amount\":200.0,\"Currency\":\"EUR\"}", Status.IN_PROGRESS, null, LocalDateTime.now(), CreationType.ACCOUNT)));
        when(ibanGenerator.generateIban(Currency.EUR.getCountryCode())).thenReturn("number1");
        String accountNumber = ibanGenerator.generateIban(Currency.EUR.getCountryCode());
        Account accountToSave = new Account(1L, anyUser, 0, Currency.PLN, accountNumber);
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArguments()[0]);
        when(accountRepository.findAccountByAccountNumber(accountNumber)).thenReturn(Optional.empty()).thenReturn(Optional.of(accountToSave));
        doNothing().when(emailService).sendEmail("ekrayniy@inbox.ru", "Request approved. Id of created account: 1", approveMessage);

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                accountService.approveAccountCreationRequest(1L));

        String expectedMessage = userEmailNotFoundExceptionText;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @WithMockUser(username = "user")
    @Test
    void emptyAccountCreationRequests_andManagerRole_whenRejectAccountCreationRequest_thenThrowsEntityNotFoundException() {
        User anyUser = new User("user", "user", "mail1@mail.ru", Role.USER);
        anyUser.setId(1L);

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                accountService.rejectAccountCreationRequest(1L));

        String expectedMessage = creationRequestWithIdNotFoundExceptionText;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @WithMockUser(username = "user")
    @Test
    void givenAccountCreationRequests_andManagerRole_whenRejectAccountCreationRequest_andAccountWithAccountNumberExists_thenCheckEmailMessageStructure() {
        User anyUser = new User("user", "user", "ekrayniy@inbox.ru", Role.USER);
        anyUser.setId(1L);

        when(creationRequestRepository.findCreationRequestsByIdAndStatusAndCreationType(1L, Status.IN_PROGRESS, CreationType.ACCOUNT)).thenReturn(
                Optional.of(new CreationRequest(1L, anyUser, "{\"Amount\":200.0,\"Currency\":\"EUR\"}", Status.IN_PROGRESS, null, LocalDateTime.now(), CreationType.ACCOUNT)));
        doNothing().when(emailService).sendEmail("ekrayniy@inbox.ru", rejectedMessageTitleText, rejectMessage);

        accountService.rejectAccountCreationRequest(1L);
        verify(emailService).sendEmail(emailCaptor.capture(), titleCaptor.capture(), messageCaptor.capture());

        assertThat(emailCaptor.getValue()).isEqualTo("ekrayniy@inbox.ru");
        assertThat(titleCaptor.getValue()).isEqualTo(rejectedMessageTitleText);
        assertThat(messageCaptor.getValue()).isEqualTo(rejectMessage);
    }

    @WithMockUser(username = "user")
    @Test
    void givenAccountCreationRequests_andManagerRole_whenRejectAccountCreationRequest_andUserEmailNotExists_thenThrowsEntityNotFoundException() {
        User anyUser = new User("user", "user", null, Role.USER);
        anyUser.setId(1L);

        when(creationRequestRepository.findCreationRequestsByIdAndStatusAndCreationType(1L, Status.IN_PROGRESS, CreationType.ACCOUNT)).thenReturn(
                Optional.of(new CreationRequest(1L, anyUser, "{\"Amount\":200.0,\"Currency\":\"EUR\"}", Status.IN_PROGRESS, null, LocalDateTime.now(), CreationType.ACCOUNT)));
        doNothing().when(emailService).sendEmail("ekrayniy@inbox.ru", rejectedMessageTitleText, rejectMessage);

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                accountService.rejectAccountCreationRequest(1L));

        String expectedMessage = userEmailNotFoundExceptionText;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @WithMockUser(username = "user")
    @Test
    void givenExpiredAccountCreationRequests_whenCheckExpiredAccountCreationRequests_thenAllStatusesMustBeExpired() {
        when(creationRequestRepository.findCreationRequestsByCreationTypeAndStatus(CreationType.ACCOUNT, Status.IN_PROGRESS)).thenReturn(Arrays.asList(
                new CreationRequest(1L, null, "payload", Status.IN_PROGRESS, null, LocalDateTime.now().minusDays(1), CreationType.ACCOUNT),
                new CreationRequest(2L, null, "payload2", Status.IN_PROGRESS, 2L, LocalDateTime.now().minusDays(1), CreationType.ACCOUNT),
                new CreationRequest(3L, null, "payload3", Status.IN_PROGRESS, null, LocalDateTime.now().minusDays(1), CreationType.ACCOUNT)));
        accountService.checkExpiredAccountCreationRequests();
        for (CreationRequest creationRequest : creationRequestRepository.findCreationRequestsByCreationTypeAndStatus(CreationType.ACCOUNT, Status.IN_PROGRESS)) {
            assertThat(creationRequest.getStatus()).isEqualTo(Status.EXPIRED);
        }
    }
}

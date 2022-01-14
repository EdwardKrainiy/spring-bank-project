package com.itech.service.account;

import com.itech.model.dto.account.AccountDto;
import com.itech.model.entity.Account;
import com.itech.model.entity.User;
import com.itech.model.enumeration.Currency;
import com.itech.model.enumeration.Role;
import com.itech.repository.AccountRepository;
import com.itech.repository.UserRepository;
import com.itech.service.account.impl.AccountServiceImpl;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {AccountServiceImpl.class})
@ExtendWith(SpringExtension.class)
public class AccountServiceUnitTest {

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private UserRepository userRepository;

    @SpyBean
    private AccountService accountService;

    @WithMockUser(username = "user")
    @Test
    public void givenAccounts_andUserRole_whenGetAccountById_withUserIdEqualsIdOfAuthorizedUser() throws Exception {
        User authorizedUser = new User("user", "user", "mail1@mail.ru", Role.USER);
        authorizedUser.setId(1L);

        when(userRepository.findUserByUsername("user")).thenReturn(Optional.of(authorizedUser));
        when(accountRepository.findAccountByIdAndUser(2L, authorizedUser)).thenReturn(Optional.of(new Account(2L, authorizedUser, 0, Currency.EUR, "number1")));

        AccountDto foundAccountByUserIdEqualsIdOfAuthorizedUser = accountService.findAccountByAccountId(2L);
        assertThat(foundAccountByUserIdEqualsIdOfAuthorizedUser.getUsername()).isEqualTo("user");
    }

    @WithMockUser(username = "user")
    @Test
    public void givenAccounts_andUserRole_whenGetAccounts() throws Exception {
        User authorizedUser = new User("user", "user", "mail1@mail.ru", Role.USER);
        authorizedUser.setId(1L);
        when(userRepository.findUserByUsername("user")).thenReturn(Optional.of(authorizedUser));

        when(accountRepository.findAccountsByUser(authorizedUser)).thenReturn(new ArrayList<>(Arrays.asList(
                new Account(1L, authorizedUser, 0, Currency.PLN, "number1"),
                new Account(2L, authorizedUser, 200, Currency.GBP, "number2"),
                new Account(3L, authorizedUser, 300, Currency.PLN, "number3"))));

        List<AccountDto> foundAccountsWhichUserIdEqualsIdOfAuthorizedUser = accountService.findAllAccounts();
        foundAccountsWhichUserIdEqualsIdOfAuthorizedUser.stream().map(account -> assertThat(account.getUsername()).isEqualTo("user"));
    }

    @WithMockUser(username = "user")
    @Test
    public void givenAccounts_andUserRole_whenUpdateAccount_withUserIdNotEqualsIdOfUser() throws Exception {
        User authorizedUser = new User("user", "user", "mail1@mail.ru", Role.USER);
        authorizedUser.setId(1L);
        when(userRepository.findUserByUsername("user")).thenReturn(Optional.of(authorizedUser));


        List<AccountDto> foundAccountsWhichUserIdEqualsIdOfAuthorizedUser = accountService.findAllAccounts();
        foundAccountsWhichUserIdEqualsIdOfAuthorizedUser.stream().map(account -> assertThat(account.getUsername()).isEqualTo("user"));
    }
}

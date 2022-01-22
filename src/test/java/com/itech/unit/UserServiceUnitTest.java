package com.itech.unit;

import com.itech.config.SecurityConfig;
import com.itech.model.dto.user.UserSignUpDto;
import com.itech.model.entity.User;
import com.itech.model.enumeration.Role;
import com.itech.repository.UserRepository;
import com.itech.security.jwt.provider.TokenProvider;
import com.itech.service.mail.EmailService;
import com.itech.service.mail.impl.EmailServiceImpl;
import com.itech.service.user.UserService;
import com.itech.service.user.impl.CustomUserDetailsService;
import com.itech.service.user.impl.UserServiceImpl;
import com.itech.utils.JwtDecoder;
import com.itech.utils.exception.EntityNotFoundException;
import com.itech.utils.exception.IncorrectPasswordException;
import com.itech.utils.exception.ValidationException;
import com.itech.utils.literal.ExceptionMessageText;
import com.itech.utils.mapper.user.UserSignUpDtoMapper;
import com.itech.utils.mapper.user.UserSignUpDtoMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {UserServiceImpl.class,
        CustomUserDetailsService.class,
        SecurityConfig.class,
        EmailServiceImpl.class,
        TokenProvider.class,
        JwtDecoder.class,
        JavaMailSenderImpl.class,
        UserSignUpDtoMapperImpl.class})
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:properties/jwt.properties")
@TestPropertySource(locations = "classpath:properties/mail.properties")
@TestPropertySource(locations = "classpath:properties/scheduler.properties")
@TestPropertySource(locations = "classpath:properties/security.properties")
@TestPropertySource(locations = "classpath:application.properties")
class UserServiceUnitTest {
    @Captor
    ArgumentCaptor<String> emailCaptor;
    @Captor
    ArgumentCaptor<String> titleCaptor;
    @Captor
    ArgumentCaptor<String> messageCaptor;
    @Value("${mail.confirmation.message}")
    private String confirmMessage;
    @Value("${mail.user.confirmation.title}")
    private String userConfirmationMessageTitleText;
    @Value("${mail.user.successful.confirmation.title}")
    private String successfulConfirmationTitle;
    @Value("${mail.user.successful.confirmation.message}")
    private String successfulConfirmationMessage;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private TokenProvider tokenProvider;
    @MockBean
    private JwtDecoder jwtDecoder;
    @SpyBean
    private CustomUserDetailsService customUserDetailsService;
    @SpyBean
    private UserSignUpDtoMapper userSignUpDtoMapper;
    @SpyBean
    private PasswordEncoder encoder;
    @SpyBean
    private EmailService emailService;
    @SpyBean
    private UserService userService;

    @Test
    void givenUser_whenLoadUserByUsername_thenCheckCredentialsAndRoleOfObtainedUser() {
        User userToFound = new User("user", "user", null, Role.USER);
        when(userRepository.findUserByUsername("user")).thenReturn(Optional.of(userToFound));
        assertThat(customUserDetailsService.loadUserByUsername("user").getUsername()).isEqualTo(userToFound.getUsername());
        assertThat(customUserDetailsService.loadUserByUsername("user").getPassword()).isEqualTo(userToFound.getPassword());
        assertThat(customUserDetailsService.loadUserByUsername("user").getAuthorities().toString()).contains(userToFound.getRole().toString());
    }

    @Test
    void createdUserNotExists_whenCreateUser_thenCheckEmailMessageStructure() {
        UserSignUpDto anyUser = new UserSignUpDto("mail1@mail.ru");
        anyUser.setUsername("user");
        anyUser.setPassword("user");

        User anyUserEntity = userSignUpDtoMapper.toEntity(anyUser);
        anyUserEntity.setPassword(encoder.encode(anyUserEntity.getPassword()));
        anyUserEntity.setRole(Role.USER);
        anyUserEntity.setId(1L);

        when(userRepository.save(any(User.class))).thenReturn(anyUserEntity);
        when(tokenProvider.generateConfirmToken(anyUserEntity.getId())).thenReturn("token1");
        when(userRepository.findUserByRole(Role.MANAGER)).thenReturn(Optional.of(new User("manager", "manager", "manager@manager.ru", Role.MANAGER)));

        doNothing().when(emailService).sendEmail("manager@manager.ru",
                String.format(userConfirmationMessageTitleText, anyUserEntity.getUsername()),
                String.format(("%s%s"), confirmMessage, "token1"));

        userService.createUser(anyUser);
        verify(emailService).sendEmail(emailCaptor.capture(), titleCaptor.capture(), messageCaptor.capture());

        assertThat(emailCaptor.getValue()).isEqualTo("manager@manager.ru");
        assertThat(titleCaptor.getValue()).isEqualTo(String.format(userConfirmationMessageTitleText, anyUserEntity.getUsername()));
        assertThat(messageCaptor.getValue()).isEqualTo(String.format(("%s%s"), confirmMessage, "token1"));
    }

    @Test
    void createdUserExists_whenCreateUser_thenValidationException() {
        UserSignUpDto anyUser = new UserSignUpDto("mail1@mail.ru");
        anyUser.setUsername("user");
        anyUser.setPassword("user");

        User anyUserEntity = userSignUpDtoMapper.toEntity(anyUser);
        anyUserEntity.setPassword(encoder.encode(anyUserEntity.getPassword()));
        anyUserEntity.setRole(Role.USER);
        anyUserEntity.setId(1L);

        when(userRepository.save(any(User.class))).thenReturn(anyUserEntity);
        when(userRepository.findUserByUsername(anyUserEntity.getUsername())).thenReturn(Optional.of(anyUserEntity));
        when(userRepository.findUserByEmail(anyUserEntity.getEmail())).thenReturn(Optional.of(anyUserEntity));

        Exception exception = assertThrows(ValidationException.class, () ->
                userService.createUser(anyUser));

        String expectedMessage = ExceptionMessageText.USER_IS_ALREADY_EXISTS;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void givenUsers_andFindUserByUsername_thenCheckAllValues() {
        User anyUser = new User("user", "user", "user@user.ru", Role.USER);

        when(userRepository.findUserByUsername("user")).thenReturn(Optional.of(anyUser));

        assertThat(userService.findUserByUsername("user").getUsername()).isEqualTo(anyUser.getUsername());
        assertThat(userService.findUserByUsername("user").getPassword()).isEqualTo(anyUser.getPassword());
        assertThat(userService.findUserByUsername("user").getEmail()).isEqualTo(anyUser.getEmail());
        assertThat(userService.findUserByUsername("user").getRole()).isEqualTo(anyUser.getRole());
    }

    @Test
    void emptyUsers_andFindUserByUsername_thenEntityNotFoundException() {
        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                userService.findUserByUsername("user"));

        String expectedMessage = ExceptionMessageText.USER_NOT_FOUND;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void givenUser_andFindUserByUsernameAndPassword_thenCheckAllValues() {
        User anyUser = new User("user", "user", "user@user.ru", Role.USER);

        when(userRepository.findUserByUsername("user")).thenReturn(Optional.of(anyUser));

        assertThat(userService.findUserByUsernameAndPassword("user", "user").getUsername()).isEqualTo(anyUser.getUsername());
        assertThat(userService.findUserByUsernameAndPassword("user", "user").getPassword()).isEqualTo(anyUser.getPassword());
    }

    @Test
    void givenUser_andFindUserByUsernameAndPassword_andPasswordIsWrong_thenIncorrectPasswordException() {
        User anyUser = new User("user", "user", "user@user.ru", Role.USER);

        when(userRepository.findUserByUsername("user")).thenReturn(Optional.of(anyUser));

        Exception exception = assertThrows(IncorrectPasswordException.class, () ->
                userService.findUserByUsernameAndPassword("user", "wrongPassword"));

        String expectedMessage = "Incorrect password for username = user!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void givenUser_andActivateUser_thenCheckEmailMessageStructure() {
        User anyUser = new User(1L, "user", "user", "user@user.ru", "token1", Role.USER, false, null);

        when(jwtDecoder.getIdFromConfirmToken("token1")).thenReturn(1L);
        when(userRepository.getById(1L)).thenReturn(anyUser);
        doNothing().when(emailService).sendEmail(anyUser.getEmail(), successfulConfirmationTitle, successfulConfirmationMessage);

        userService.activateUser("token1");
        verify(emailService).sendEmail(emailCaptor.capture(), titleCaptor.capture(), messageCaptor.capture());

        assertThat(emailCaptor.getValue()).isEqualTo(anyUser.getEmail());
        assertThat(titleCaptor.getValue()).isEqualTo(successfulConfirmationTitle);
        assertThat(messageCaptor.getValue()).isEqualTo(successfulConfirmationMessage);
    }

    @Test
    void givenAlreadyActivatedUserUser_andActivateUser_thenCheckEmailMessageStructure() {
        User anyUser = new User(1L, "user", "user", "user@user.ru", null, Role.USER, false, null);

        when(jwtDecoder.getIdFromConfirmToken("token1")).thenReturn(1L);
        when(userRepository.getById(1L)).thenReturn(anyUser);

        Exception exception = assertThrows(ValidationException.class, () ->
                userService.activateUser("token1"));

        String expectedMessage = ExceptionMessageText.USER_IS_ALREADY_ACTIVATED;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}

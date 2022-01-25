package com.itech.contoller;

import com.itech.model.dto.user.UserSignInDto;
import com.itech.model.dto.user.UserSignUpDto;
import com.itech.service.user.UserService;
import com.itech.utils.JsonEntitySerializer;
import com.itech.utils.literal.LogMessageText;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication controller with sign-in, sign-up and email-confirmation endpoints.
 *
 * @author Edvard Krainiy on 12/3/2021
 */
@RestController
@Log4j2
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final UserService userService;

  private final JsonEntitySerializer jsonEntitySerializer;

  /**
   * Sign-in endpoint.
   *
   * @param userSignInDto User object we need to sign-in.
   * @return ResponseEntity Response, which contains message and HTTP code.
   */
  @ApiOperation(
      value = "Sign in.",
      notes = "Checks entered credentials and signs in user.",
      response = String.class)
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Successful sign in."),
        @ApiResponse(code = 400, message = "Bad request because of invalid credentials."),
        @ApiResponse(code = 404, message = "User with this credentials not found.")
      })
  @ResponseStatus(value = HttpStatus.OK)
  @PostMapping("/sign-in")
  public ResponseEntity<String> signIn(
      @RequestBody
          @Valid
          @ApiParam(name = "userSignInDto", value = "Dto of user, which we want to sign in.")
          UserSignInDto userSignInDto)
      throws AuthenticationException {

    if (log.isDebugEnabled()) {
      log.debug(
          String.format(
              LogMessageText.DEBUG_REQUEST_BODY_LOG,
              jsonEntitySerializer.serializeObjectToJson(userSignInDto)));
    }
    return userService.authenticateUser(userSignInDto);
  }

  /**
   * Sign-up endpoint.
   *
   * @param userSignUpDto User object we need to sign-up.
   * @return ResponseEntity Response, which contains message and HTTP code.
   */
  @ApiOperation(value = "Sign up.", notes = "Checks entered credentials and signs up new user.")
  @ApiResponses(
      value = {
        @ApiResponse(code = 201, message = "Successful sign up."),
        @ApiResponse(
            code = 400,
            message =
                "Bad request because of invalid values or existing of user with this username or email.")
      })
  @PostMapping("/sign-up")
  public ResponseEntity<Void> signUp(
      @RequestBody
          @Valid
          @ApiParam(name = "userSignUpDto", value = "Dto of user, which we want to sign up.")
          UserSignUpDto userSignUpDto) {

    if (log.isDebugEnabled()) {
      log.debug(
          String.format(
              LogMessageText.DEBUG_REQUEST_BODY_LOG,
              jsonEntitySerializer.serializeObjectToJson(userSignUpDto)));
    }

    userService.createUser(userSignUpDto);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  /**
   * Email-confirmation endpoint.
   *
   * @param token token of user we need to confirm.
   * @return ResponseEntity Response, which contains message and HTTP code.
   */
  @ApiOperation(value = "Email confirmation.", notes = "Confirms account by token sent to email.")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Successful email confirmation."),
        @ApiResponse(code = 400, message = "Bad request or this user is already activated."),
        @ApiResponse(
            code = 403,
            message = "Accessing the resource you were trying to reach is forbidden"),
      })
  @GetMapping("/email-confirmation")
  public ResponseEntity<Void> emailConfirmation(
      @RequestParam("token")
          @ApiParam(name = "token", value = "Token of user, which we want to confirm.")
          String token) {
    userService.activateUser(token);
    return ResponseEntity.ok().build();
  }
}

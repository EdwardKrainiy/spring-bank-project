package com.itech.service.user;

import com.itech.model.dto.user.UserSignInDto;
import com.itech.model.dto.user.UserSignUpDto;
import com.itech.model.entity.User;
import com.itech.utils.exception.EntityExistsException;
import com.itech.utils.exception.EntityNotFoundException;
import org.springframework.http.ResponseEntity;

/**
 * UserService interface. Provides us different methods to work with User objects on Service layer.
 *
 * @author Edvard Krainiy on 12/7/2021
 */

public interface UserService {

    /**
     * createUser method. Saves our user on DB.
     *
     * @param userDto User transfer object, which we need to save. This one will be converted into User object, passed some checks and will be saved on DB.
     * @throws EntityNotFoundException If user wasn't found.
     * @throws EntityExistsException   if user already exists.
     */

    void createUser(UserSignUpDto userDto);

    /**
     * findUserByUsername method. Finds user by username.
     *
     * @param username Username of the user we need to get from DB.
     * @return User Found by username User object. If user wasn't found, it will throw UserNotFoundException.
     */

    User findUserByUsername(String username);

    /**
     * findUserByUsernameAndPassword method. Finds user by username and password.
     *
     * @param username Username of the user we need to get from DB.
     * @param password Password of the user we need to get from DB.
     * @return User Found by username and password User object. If user wasn't found, it will throw UserNotFoundException.
     */

    User findUserByUsernameAndPassword(String username, String password);

    /**
     * activateUser method. Activates user, found by token.
     *
     * @param token Transferred token of the user we need to activate.
     */

    void activateUser(String token);

    /**
     * isUserActivated method. Checks, is User activated.
     *
     * @param user User object, which we need to check.
     * @return Boolean isUserActivated flag.
     */

    boolean isUserActivated(User user);

    /**
     * authenticateUser method. Checks potential user and authenticate him. If this user is not activated or not exists, Exception will be thrown.
     *
     * @param userSignInDto UserSignInDto object, which contains all necessary information to signing in.
     */
    ResponseEntity<String> authenticateUser(UserSignInDto userSignInDto);
}

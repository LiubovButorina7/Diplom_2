package ru.practikum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.practikum.config.RestConfig;
import ru.practikum.models.User;
import ru.practikum.steps.UserSteps;

import static org.hamcrest.Matchers.is;

public class LoginUserTests extends BaseTest{
    private final UserSteps userSteps = new UserSteps();
    private User user;
    private String userAccessTokenAuthorized;

    @Before
    public void setUp() {
        user = new User();
        setRequestEmailPasswordName(RandomStringUtils.randomAlphabetic(12), RandomStringUtils.randomAlphabetic(12), RandomStringUtils.randomAlphabetic(6));
        ValidatableResponse response = userSteps.registerUser(user);
        getUserAccessTokenAuthorized(response);
    }

    @Test
    @DisplayName("Login user with existing email and password")
    @Description("Test for '/api/auth/login' endpoint")
    public void testLoginUserWithExistingLoginAndPasswordReturnsSuccess() {
        ValidatableResponse response = userSteps.loginUser(user);
        checkCodeResponse(response, HttpStatus.SC_OK);
        checkBodyResponse(response, RestConfig.KEY_SUCCESS,RestConfig.VALUE_SUCCESS);
    }

    @Step("Set user email, password and name")
    public void setRequestEmailPasswordName(String email, String password, String name) {
        user.setEmail(email != null ? email +"@test.ru" : email);
        user.setPassword(password);
        user.setName(name);
    }

    @Step("Get authorized accessToken")
    public void getUserAccessTokenAuthorized(ValidatableResponse response) {
        userAccessTokenAuthorized = response.extract().body().path("accessToken");
    }

    @Step("Check code response")
    public void checkCodeResponse(ValidatableResponse response, Integer expectedCode) {
        response.statusCode(expectedCode);
    }

    @Step("Check body response")
    public void checkBodyResponse(ValidatableResponse response, String key, Object value) {
        response.body(key, is(value));
    }

    @Step("Set accessToken for delete request")
    public void setUserAccessToken() {
        user.setAccessToken(userAccessTokenAuthorized);
    }

    @After
    public void tearDown() {
        setUserAccessToken();
        userSteps.deleteUser(user);
    }
}

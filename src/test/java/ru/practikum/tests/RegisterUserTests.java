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

public class RegisterUserTests extends BaseTest {
    private final UserSteps userSteps = new UserSteps();
    private User user;
    private String userAccessToken;

    @Before
    public void setUp() {
        user = new User();
    }

    @Test
    @DisplayName("Register unique user")
    @Description("Test for '/api/auth/register' endpoint")
    public void testRegisterUniqueUserReturnsSuccess() {
        setRequestEmailPasswordName(RandomStringUtils.randomAlphabetic(12)+"@test.ru", RandomStringUtils.randomAlphabetic(12),RandomStringUtils.randomAlphabetic(6));
        ValidatableResponse response = userSteps.registerUser(user);
        checkCodeResponse(response, HttpStatus.SC_OK);
        checkBodyResponse(response, RestConfig.KEY_REGISTER, RestConfig.VALUE_REGISTER_SUCCESS);
    }

    @Test
    @DisplayName("Register user that already exists")
    @Description("Test for '/api/auth/register' endpoint")
    public void testRegisterUserAlreadyExistingReturnsFailure() {
        setRequestEmailPasswordName(RandomStringUtils.randomAlphabetic(12)+"@test.ru", RandomStringUtils.randomAlphabetic(12),RandomStringUtils.randomAlphabetic(6));
        userSteps.registerUser(user);
        ValidatableResponse response = userSteps.registerUser(user);
        checkCodeResponse(response, HttpStatus.SC_FORBIDDEN);
        checkBodyResponse(response, RestConfig.KEY_REGISTER, RestConfig.VALUE_REGISTER_ERROR);
        checkBodyResponse(response, RestConfig.KEY_MESSAGE, RestConfig.VALUE_REGISTER_MESSAGE_ERROR);
    }

    @Step("Set user email, password and name")
    public void setRequestEmailPasswordName(String email, String password, String name) {
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
    }

    @Step("Check code response")
    public void checkCodeResponse(ValidatableResponse response, Integer expectedCode) {
        response.statusCode(expectedCode);
    }

    @Step("Check body response")
    public void checkBodyResponse(ValidatableResponse response, String key, Object value) {
        response.body(key, is(value));
    }

    @Step("Get accessToken from authorized user")
    public void getAccessToken() {
        try {
            userAccessToken = userSteps.loginUser(user).extract().body().path("accessToken");
        } catch (Exception e) {
            System.out.println("Ошибка при парсинге JSON: " + e.getMessage());
        }
    }

    @Step("Set accessToken for delete request")
    public void setUserAccessToken() {
        user.setAccessToken(userAccessToken);
    }

    @After
    public void tearDown() {
        getAccessToken();
        if (userAccessToken != null) {
            setUserAccessToken();
            userSteps.deleteUser(user);
        }
    }

}

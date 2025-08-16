package ru.practikum.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.practikum.config.Endpoints;
import ru.practikum.models.User;

import static io.restassured.RestAssured.given;

public class UserSteps {
    @Step("Send POST request to /api/auth/register")
    public ValidatableResponse registerUser(User user) {
        return given()
                    .body(user)
                    .when()
                    .post(Endpoints.REGISTER_USER)
                    .then();
    }

    @Step("Send POST request to /api/auth/login")
    public ValidatableResponse loginUser(User user) {
        return given()
                .body(user)
                .when()
                .post(Endpoints.LOGIN_USER)
                .then();
    }

    @Step("Send DELETE request to /api/auth/user")
    public void deleteUser(User user) {
        given()
            .header("Authorization", user.getAccessToken())
            .when()
            .post(Endpoints.DELETE_USER)
            .then();
    }
}

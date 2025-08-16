package ru.practikum.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.practikum.config.Endpoints;
import ru.practikum.models.Order;
import ru.practikum.models.User;

import static io.restassured.RestAssured.given;

public class OrderSteps {
    @Step("Send POST request with authorization to /api/orders")
    public ValidatableResponse makeOrder(Order order, User user) {
        return given()
                .header("Authorization", user.getAccessToken())
                .body(order)
                .when()
                .post(Endpoints.MAKE_ORDER)
                .then();
    }

    @Step("Send POST request to /api/orders")
    public ValidatableResponse makeOrder(Order order) {
        return given()
                .body(order)
                .when()
                .post(Endpoints.MAKE_ORDER)
                .then();
    }

    @Step("Send Get request to /api/ingredients")
    public ValidatableResponse getIngredients() {
        return given()
                .when()
                .get(Endpoints.GET_INGREDIENTS)
                .then();
    }
}

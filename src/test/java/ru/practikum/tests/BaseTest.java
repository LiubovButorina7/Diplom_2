package ru.practikum.tests;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import ru.practikum.config.RestConfig;

import static org.hamcrest.Matchers.is;

public class BaseTest {
    @Before
    public void startUp() {
        RestAssured.defaultParser = Parser.JSON;
        RestAssured.requestSpecification = new RequestSpecBuilder().setBaseUri(RestConfig.HOST)
                                                                   .setContentType(ContentType.JSON)
                                                                   .build();
        RestAssured.config = RestAssured.config()
                                        .logConfig(LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails());
    }

    @Step("Check code response")
    public void checkCodeResponse(ValidatableResponse response, Integer expectedCode) {
        response.statusCode(expectedCode);
    }

    @Step("Check body response")
    public void checkBodyResponse(ValidatableResponse response, String key, Object value) {
        response.body(key, is(value));
    }
}

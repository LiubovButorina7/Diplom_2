package ru.practikum.tests;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.junit.Before;
import ru.practikum.config.RestConfig;

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
}

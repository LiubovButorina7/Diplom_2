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
import ru.practikum.models.Order;
import ru.practikum.models.User;
import ru.practikum.steps.OrderSteps;
import ru.practikum.steps.UserSteps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MakeOrderTests extends BaseTest {
    private Order order;
    private User user;
    private final OrderSteps orderSteps = new OrderSteps();
    private UserSteps userSteps;
    private String userAccessTokenAuthorized;
    private ArrayList<String> ingredients;

    @Before
    public void setUp() {
        order = new Order();
        ingredients = new ArrayList<>();

        user = new User();
        userSteps = new UserSteps();
        setRequestEmailPasswordName(RandomStringUtils.randomAlphabetic(12), RandomStringUtils.randomAlphabetic(12), RandomStringUtils.randomAlphabetic(6));
        userSteps.registerUser(user);
        ValidatableResponse responseUser = userSteps.loginUser(user);
        getUserAccessTokenAuthorized(responseUser);
        setUserAccessToken();
    }

    @Test
    @DisplayName("Make order with authorization and ingredients")
    @Description("Test for '/api/orders' endpoint")
    public void testMakeOrderWithAuthorizationAndIngredientsReturnsSuccess() {
        ValidatableResponse responseIngredients = orderSteps.getIngredients();
        setOrderIngredients(responseIngredients);
        ValidatableResponse responseOrder = orderSteps.makeOrder(order, user);
        checkCodeResponse(responseOrder, HttpStatus.SC_OK);
        checkBodyResponse(responseOrder, RestConfig.KEY_SUCCESS,RestConfig.VALUE_SUCCESS);
    }

    @Test
    @DisplayName("Make order with authorization and without ingredients")
    @Description("Test for '/api/orders' endpoint")
    public void testMakeOrderWithAuthorizationAndWithoutIngredientsReturnsFailure() {
        ValidatableResponse responseOrder = orderSteps.makeOrder(order, user);
        checkCodeResponse(responseOrder, HttpStatus.SC_BAD_REQUEST);
        checkBodyResponse(responseOrder, RestConfig.KEY_MESSAGE,RestConfig.VALUE_MAKE_ORDER_MESSAGE_NO_INGREDIENTS);
    }

    @Test
    @DisplayName("Make order with authorization and incorrect ingredients")
    @Description("Test for '/api/orders' endpoint")
    public void testMakeOrderWithAuthorizationAndIncorrectIngredientsReturnsFailure() {
        setOrderIncorrectIngredients(RandomStringUtils.randomAlphanumeric(8));
        ValidatableResponse responseOrder = orderSteps.makeOrder(order, user);
        checkCodeResponse(responseOrder, HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Make order without authorization and with ingredients")
    @Description("Test for '/api/orders' endpoint")
    public void testMakeOrderWithoutAuthorizationAndWithIngredientsReturnsFailure() {
        ValidatableResponse responseIngredients = orderSteps.getIngredients();
        setOrderIngredients(responseIngredients);
        ValidatableResponse responseOrder = orderSteps.makeOrder(order);
        checkCodeResponse(responseOrder, HttpStatus.SC_UNAUTHORIZED);
        checkBodyResponse(responseOrder, RestConfig.KEY_SUCCESS,RestConfig.VALUE_FAILURE);
    }

    @Step("Set user email, password and name")
    public void setRequestEmailPasswordName(String email, String password, String name) {
        user.setEmail(email + "@test.ru");
        user.setPassword(password);
        user.setName(name);
    }

    @Step("Get authorized accessToken")
    public void getUserAccessTokenAuthorized(ValidatableResponse response) {
        userAccessTokenAuthorized = response.extract().body().path("accessToken");
    }

    @Step("Set accessToken")
    public void setUserAccessToken() {
        user.setAccessToken(userAccessTokenAuthorized);
    }

    @Step("Set ingredients for make order request")
    public void setOrderIngredients(ValidatableResponse response) {
        List<Object> data = response.extract().body().path("data");
        int count = 0;
        for (Object item : data) {
            if (count >= 2) {
                break;
            }
            if (item instanceof Map) {
                Map<?, ?> mapItem = (Map<?, ?>) item;
                String id = (String) mapItem.get("_id");
                ingredients.add(id);
                count ++;
            }
        }
        order.setIngredients(ingredients);
    }

    @Step("Set incorrect ingredients for make order request")
    public void setOrderIncorrectIngredients(String ingredient) {
        ingredients.add(ingredient);
        order.setIngredients(ingredients);
    }

    @After
    public void tearDown() {
        if (user.getAccessToken() != null) {
            userSteps.deleteUser(user);
        }
    }
}
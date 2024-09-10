package ordertests.createorder;

import dto.OrderCreateModel;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Test;
import steps.OrderSteps;
import steps.UserSteps;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.is;

public class AuthorizedUserCreateOrderFullTest {

    OrderSteps orderSteps = new OrderSteps();
    UserSteps userSteps = new UserSteps();
    private String email;
    private String password;
    private String token;


    @Test
    @DisplayName("Позитивный сценарий создания заказа авторизованным пользователем")
    @Description("Проверяем, что авторизованный пользователь может создать заказ, передав в теле хеши ингридиентов")
    public void authorizedCreateOrderSuccess() {

        //-----------------Создаем пользователя в системе и сохраняем токен авторизации--------------------

        email = "test-naz-" + RandomStringUtils.randomAlphabetic(3).toLowerCase() + "@yandex.ru";
        password = RandomStringUtils.randomAlphabetic(6);


        token = userSteps.userRegistration(email, password)
                .then()
                .statusCode(200)
                .body("accessToken", containsString("Bearer"))
                .extract().path("accessToken");

        //-----------------Получаем список всех ингридиентов и сохраняем его--------------------
        List<String> ids = orderSteps.getIngredients()
                .then()
                .statusCode(200)
                .extract()
                .path("data._id");

        //-----------------Перемешиваем элементы списка, тем самым обеспечивая изменение входных данных для теста--------------------
        Collections.shuffle(ids);

        //-----------------Создаем модель бади с двумя ингридиентами из списка --------------------
        String firstIngredient = ids.get(0);
        String secondIngredient = ids.get(1);
        OrderCreateModel order = new OrderCreateModel(List.of(firstIngredient, secondIngredient));

        //-----------------Создаем заказ авторизованным пользователем--------------------
        orderSteps.createOrder(order, token)
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("name", containsString("бургер"))
                .body("order.ingredients[0]._id", is(firstIngredient))
                .body("order.ingredients[1]._id", is(secondIngredient))
                .body("order._id", notNullValue())
                .body("order.owner.email", is(email));

    }

    @Test
    @DisplayName("Негативный сценарий создания заказа без ингридиентов авторизованным пользователем")
    @Description("Проверяем, что авторизованный пользователь не может создать заказ без ингредиентов")
    public void authorizedWithoutIngredients() {

        email = "test-naz-" + RandomStringUtils.randomAlphabetic(3).toLowerCase() + "@yandex.ru";
        password = RandomStringUtils.randomAlphabetic(6);

        //-----------------Создаем пользователя в системе и сохраняем токен авторизации--------------------
        token = userSteps.userRegistration(email, password)
                .then()
                .statusCode(200)
                .body("accessToken", containsString("Bearer"))
                .extract().path("accessToken");

        //-----------------Пытаемся создать заказ без авторизации и без ингридиентов--------------------
        orderSteps.createOrderWithoutIngredients(token)
                .then()
                .statusCode(400)
                .body("success", is(false))
                .body("message", is("Ingredient ids must be provided"));

    }


    @Test
    @DisplayName("Негативный сценарий создания заказа c инвалидыми хешами авторизованным пользователем")
    @Description("Проверяем, что авторизованный пользователь не может создать заказ, передав в теле несуществующие в БД хеши ингридиентов")
    public void authorizedInvalidHash() {

        email = "test-naz-" + RandomStringUtils.randomAlphabetic(3).toLowerCase() + "@yandex.ru";
        password = RandomStringUtils.randomAlphabetic(6);

        //-----------------Создаем пользователя в системе и сохраняем токен авторизации--------------------
        token = userSteps.userRegistration(email, password)
                .then()
                .statusCode(200)
                .body("accessToken", containsString("Bearer"))
                .extract().path("accessToken");

        //-----------------Создаем два инвалидных хэша--------------------
        String invalidHashOne = RandomStringUtils.randomAlphabetic(24).toLowerCase();
        String invalidHashTwo = RandomStringUtils.randomAlphabetic(24).toLowerCase();

        //-----------------Создаем бади с инвалидными хэшами--------------------
        OrderCreateModel order = new OrderCreateModel(List.of(invalidHashOne, invalidHashTwo));

        //-----------------Пытаемся создать заказ без авторизации с инвалидными хешами --------------------
        orderSteps.createOrder(order, token)
                .then()
                .statusCode(500);

    }

    //-----------------Удаляем пользователя из БД после теста--------------------
    @After
    public void deleteUser(){
        userSteps.userDelete(token);
    }


}

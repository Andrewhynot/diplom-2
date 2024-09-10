package ordertests.getuserorders;

import dto.OrderCreateModel;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Test;
import steps.OrderSteps;
import steps.UserSteps;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;

public class GetUserOrdersTest {

    OrderSteps orderSteps = new OrderSteps();
    UserSteps userSteps = new UserSteps();
    private String email;
    private String password;
    private String token;

    @Test
    @DisplayName("Позитивный сценарий возврат заказов авторизованного пользователя")
    @Description("Проверяем, что ручка возвращает заказы, которые создавал авторизованный в системе пользователь ")
    public void getOrdersNotEmpty() {

        email = "test-naz-" + RandomStringUtils.randomAlphabetic(3).toLowerCase() + "@yandex.ru";
        password = RandomStringUtils.randomAlphabetic(6);

        //-----------------Создаем нового пользователя-----------------
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

        //-----------------Создаем заказ авторизованным пользователем + записываем айди заказа--------------------
        String orderID = orderSteps.createOrder(order, token)
                .then()
                .statusCode(200)
                .body("success", is(true))
                .extract()
                .path("order._id");

        //-----------------Проверяем, что ручка вернула созданный ранее заказ--------------------
        orderSteps.getUserOrders(token)
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("orders[0]._id", is(orderID));

    }


    @Test
    @DisplayName("Позитивный сценарий возврат заказов авторизованного пользователя, но без сделанных заказов")
    @Description("Проверяем, что ручка возвращает заказы, которые создавал авторизованный в системе пользователь ")
    public void getOrdersEmpty() {

        email = "test-naz-" + RandomStringUtils.randomAlphabetic(3).toLowerCase() + "@yandex.ru";
        password = RandomStringUtils.randomAlphabetic(6);

        //-----------------Создаем нового пользователя, без заказов-----------------
        token = userSteps.userRegistration(email, password)
                .then()
                .statusCode(200)
                .body("accessToken", containsString("Bearer"))
                .extract().path("accessToken");

        //-----------Проверяем, что ручка отрабатывает без ошибки, даже если у пользовтеля нет заказов-----------------
        orderSteps.getUserOrders(token)
                .then()
                .statusCode(200)
                .body("success", is(true));
    }

    //-----------------удаляем пользователя из БД-----------------
    @After
    public void deleteUser() {
        userSteps.userDelete(token);
    }

}

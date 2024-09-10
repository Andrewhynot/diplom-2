package ordertests.createorder;

import dto.OrderCreateModel;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import steps.OrderSteps;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;


public class UnauthorizedUserCreateOrderFullTest {

    OrderSteps orderSteps = new OrderSteps();

    @Test
    @DisplayName("Позитивный сценарий создания заказа неавторизованным пользователем")
    @Description("Проверяем, что неавторизованный пользователь может создать заказ, передав в теле хеши ингридиентов")
    public void unauthorizedCreateOrderSuccess() {

        //-----------------Получаем список всех ингридиентов и сохраняем его--------------------
        List<String> ids = orderSteps.getIngredients()
                .then()
                .statusCode(200)
                .extract()
                .path("data._id");

        //-----------------Перемешиваем элементы списка, тем самым обеспечивая изменение входных данных для теста--------------------
        Collections.shuffle(ids);

        //-----------------Создаем модель бади с двумя ингридиентами из списка --------------------
        OrderCreateModel order = new OrderCreateModel(List.of(ids.get(0), ids.get(1)));

        //-----------------Создаем заказ без авторизации --------------------
        orderSteps.createOrder(order, "")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("name", containsString("бургер"))
                .body("order.number", notNullValue());

    }

    @Test
    @DisplayName("Негативный сценарий создания заказа без ингридиентов неавторизованным пользователем")
    @Description("Проверяем, что неавторизованный пользователь не может создать заказ без ингредиентов")
    public void unauthorizedWithoutIngredients() {


        //-----------------Пытаемся создать заказ без авторизации и без ингридиентов--------------------
        orderSteps.createOrderWithoutIngredients("")
                .then()
                .statusCode(400)
                .body("success", is(false))
                .body("message", is("Ingredient ids must be provided"));

    }


    @Test
    @DisplayName("Негативный сценарий создания заказа c инвалидыми хешами неавторизованным пользователем")
    @Description("Проверяем, что неавторизованный пользователь не может создать заказ, передав в теле несуществующие в БД хеши ингридиентов")
    public void unauthorizedInvalidHash() {

        //-----------------Создаем два инвалидных хэша--------------------
        String invalidHashOne = RandomStringUtils.randomAlphabetic(24).toLowerCase();
        String invalidHashTwo = RandomStringUtils.randomAlphabetic(24).toLowerCase();

        //-----------------Создаем бади с инвалидными хэшами--------------------
        OrderCreateModel order = new OrderCreateModel(List.of(invalidHashOne, invalidHashTwo));

        //-----------------Пытаемся создать заказ без авторизации с инвалидными хешами --------------------
        orderSteps.createOrder(order, "")
                .then()
                .statusCode(500);

    }

}


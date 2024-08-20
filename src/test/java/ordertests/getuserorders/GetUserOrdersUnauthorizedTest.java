package ordertests.getuserorders;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import steps.OrderSteps;

import static org.hamcrest.CoreMatchers.is;

public class GetUserOrdersUnauthorizedTest {

    OrderSteps orderSteps = new OrderSteps();

    @Test
    @DisplayName("Негативный сценарий получения заказов неавторизованного пользователя")
    @Description("Проверяем, что невозможно посмотреть заказы пользователя, который не авторизован в системе")
    public void getOrdersUnauthorized() {

        orderSteps.getUserOrders("")
                .then()
                .statusCode(401)
                .body("success", is(false))
                .body("message", is("You should be authorised"));

    }

}


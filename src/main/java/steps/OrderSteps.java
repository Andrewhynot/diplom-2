package steps;

import dto.OrderCreateModel;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static constants.Constants.*;
import static io.restassured.RestAssured.given;

public class OrderSteps {

    @Step("Шаг получение списка всех доступных ингридиентов")
    public Response getIngredients() {
        return given().log().all()
                .contentType(ContentType.JSON)
                .baseUri(URL)
                .when()
                .get(INGREDIENTS);
    }

    @Step("Шаг создания заказа с ингридиентами")
    public Response createOrder(OrderCreateModel order, String token) {
        return given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .baseUri(URL)
                .body(order)
                .when()
                .post(ORDER);
    }

    @Step("Шаг создания заказа с без ингридиентов")
    public Response createOrderWithoutIngredients(String token) {
        return given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .baseUri(URL)
                .when()
                .post(ORDER);
    }

    @Step("Шаг получения заказов пользователя")
    public Response getUserOrders(String token) {
        return given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .baseUri(URL)
                .when()
                .get(ORDER);
    }

}

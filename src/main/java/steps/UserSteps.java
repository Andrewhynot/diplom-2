package steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static constants.Constants.*;
import static io.restassured.RestAssured.given;


public class UserSteps {

    @Step("Шаг регистрации пользователя в приложении")
    public Response userRegistration(String email, String password) {
        return given().log().all()
                .header("Content-type", "application/json")
                .baseUri(URL)
                .body("{\n" +
                        "    \"email\": \"" + email + "\",\n" +
                        "    \"password\": \"" + password + "\",\n" +
                        "    \"name\": \"" + email + "\"\n" +
                        "}")
                .when()
                .post(REGISTER);
    }

    @Step("Шаг авторизации пользователя в приложении")
    public Response userAuthorization(String email, String password) {
        return given().log().all()
                .header("Content-type", "application/json")
                .baseUri(URL)
                .body("{\n" +
                        "    \"email\": \"" + email + "\",\n" +
                        "    \"password\": \"" + password + "\"\n" +
                        "}")
                .when()
                .post(LOGIN);
    }

    @Step("Шаг изменения данных пользователя приложения")
    public Response userChanging(String fieldName, String newValue, String token) {
        return given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .baseUri(URL)
                .body("{\n" +
                        "    \"" + fieldName + "\": \"" + newValue + "\"\n" +
                        "}")
                .when()
                .patch(UPDATE_OR_DELETE);
    }

    @Step("Шаг удаления пользователя из приложения")
    public Response userDelete(String token) {
        return given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .baseUri(URL)
                .when()
                .delete(UPDATE_OR_DELETE);
    }

}

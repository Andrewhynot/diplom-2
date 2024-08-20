package usertests.registration;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import static constants.Constants.REGISTER;
import static constants.Constants.URL;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

public class UserRegistrationNegativeTest {

    private String email;
    private String password;

    @Test
    @DisplayName("Негативный сценарий регистрации пользователя без обязательных полей")
    @Description("Проверяем, что без обязательных полей email, name или password пользователь не может быть создан в системе")
    public void userRegistrationWithoutRequiredField() {

        email = "test-naz-" + RandomStringUtils.randomAlphabetic(3).toLowerCase() + "@yandex.ru";
        password = RandomStringUtils.randomAlphabetic(6);

        //-----------------Почта и имя без пароля--------------------
        given()
                .header("Content-type", "application/json")
                .baseUri(URL)
                .body("{\n" +
                        "    \"email\": \"" + email + "\",\n" +
                        "    \"name\": \"" + email + "\"\n" +
                        "}")
                .when()
                .post(REGISTER)
                .then()
                .statusCode(403)
                .assertThat()
                .body("success", is(false))
                .and()
                .body("message", is("Email, password and name are required fields"));

        //-----------------Почта и пароль без имени-----------------
        given()
                .header("Content-type", "application/json")
                .baseUri(URL)
                .body("{\n" +
                        "    \"email\": \"" + email + "\",\n" +
                        "    \"password\": \"" + password + "\"\n" +
                        "}")
                .when()
                .post(REGISTER)
                .then()
                .statusCode(403)
                .assertThat()
                .body("success", is(false))
                .and()
                .body("message", is("Email, password and name are required fields"));

        //-----------------Имя и пароль без почты-----------------
        given()
                .header("Content-type", "application/json")
                .baseUri(URL)
                .body("{\n" +
                        "    \"name\": \"" + email + "\",\n" +
                        "    \"password\": \"" + password + "\"\n" +
                        "}")
                .when()
                .post(REGISTER)
                .then()
                .statusCode(403)
                .assertThat()
                .body("success", is(false))
                .and()
                .body("message", is("Email, password and name are required fields"));
    }

}

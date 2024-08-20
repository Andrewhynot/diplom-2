package usertests.authorization;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Test;
import steps.UserSteps;

import static org.hamcrest.CoreMatchers.*;

public class UserAuthorizationFullTest {

    private UserSteps userSteps = new UserSteps();
    private String email;
    private String password;
    private String token;

    @Test
    @DisplayName("Позитивный сценарий авторизации пользователя")
    @Description("Проверяем, что при верном пароле и логине у созданного пользователя происходит успешная авторизация")
    public void userAuthorizationSuccess() {

        email = "test-naz-" + RandomStringUtils.randomAlphabetic(3).toLowerCase() + "@yandex.ru";
        password = RandomStringUtils.randomAlphabetic(6);

        userSteps.userRegistration(email, password)
                .then()
                .statusCode(200)
                .body("accessToken", containsString("Bearer"));


        token = userSteps.userAuthorization(email, password)
                .then()
                .statusCode(200)
                .assertThat()
                .body("success", is(true))
                .body("user.email", is(email))
                .body("user.name", is(email))
                .body("accessToken", containsString("Bearer"))
                .body("refreshToken", notNullValue())
                .extract().path("accessToken");

    }

    @Test
    @DisplayName("Негативный сценарий авторизации неправильный пароль")
    @Description("Проверяем, что при неверном пароле у созданного пользователя НЕ происходит авторизации")
    public void userAuthorizationInvalidPassword() {

        email = "test-naz-" + RandomStringUtils.randomAlphabetic(3).toLowerCase() + "@yandex.ru";
        password = RandomStringUtils.randomAlphabetic(6);

        token = userSteps.userRegistration(email, password)
                .then()
                .statusCode(200)
                .body("accessToken", containsString("Bearer"))
                .extract().path("accessToken");

        password = RandomStringUtils.randomAlphabetic(6);

        userSteps.userAuthorization(email, password)
                .then()
                .statusCode(401)
                .assertThat()
                .body("success", is(false))
                .and()
                .body("message", is("email or password are incorrect"));

    }

    @Test
    @DisplayName("Негативный сценарий авторизации неправильный логин")
    @Description("Проверяем, что при неверном пароле у созданного пользователя НЕ происходит авторизации")
    public void userAuthorizationInvalidEmail() {

        email = "test-naz-" + RandomStringUtils.randomAlphabetic(3).toLowerCase() + "@yandex.ru";
        password = RandomStringUtils.randomAlphabetic(6);

        token = userSteps.userRegistration(email, password)
                .then()
                .statusCode(200)
                .body("accessToken", containsString("Bearer"))
                .extract().path("accessToken");

        email = "test-naz-" + RandomStringUtils.randomAlphabetic(3).toLowerCase() + "@yandex.ru";

        userSteps.userAuthorization(email, password)
                .then()
                .statusCode(401)
                .assertThat()
                .body("success", is(false))
                .and()
                .body("message", is("email or password are incorrect"));

    }

    @After
    public void deleteUser() {
        userSteps.userDelete(token);
    }

}




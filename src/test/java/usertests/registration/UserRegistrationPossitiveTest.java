package usertests.registration;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Test;
import steps.UserSteps;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

public class UserRegistrationPossitiveTest {

    private UserSteps userSteps = new UserSteps();
    private String email;
    private String password;
    private String token;


    @Test
    @DisplayName("Позитивный сценарий регистрации пользователя")
    @Description("Проверяем, что при запросе с именем, имейлом и паролем происходит успешная регистрация пользователя в системе")
    public void userRegistrationSuccess() {

        email = "test-naz-" + RandomStringUtils.randomAlphabetic(3).toLowerCase() + "@yandex.ru";
        password = RandomStringUtils.randomAlphabetic(6);

        token = userSteps.userRegistration(email, password)
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
    @DisplayName("Негативный сценарий повторной регистрации пользователя")
    @Description("Проверяем, что при повторной регистрации с идентичными данными пользователь не может быть создан в системе")
    public void userRegistrationTwice() {

        email = "test-naz-" + RandomStringUtils.randomAlphabetic(3).toLowerCase() + "@yandex.ru";
        password = RandomStringUtils.randomAlphabetic(6);

        token = userSteps.userRegistration(email, password)
                .then()
                .statusCode(200)
                .body("accessToken", containsString("Bearer"))
                .extract().path("accessToken");

        userSteps.userRegistration(email, password)
                .then()
                .statusCode(403)
                .assertThat()
                .body("success", is(false))
                .body("message", is("User already exists"));

    }


    @After
    public void deleteUser(){
        userSteps.userDelete(token);
    }

}
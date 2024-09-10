package usertests.changedata;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import steps.UserSteps;

import static org.hamcrest.CoreMatchers.is;

public class UserChangeDataNegativeTest {

    private UserSteps userSteps = new UserSteps();
    private String email;
    private String name;
    private String password;
    private final String emailField = "email";
    private final String nameField = "name";
    private final String passField = "password";


    @Test
    @DisplayName("Негативный сценарий изменения имени, без авторизационного токена")
    @Description("Проверяем, что без авторизации нельзя менять имя пользователя")
    public void changeNameFieldFalse() {

        name = "test-naz-" + RandomStringUtils.randomAlphabetic(3).toLowerCase() + "-name";

        userSteps.userChanging(nameField, name, "")
                .then()
                .statusCode(401)
                .assertThat()
                .body("success", is(false))
                .body("message", is("You should be authorised"));


    }

    @Test
    @DisplayName("Негативный сценарий изменения почты, без авторизационного токена")
    @Description("Проверяем, что без авторизации нельзя менять почту пользователя")
    public void changeEmailFieldFalse() {

        email = "test-naz-" + RandomStringUtils.randomAlphabetic(3).toLowerCase() + "@yandex.ru";

        userSteps.userChanging(emailField, email, "")
                .then()
                .statusCode(401)
                .assertThat()
                .body("success", is(false))
                .body("message", is("You should be authorised"));

    }

    @Test
    @DisplayName("Негативный сценарий изменения пароля, без авторизационного токена")
    @Description("Проверяем, что без авторизации нельзя менять пароль пользователя")
    public void changePassFieldFalse() {

        password = RandomStringUtils.randomAlphabetic(6);

        userSteps.userChanging(passField, password, "")
                .then()
                .statusCode(401)
                .assertThat()
                .body("success", is(false))
                .body("message", is("You should be authorised"));

    }

}
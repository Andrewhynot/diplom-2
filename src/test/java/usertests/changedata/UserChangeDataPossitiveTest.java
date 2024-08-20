package usertests.changedata;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Test;
import steps.UserSteps;

import static org.hamcrest.CoreMatchers.*;

public class UserChangeDataPossitiveTest {

    private UserSteps userSteps = new UserSteps();
    private String email;
    private String password;
    private String token;
    private final String emailField = "email";
    private final String nameField = "name";
    private final String passField = "password";


    @Test
    @DisplayName("Позитивный сценарий изменения почты пользователя")
    @Description("Проверяем, что при выполненной ранее авторизации у пользователя есть возможность изменить имейл")
    public void changeMailFieldSuccess() {

        email = "test-naz-" + RandomStringUtils.randomAlphabetic(3).toLowerCase() + "@yandex.ru";
        password = RandomStringUtils.randomAlphabetic(6);

        token = userSteps.userRegistration(email, password)
                .then()
                .statusCode(200)
                .assertThat()
                .body("accessToken", containsString("Bearer"))
                .extract().path("accessToken");

        String newEmail = "test-naz-" + RandomStringUtils.randomAlphabetic(3).toLowerCase() + "@mail.ru";

        userSteps.userChanging(emailField, newEmail, token)
                .then()
                .statusCode(200)
                .assertThat()
                .body("success", is(true))
                .body("user.email", is(newEmail));


    }

    @Test
    @DisplayName("Позитивный сценарий изменения имени пользователя")
    @Description("Проверяем, что при выполненной ранее авторизации у пользователя есть возможность изменить имя")
    public void changeNameFieldSuccess() {

        email = "test-naz-" + RandomStringUtils.randomAlphabetic(3).toLowerCase() + "@yandex.ru";
        password = RandomStringUtils.randomAlphabetic(6);

        token = userSteps.userRegistration(email, password)
                .then()
                .statusCode(200)
                .assertThat()
                .body("accessToken", containsString("Bearer"))
                .extract().path("accessToken");

        String newName = "test-naz-" + RandomStringUtils.randomAlphabetic(3).toLowerCase() + "-name";

        userSteps.userChanging(nameField, newName, token)
                .then()
                .statusCode(200)
                .assertThat()
                .body("success", is(true))
                .body("user.name", is(newName));

    }

    @Test
    @DisplayName("Позитивный сценарий изменения пароля пользователя")
    @Description("Проверяем, что при выполненной ранее авторизации у пользователя есть возможность изменить пароль")
    public void changePassFieldSuccess() {

        email = "test-naz-" + RandomStringUtils.randomAlphabetic(3).toLowerCase() + "@yandex.ru";
        password = RandomStringUtils.randomAlphabetic(6);

//------------------------Создаем нового пользователя и сохраняем бирир токен.
        token = userSteps.userRegistration(email, password)
                .then()
                .statusCode(200)
                .assertThat()
                .body("accessToken", containsString("Bearer"))
                .extract().path("accessToken");

        String newPass = RandomStringUtils.randomAlphabetic(6);

//------------------------Проверяем возможность изменить пароль при помощи ручки и токена.
        userSteps.userChanging(passField, newPass, token)
                .then()
                .statusCode(200)
                .assertThat()
                .body("success", is(true));

//------------------------Проверяем успешную авторизацию с новым паролем.
        token = userSteps.userAuthorization(email, newPass)
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


    @After
    public void deleteUser(){
        userSteps.userDelete(token);
    }


}

package com.serenitydojo.playwright.toolshop.login;

import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.serenitydojo.playwright.toolshop.domain.User;
import com.serenitydojo.playwright.toolshop.fixtures.PlaywrightTestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.serenitydojo.playwright.toolshop.domain.User.randomUser;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class LoginWithRegisteredUserTest extends PlaywrightTestCase {

    @Test
    @DisplayName("Should be able to login with registered user")
    void should_be_able_to_login_with_registered_user() {
//        Register a user via API
        User user = randomUser();
        UserAPIClient userAPIClient = new UserAPIClient(page);
        userAPIClient.registerUser(user);

//        Login via LoginPage
        LoginPage loginPage = new LoginPage(page);
        loginPage.open();
        loginPage.loginAs(user);

        //        Check that we are on the right page
        assertThat(loginPage.title())
                .as("Page title is incorrect.")
                .isEqualTo("My account");
    }

    @Test
    @DisplayName("Should reject login with with wrong password")
    void should_reject_login_with_wrong_password() {
//        Register a user via API
        User user = User.randomUser();
        UserAPIClient userAPIClient = new UserAPIClient(page);
        userAPIClient.registerUser(user);

//        Login via LoginPage
        LoginPage loginPage = new LoginPage(page);
        loginPage.open();
        loginPage.loginAs(user.randomUserWithSpecificPassword("wrong-password"));

        //        Check that we are on the right page
        assertThat(loginPage.getAlertText())
                .as("Incorrect text.")
                .isEqualToIgnoringCase("Invalid email or password");
        PlaywrightAssertions.assertThat(loginPage.getAlert()).hasText("Invalid email or password");
        PlaywrightAssertions.assertThat(loginPage.getAlert()).isVisible();
    }
}

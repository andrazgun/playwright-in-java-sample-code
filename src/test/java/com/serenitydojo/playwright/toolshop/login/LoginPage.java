package com.serenitydojo.playwright.toolshop.login;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.serenitydojo.playwright.toolshop.domain.User;
import com.serenitydojo.playwright.toolshop.fixtures.PlaywrightTestCase;

public class LoginPage extends PlaywrightTestCase {
    private final Page page;

    public LoginPage(Page page) {
        this.page = page;
    }

    public void open() {
        page.navigate("https://www.practicesoftwaretesting.com/auth/login");
    }

    public void loginAs(User user) {
        page.getByPlaceholder("Your email").fill(user.email());
        page.getByPlaceholder("Your password").fill(user.password());
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login")).click();
    }

    public String title() {
        return page.getByTestId("page-title").textContent();
    }

    public String getAlertText() {
        return page.getByTestId("login-error").textContent().trim();
    }

    public Locator getAlert() {
        return page.getByTestId("login-error");
    }
}

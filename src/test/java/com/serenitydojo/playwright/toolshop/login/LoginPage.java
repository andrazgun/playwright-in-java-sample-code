package com.serenitydojo.playwright.toolshop.login;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.serenitydojo.playwright.toolshop.domain.User;
import com.serenitydojo.playwright.toolshop.fixtures.PlaywrightTestCase;
import io.qameta.allure.Step;

import static com.serenitydojo.playwright.toolshop.fixtures.ScreenshotManager.takeScreenshot;

public class LoginPage extends PlaywrightTestCase {
    private final Page page;

    public LoginPage(Page page) {
        this.page = page;
    }

    @Step
    public void openLoginPage() {
        page.navigate("https://www.practicesoftwaretesting.com/auth/login");
    }

    @Step
    public void loginAs(User user) {
        page.getByPlaceholder("Your email").fill(user.email());
        page.getByPlaceholder("Your password").fill(user.password());
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login")).click();
    }

    @Step
    public String getPageTitle() {
        takeScreenshot(page, "get page title");
        return page.getByTestId("page-title").textContent();
    }

    @Step
    public String getAlertText() {
        takeScreenshot(page, "get Alert text");
        return page.getByTestId("login-error").textContent().trim();
    }

    @Step
    public Locator getAlert() {
        return page.getByTestId("login-error");
    }
}

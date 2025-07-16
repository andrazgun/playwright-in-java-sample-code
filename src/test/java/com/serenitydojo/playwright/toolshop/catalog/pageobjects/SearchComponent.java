package com.serenitydojo.playwright.toolshop.catalog.pageobjects;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

import static com.serenitydojo.playwright.toolshop.fixtures.ScreenshotManager.takeScreenshot;

public class SearchComponent {

    private final Page page;

    public SearchComponent(Page page) {
        this.page = page;
    }

    @Step
    public void searchBy(String keyword) {
        page.waitForResponse("**/products/search?q=" + keyword, () -> {
            page.getByPlaceholder("Search").fill(keyword);
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();
            takeScreenshot(page, keyword);
        });
    }

    @Step
    public void clearSearch() {
        page.waitForResponse("**/products**", () -> page.getByTestId("search-reset").click());
    }
}

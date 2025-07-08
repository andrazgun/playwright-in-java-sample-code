package com.serenitydojo.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ASimplePlaywrightTest {

    @Test
    void shouldShowThePageTitle() {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch();
        Page page = browser.newPage();
        page.navigate("https://www.practicesoftwaretesting.com/");
        String title = page.title();
        assertThat(title)
                .withFailMessage("Page title is incorrect")
                .containsIgnoringCase("Practice Software Testing");
        assertThat(title).as("Page title is %s ", title)
                .contains("Practice Software Testing");

        browser.close();
        playwright.close();
    }

    @Test
    void shouldSearchByKeyword() {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch();
        Page page = browser.newPage();
        page.navigate("https://www.practicesoftwaretesting.com/");
        page.locator("[placeholder=Search]").fill("Pliers");
        page.locator("button:has-text('Search')").click();
        int matchingSearchResults = page.locator(".card").count();
        assertThat(matchingSearchResults)
                .as("Result count should be greater than %d, but it wasn't.", 0)
                .isGreaterThan(0);
        browser.close();
        playwright.close();
    }
}

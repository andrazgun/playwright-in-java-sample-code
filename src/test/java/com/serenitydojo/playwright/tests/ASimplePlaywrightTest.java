package com.serenitydojo.playwright.tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ASimplePlaywrightTest {

    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext browserContext;

    Page page;

    @BeforeAll
    public static void setUpBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setArgs(Arrays.asList("--no-sandbox","--disable-extensions","--disable-gpu"))
        );
        browserContext = browser.newContext();
    }

    @BeforeEach
    public void setUp() {
        page = browserContext.newPage();
    }

    @AfterAll
    public static void tearDown() {
        browser.close();
        playwright.close();
    }

    @Test
    void shouldShowThePageTitle() {
        page.navigate("https://www.practicesoftwaretesting.com/");
        String title = page.title();
        assertThat(title)
                .withFailMessage("Page title is incorrect")
                .containsIgnoringCase("Practice Software Testing");
        assertThat(title).as("Page title is %s ", title)
                .contains("Practice Software Testing");
    }

    @Test
    void shouldSearchByKeyword() {
        page.navigate("https://www.practicesoftwaretesting.com/");
        page.locator("[placeholder=Search]").fill("Pliers");
        page.locator("button:has-text('Search')").click();
        int matchingSearchResults = page.locator(".card").count();
        assertThat(matchingSearchResults)
                .as("Result count should be greater than %d, but it wasn't.", 0)
                .isGreaterThan(0);
    }
}

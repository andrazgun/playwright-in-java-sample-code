package com.serenitydojo.playwright.tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AddingProductsToCartTest {

    protected static Playwright playwright;
    protected static Browser browser;
    protected static BrowserContext browserContext;

    Page page;

    @BeforeAll
    static void setUpBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
                        .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu"))
        );

        playwright.selectors().setTestIdAttribute("data-test");
    }

    @BeforeEach
    void setUp() {
        browserContext = browser.newContext();
        page = browserContext.newPage();
    }

    @AfterEach
    void closeContext() {
        browserContext.close();
    }

    @AfterAll
    static void tearDown() {
        browser.close();
        playwright.close();
    }

    @DisplayName("Search for pliers")
    @Test
    void searchForPliers() {
        page.navigate("https://practicesoftwaretesting.com/");
        page.locator("#search-query").fill("Pliers");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();

        List<Integer> searchResultTileList = Collections.singletonList(page.locator(".card").count());
        List<String> searchResultList = page.getByTestId("product-name").allTextContents();
        assertThat(searchResultTileList)
                .as("Search result list shouldn't be empty, but it was.").hasSizeGreaterThan(0);
        assertThat(searchResultList)
                .as("Search result list should contain only pliers, but it did not.")
                .anyMatch(name -> name.contains("Pliers"));

        Locator outOfStockItems = page.locator(".card")
                .filter(new Locator.FilterOptions().setHasText("Out of stock"))
                .getByTestId("product-name");

        assertThat(outOfStockItems.count()).isGreaterThan(0);
        assertThat(outOfStockItems.textContent())
                .as("Text was incorrect.")
                .containsIgnoringCase("Long Nose Pliers");
    }
}

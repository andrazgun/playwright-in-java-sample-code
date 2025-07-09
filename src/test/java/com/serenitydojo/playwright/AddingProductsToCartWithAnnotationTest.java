package com.serenitydojo.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.junit.UsePlaywright;
import org.junit.jupiter.api.*;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@UsePlaywright(HeadlessChromeOption.class)
public class AddingProductsToCartWithAnnotationTest {

    @DisplayName("Search for pliers")
    @Test
    void searchForPliers(Page page, Playwright playwright, Browser browser, BrowserContext browserContext) {
        page.navigate("https://practicesoftwaretesting.com/");
        page.locator("#search-query").fill("Pliers");
        page.getByTestId("search-submit").click();

        page.waitForLoadState();
        page.waitForCondition( () -> page.getByTestId("product-name").count() > 0);

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

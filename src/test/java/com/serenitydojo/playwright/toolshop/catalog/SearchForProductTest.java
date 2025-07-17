package com.serenitydojo.playwright.toolshop.catalog;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import com.microsoft.playwright.junit.UsePlaywright;
import com.serenitydojo.playwright.tests.HeadlessChromeOption;
import com.serenitydojo.playwright.toolshop.catalog.pageobjects.*;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import java.nio.file.Paths;

@UsePlaywright(HeadlessChromeOption.class) //extends PlaywrightTestCase not needed if this annotation is used
@DisplayName("Search by keyword")
@Feature("Product catalog")
public class SearchForProductTest {

    SearchComponent searchComponent;
    ProductList productList;
    ProductDetails productDetails;
    NavBar navBar;
    CheckoutCart checkoutCart;

    @BeforeEach
    @Step
    void openHomePage(Page page) {
        page.navigate("https://practicesoftwaretesting.com");
    }

    @BeforeEach
    void setUp(Page page) {
        searchComponent = new SearchComponent(page);
        productList = new ProductList(page);
        productDetails = new ProductDetails(page);
        navBar = new NavBar(page);
        checkoutCart = new CheckoutCart(page);
    }

    @BeforeEach
    void setupTrace(BrowserContext context) {
        context.tracing().start(
                new Tracing.StartOptions()
                        .setScreenshots(true)
                        .setSnapshots(true)
                        .setSources(true)
        );
    }

    @AfterEach
    void recordTrace(TestInfo testInfo, BrowserContext context) {
        String traceName = testInfo.getDisplayName().replace(" ", "-").toLowerCase();
        context.tracing().stop(
                new Tracing.StopOptions()
                        .setPath(Paths.get("target/traces/trace-" + traceName + ".zip"))
        );
//        to see the trace execute in CLI: npx playwright show-trace "trace file"
//        or go to https://trace.playwright.dev/ and open the trace file
    }

    @DisplayName("Search With Page Objects")
    @Test
    void withPageObjects01() {

        searchComponent.searchBy("tape");
        var matchingProducts = productList.getProductNameList();

        Assertions.assertThat(matchingProducts)
                .contains("Tape Measure 7.5m", "Measuring Tape", "Tape Measure 5m");
    }

    @Test
    @DisplayName("When there are no matching results")
    void whenThereIsNoMatchingProduct() {
        searchComponent.searchBy("unknown");

        var matchingProducts = productList.getProductNameList();

        Assertions.assertThat(matchingProducts).isEmpty();
        Assertions.assertThat(productList.getSearchCompletedMessage()).contains("There are no products found.");
    }

    @Test
    @DisplayName("When the user clears a previous search results")
    void clearingTheSearchResults() {
        searchComponent.searchBy("saw");

        var matchingFilteredProducts = productList.getProductNameList();
        Assertions.assertThat(matchingFilteredProducts).hasSize(2);

        searchComponent.clearSearch();

        var matchingProducts = productList.getProductNameList();
        Assertions.assertThat(matchingProducts).hasSize(9);
    }
}

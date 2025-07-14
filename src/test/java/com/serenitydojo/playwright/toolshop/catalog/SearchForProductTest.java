package com.serenitydojo.playwright.toolshop.catalog;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import com.serenitydojo.playwright.HeadlessChromeOption;
import com.serenitydojo.playwright.toolshop.catalog.pageobjects.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@UsePlaywright(HeadlessChromeOption.class) //extends PlaywrightTestCase not needed if this annotation is used
@DisplayName("Searching for products")
public class SearchForProductTest {

    SearchComponent searchComponent;
    ProductList productList;
    ProductDetails productDetails;
    NavBar navBar;
    CheckoutCart checkoutCart;

    @BeforeEach
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

    @DisplayName("Search With Page Objects")
    @Test
    void withPageObjects01() {

        searchComponent.searchBy("tape");
        var matchingProducts = productList.getProductName();

        Assertions.assertThat(matchingProducts)
                .contains("Tape Measure 7.5m", "Measuring Tape", "Tape Measure 5m");
    }

    @Test
    @DisplayName("When there are no matching results")
    void whenThereIsNoMatchingProduct() {
        searchComponent.searchBy("unknown");

        var matchingProducts = productList.getProductName();

        Assertions.assertThat(matchingProducts).isEmpty();
        Assertions.assertThat(productList.getSearchCompletedMessage()).contains("There are no products found.");
    }

    @Test
    @DisplayName("When the user clears a previous search results")
    void clearingTheSearchResults() {
        searchComponent.searchBy("saw");

        var matchingFilteredProducts = productList.getProductName();
        Assertions.assertThat(matchingFilteredProducts).hasSize(2);

        searchComponent.clearSearch();

        var matchingProducts = productList.getProductName();
        Assertions.assertThat(matchingProducts).hasSize(9);
    }
}

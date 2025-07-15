package com.serenitydojo.playwright.toolshop.catalog;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import com.microsoft.playwright.junit.UsePlaywright;
import com.serenitydojo.playwright.tests.HeadlessChromeOption;
import com.serenitydojo.playwright.toolshop.catalog.pageobjects.*;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import java.nio.file.Paths;
import java.util.List;

@UsePlaywright(HeadlessChromeOption.class)
@DisplayName("Shopping cart")
@Feature("Cart")
public class AddToCartTest {

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
    void recordTrace(TestInfo testInfo,BrowserContext context) {
        String traceName = testInfo.getDisplayName().replace(" ", "-").toLowerCase();
        context.tracing().stop(
                new Tracing.StopOptions()
                        .setPath(Paths.get("target/traces/trace-" + traceName + ".zip"))
        );
//        to see the trace execute in CLI: npx playwright show-trace "trace file"
//        or go to https://trace.playwright.dev/ and open the trace file
    }

    @DisplayName("Add to cart With Page Objects")
    @Story("Add to cart")
    @Test
    void withPageObjects02() {

        String keyword = "pliers";
        String productName = "Combination Pliers";
        int productQuantity = 2;

        searchComponent.searchBy(keyword);
        productList.viewProductDetails(productName);
        productDetails.increaseQuantity(productQuantity);
        productDetails.addToCart();
        navBar.openCart();

        List<CartLineItem> lineItems = checkoutCart.getLineItem();

        Assertions.assertThat(lineItems)
                .hasSize(1)
                .first()
                .satisfies(item -> {
                    Assertions.assertThat(item.title()).contains(productName);
                    Assertions.assertThat(item.quantity()).isEqualTo(3);
                    Assertions.assertThat(item.total()).isEqualTo(item.quantity() * item.price());
                });
    }

    @DisplayName("Checking out multiple items")
    @Test
    void whenCheckingOutMultipleItems() {
        productList.viewProductDetails("Bolt Cutters");
        productDetails.increaseQuantity(2);
        productDetails.addToCart();

        navBar.openHomepage();
        productList.viewProductDetails("Claw hammer with Shock Reduction Grip");
        productDetails.addToCart();
        navBar.openCart();

        List<CartLineItem> lineItems = checkoutCart.getLineItem();
        Assertions.assertThat(lineItems).hasSize(2);

        List<String> productNames = lineItems.stream().map(CartLineItem::title).toList();
        Assertions.assertThat(productNames).contains("Bolt Cutters", "Claw Hammer with Shock Reduction Grip");

        Assertions.assertThat(lineItems)
                .allSatisfy(item -> {
                    Assertions.assertThat(item.quantity()).isGreaterThanOrEqualTo(1);
                    Assertions.assertThat(item.price()).isGreaterThan(0.0);
                    Assertions.assertThat(item.total()).isGreaterThan(1.0);
                    Assertions.assertThat(item.total()).isEqualTo(item.quantity() * item.price());
                });
    }
}

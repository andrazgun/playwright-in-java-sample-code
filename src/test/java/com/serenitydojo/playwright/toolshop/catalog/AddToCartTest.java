package com.serenitydojo.playwright.toolshop.catalog;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import com.serenitydojo.playwright.HeadlessChromeOption;
import com.serenitydojo.playwright.toolshop.catalog.pageobjects.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

@UsePlaywright(HeadlessChromeOption.class)
public class AddToCartTest {

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

    @DisplayName("Add to cart With Page Objects")
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

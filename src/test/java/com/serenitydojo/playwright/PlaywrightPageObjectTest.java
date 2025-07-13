package com.serenitydojo.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.AriaRole;
import com.serenitydojo.playwright.toolshop.fixtures.PlaywrightTestCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

public class PlaywrightPageObjectTest extends PlaywrightTestCase {

/*    Constructor not needed as page is taken from PlaywrightTestCase
    protected page

    private final Page page;

    public PlaywrightPageObjectTest(Page page) {
        this.page = page;
    }*/

    @BeforeEach
    public void openHomePage() {
        page.navigate("https://practicesoftwaretesting.com");
    }

    @Nested
    class WhenSearchingProductsByKeyword {

        @DisplayName("Search Without Page Objects")
        @Test
        void withoutPageObjects01() {
            page.waitForResponse("**/products/search?q=tape", () -> {
                page.getByPlaceholder("Search").fill("tape");
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();
            });
            List<String> matchingProducts = page.getByTestId("product-name").allInnerTexts();
            Assertions.assertThat(matchingProducts)
                    .contains("Tape Measure 7.5m", "Measuring Tape", "Tape Measure 5m");
        }

        @DisplayName("Search With Page Objects")
        @Test
        void withPageObjects01() {
            SearchComponent searchComponent = new SearchComponent(page);
            ProductList productList = new ProductList(page);

            searchComponent.searchBy("tape");
            var matchingProducts = productList.getProductName();

            Assertions.assertThat(matchingProducts)
                    .contains("Tape Measure 7.5m", "Measuring Tape", "Tape Measure 5m");
        }

        @DisplayName("Add to cart Without Page Objects")
        @Test
        void withoutPageObjects02() {
            // Search for pliers
            page.waitForResponse("**/products/search?q=pliers", () -> {
                page.getByPlaceholder("Search").fill("pliers");
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();
            });
            // Show details page
            page.locator(".card").getByText("Combination Pliers").click();

            // Increase cart quanity
            page.getByTestId("increase-quantity").click();
            page.getByTestId("increase-quantity").click();
            // Add to cart
            page.getByText("Add to cart").click();
            page.waitForCondition(() -> page.getByTestId("cart-quantity").textContent().equals("3"));

            // Open the cart
            page.getByTestId("nav-cart").click();

            // check cart contents
            PlaywrightAssertions.assertThat(page.locator(".product-title").getByText("Combination Pliers")).isVisible();
            PlaywrightAssertions.assertThat(page.getByTestId("cart-quantity").getByText("3")).isVisible();
        }

        @DisplayName("Add to cart With Page Objects")
        @Test
        void withPageObjects02() {
            SearchComponent searchComponent = new SearchComponent(page);
            ProductList productList = new ProductList(page);
            ProductDetails productDetails = new ProductDetails(page);
            NavBar navBar = new NavBar(page);
            CheckoutCart checkoutCart = new CheckoutCart(page);

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

        class ProductDetails {
            private final Page page;

            public ProductDetails(Page page) {
                this.page = page;
            }

            public void increaseQuantity(int quantity) {
                for (int i = 1; i <= quantity; i++) {
                    page.getByTestId("increase-quantity").click();
                }
            }

            public void addToCart() {
                page.waitForResponse(
                        response -> response.url().contains("/carts") && response.request().method().equals("POST"),
                        () -> {
                            page.getByText("Add to cart").click();
                            page.getByRole(AriaRole.ALERT).click();
                        }
                );
            }
        }

        class SearchComponent {
            private final Page page;

            public SearchComponent(Page page) {
                this.page = page;
            }

            public void searchBy(String keyword) {
                page.waitForResponse("**/products/search?q=" + keyword, () -> {
                    page.getByPlaceholder("Search").fill(keyword);
                    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();
                });
            }
        }

        class ProductList {

            private final Page page;

            public ProductList(Page page) {
                this.page = page;
            }

            public List<String> getProductName() {
                return page.getByTestId("product-name").allInnerTexts();
            }

            public void viewProductDetails(String productName) {
                page.locator(".card").getByText(productName).click();
            }
        }

        class NavBar {
            private final Page page;

            public NavBar(Page page) {
                this.page = page;
            }

            public void openCart() {
                page.getByTestId("nav-cart").click();
            }
        }

        record CartLineItem(String title, int quantity, double price, double total) {
        }

        class CheckoutCart {
            private final Page page;

            public CheckoutCart(Page page) {
                this.page = page;
            }


            public List<CartLineItem> getLineItem() {
                page.locator("app-cart tbody tr").waitFor();
                return page.locator("app-cart tbody tr").all()
                        .stream()
                        .map(
                                row -> {
                                    String title = row.getByTestId("product-title").innerText();
                                    int quantity = Integer.parseInt(row.getByTestId("product-quantity").inputValue());
                                    double price = Double.parseDouble(price(row.getByTestId("product-price").innerText()));
                                    double linePrice = Double.parseDouble(price(row.getByTestId("line-price").innerText()));
                                    return new CartLineItem(title, quantity, price, linePrice);
                                }
                        ).toList();
            }

            private String price(String value) {
                return value.replace("$", "");
            }
        }
    }
}

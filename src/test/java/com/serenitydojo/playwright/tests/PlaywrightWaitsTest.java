package com.serenitydojo.playwright.tests;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.AriaRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

@UsePlaywright(HeadlessChromeOption.class)
public class PlaywrightWaitsTest {

    @BeforeEach
    void openHomePage(Page page) {
        page.navigate("https://practicesoftwaretesting.com");
    }

    @Nested
    class WaitingForState {

        @Test
        @DisplayName("Should show all products names")
        void shouldShowAllProductsNames(Page page) {
            Locator productName = page.getByTestId("product-name");
            List<String> productNameList = productName.allInnerTexts();
            Assertions.assertThat(productNameList).contains("Pliers", "Bolt Cutters", "Hammer");
        }

        @Test
        @DisplayName("Should show all products images")
        void shouldShowAllProductsImages(Page page) {
            List<String> productImageList = page.locator(".card-img-top")
                    .all().stream()
                    .map(img -> img.getAttribute("alt"))
                    .toList();
            Assertions.assertThat(productImageList).contains("Pliers", "Bolt Cutters", "Hammer");
        }
    }

    @Nested
    class AutomaticWaits {

        @Test
        @DisplayName("Should wait for filter checkbox options before clicking")
        void shouldWaitForTheFilterCheckboxes(Page page) {
            Locator hammerFilter = page.getByLabel(" Hammer ");
            Locator handSawFilter = page.getByLabel(" Hand Saw ");

            hammerFilter.click();
            handSawFilter.click();

            PlaywrightAssertions.assertThat(hammerFilter).isChecked();
            PlaywrightAssertions.assertThat(handSawFilter).isChecked();
        }

        @Test
        @DisplayName("Should filter products by category")
        void shouldFilterProductsByCategory(Page page) {
            Locator categoriesButton = page.getByRole(AriaRole.BUTTON).getByText(" Categories ");
            Locator otherToolsCategory = page.getByTestId("nav-other");
            Locator rentalsCategory = page.getByTestId("nav-rentals");

            categoriesButton.click();
            PlaywrightAssertions.assertThat(rentalsCategory).isVisible();
            otherToolsCategory.click();
//            page.waitForSelector(".card");
            page.waitForCondition(() -> page.title().contains("Other"));

            String pageTitle = page.title();
            List<String> productList = page.getByTestId("product-name").allInnerTexts();

            Assertions.assertThat(pageTitle).containsIgnoringCase("Other");
            Assertions.assertThat(productList).contains("Safety Goggles", "Ear Protection");
        }

    }

    @Nested
    class WaitingForElementsToAppearAndDisappear {

        @Test
        @DisplayName("It should display a toaster message when an item is added to the cart")
        void shouldDisplayToasterMessage(Page page) {
            page.getByText("Bolt Cutters").click();
            page.getByText("Add to cart").click();

            PlaywrightAssertions.assertThat(page.getByRole(AriaRole.ALERT)).isVisible();
            PlaywrightAssertions.assertThat(page.getByRole(AriaRole.ALERT)).hasText("Product added to shopping cart.");
            page.waitForCondition(() -> page.getByRole(AriaRole.ALERT).isHidden());
            page.waitForCondition(() -> page.getByTestId("cart-quantity").textContent().equals("1"));
        }
    }

    @Nested
    class WaitingForAPICalls {

        @Test
        @DisplayName("Sort by price in descending order")
        void sortByPriceDescending(Page page) {

            page.waitForResponse("**/products?page=0&sort**",
                    () ->
                    {
                        page.getByTestId("sort").selectOption("Price (High - Low)");
//                        page.getByTestId("product-price").first().waitFor();
                    });

            var productPrices = page.getByTestId("product-price")
                    .allInnerTexts()
                    .stream()
                    .map(WaitingForAPICalls::extractPrice)
                    .toList();
            System.out.println("Product prices: " + productPrices);
            Assertions.assertThat(productPrices)
                    .isNotEmpty()
                    .isSortedAccordingTo(Comparator.reverseOrder());
        }

        private static double extractPrice(String price) {
            return Double.parseDouble(price.replace("$",""));
        }
    }
}

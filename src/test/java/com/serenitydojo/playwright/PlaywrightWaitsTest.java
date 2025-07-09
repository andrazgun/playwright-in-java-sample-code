package com.serenitydojo.playwright;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

@UsePlaywright(HeadlessChromeOption.class)
public class PlaywrightWaitsTest {

    @DisplayName("Waits tests")
    @Nested
    class Waits {

        @BeforeEach
        void openHomePage(Page page) {
            page.navigate("https://practicesoftwaretesting.com");
//            page.waitForCondition(() -> page.getByTestId("product-name").count() > 0);
            page.waitForSelector("[data-test='product-name']");
        }

        @DisplayName("Should show all products names")
        @Test
        void shouldShowAllProductsNames(Page page) {
            Locator productName = page.getByTestId("product-name");
            List<String> productNameList = productName.allInnerTexts();
            Assertions.assertThat(productNameList).contains("Pliers","Bolt Cutters","Hammer");
        }

        @DisplayName("Should show all products images")
        @Test
        void shouldShowAllProductsImages(Page page) {
            List<String> productImageList = page.locator(".card-img-top")
                    .all().stream()
                    .map(img -> img.getAttribute("alt"))
                    .toList();
            Assertions.assertThat(productImageList).contains("Pliers","Bolt Cutters","Hammer");
        }
    }
}

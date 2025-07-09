package com.serenitydojo.playwright;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.LoadState;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.serenitydojo.playwright.PlaywrightFormTest.WhenInteractingWithTextFields.loadFormData;

@UsePlaywright(HeadlessChromeOption.class)
public class PlaywrightAssertionTest {

    @DisplayName("Interacting with text fields")
    @Nested
    class WhenInteractingWithTextFields {

        @BeforeEach
        void openContactPage(Page page) {
            page.navigate("https://practicesoftwaretesting.com/contact");
        }

        @DisplayName("Checking the value of a field")
        @Test
        void fieldValues(Page page) {
            String testDataFile = "src/test/resources/testData.txt";
            Map<String, String> formData = loadFormData(testDataFile);

            String firstName = formData.get("firstName");

            Locator firstNameField = page.getByTestId("first-name");

            firstNameField.fill(firstName);
            PlaywrightAssertions.assertThat(firstNameField).hasValue(firstName);
            PlaywrightAssertions.assertThat(firstNameField).not().isDisabled();
            PlaywrightAssertions.assertThat(firstNameField).isVisible();
            PlaywrightAssertions.assertThat(firstNameField).isEditable();
        }

        @DisplayName("Making assertions about values")
        @Nested
        class MakingAssertionsAboutDataValues {

            @BeforeEach
            void openPage(Page page) {
                page.navigate("https://practicesoftwaretesting.com");
                page.waitForCondition(() -> page.getByTestId("product-name").count() > 0);
            }

            @Test
            void allProductPricesShouldBeCorrectValues(Page page) {
                Locator productPrice = page.getByTestId("product-price");

                List<Double> prices = productPrice.allInnerTexts()
                        .stream()
                        .map(price -> Double.parseDouble(price.replace("$","")))
                        .toList();

                Assertions.assertThat(prices)
                        .isNotEmpty()
                        .allMatch(price -> price > 0)
                        .doesNotContain(0.0)
                        .allMatch(price -> price < 1000)
                        .allSatisfy(price ->
                                Assertions.assertThat(price)
                                        .isGreaterThan(0.0)
                                        .isLessThan(1000.0));
            }

            @Test
            void shouldSortFromAtoZ(Page page) {
                Locator productSort = page.getByTestId("sort");
                Locator productName = page.getByTestId("product-name");

                productSort.selectOption("Name (A - Z)");
                page.waitForLoadState(LoadState.NETWORKIDLE);

                List<String> productNames = productName.allTextContents();
                Assertions.assertThat(productNames).isSortedAccordingTo(Comparator.naturalOrder());
                Assertions.assertThat(productNames).isSortedAccordingTo(String.CASE_INSENSITIVE_ORDER);
            }

            @Test
            void shouldSortFromZtoA(Page page) {
                Locator productSort = page.getByTestId("sort");
                Locator productName = page.getByTestId("product-name");

                productSort.selectOption("Name (Z - A)");
                page.waitForLoadState(LoadState.NETWORKIDLE);

                List<String> productNames = productName.allTextContents();
                Assertions.assertThat(productNames).isSortedAccordingTo(Comparator.reverseOrder());
            }
        }
    }
}

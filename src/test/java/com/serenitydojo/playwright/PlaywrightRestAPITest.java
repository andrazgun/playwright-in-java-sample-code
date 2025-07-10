package com.serenitydojo.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.junit.UsePlaywright;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@UsePlaywright(HeadlessChromeOption.class)
public class PlaywrightRestAPITest {

    @BeforeEach
    void openHomePage(Page page) {
        page.navigate("https://practicesoftwaretesting.com");
//        page.getByPlaceholder("Search").waitFor();
    }

    @DisplayName("Playwright allows us to mock out API responses")
    @Nested
    class MockingAPIResponses {

        @Test
        @DisplayName("When a search returns a single product")
        void whenASingleItemIsFound(Page page) {

            page.route("**/products/search?q=hammer**", route -> route.fulfill(
                    new Route.FulfillOptions()
                            .setBody(MockSearchResponse.RESPONSE_WITH_A_SINGLE_ENTRY)
                            .setStatus(200)
            ));

            page.getByPlaceholder("Search").fill("hammer");
            page.getByPlaceholder("Search").press("Enter");
            PlaywrightAssertions.assertThat(page.getByTestId("product-name")).hasCount(1);
            PlaywrightAssertions.assertThat(page.getByTestId("product-name")).hasText("Super Hammer");
        }

        @Test
        @DisplayName("When a search returns no products")
        void whenNoItemsAreFound(Page page) {

            page.route("**/products/search?q=hammer**", route -> route.fulfill(
                    new Route.FulfillOptions()
                            .setBody(MockSearchResponse.RESPONSE_WITH_NO_ENTRIES)
                            .setStatus(200)
            ));

            page.getByPlaceholder("Search").fill("hammer");
            page.getByPlaceholder("Search").press("Enter");

            PlaywrightAssertions.assertThat(page.getByTestId("product-name")).hasCount(0);
            PlaywrightAssertions.assertThat(page.getByTestId("search_completed"))
                    .hasText("There are no products found.");
        }
    }
}

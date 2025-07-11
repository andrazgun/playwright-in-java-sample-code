package com.serenitydojo.playwright;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;


public class PlaywrightRestAPITest {

    protected static Playwright playwright;
    protected static Browser browser;
    protected static BrowserContext browserContext;

    Page page;

    @BeforeAll
    static void setUpBrowser() {
        playwright = Playwright.create();
        playwright.selectors().setTestIdAttribute("data-test");
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(true)
                        .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu"))
        );
    }

    @BeforeEach
    void setUp() {
        browserContext = browser.newContext();
        page = browserContext.newPage();

        page.navigate("https://practicesoftwaretesting.com");
        page.getByPlaceholder("Search").waitFor();

    }

    @AfterEach
    void closeContext() {
        browserContext.close();
    }

    @AfterAll
    static void tearDown() {
        browser.close();
        playwright.close();
    }

    @DisplayName("Playwright allows us to mock out API responses")
    @Nested
    class MockingAPIResponses {

        @Test
        @DisplayName("When a search returns a single product")
        void whenASingleItemIsFound() {

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
        void whenNoItemsAreFound() {

            page.route("**/products/search?q=hammer**", route -> route.fulfill(
                    new Route.FulfillOptions()
                            .setBody(MockSearchResponse.RESPONSE_WITH_NO_ENTRIES)
                            .setStatus(200)
            ));
            var searchBox = page.getByPlaceholder("Search");
            searchBox.fill("hammer");
            searchBox.press("Enter");

            PlaywrightAssertions.assertThat(page.getByTestId("product-name")).hasCount(0);
            PlaywrightAssertions.assertThat(page.getByTestId("search_completed"))
                    .hasText("There are no products found.");
        }
    }

    @DisplayName("Playwright allows us to make API calls")
    @Nested
    class MakingAPICalls {

        record Product(String name, Double price) {
        }

        private static APIRequestContext requestContext;

        @BeforeAll
        public static void setupRequestContext() {
            requestContext = playwright.request().newContext(
                    new APIRequest.NewContextOptions()
                            .setBaseURL("https://api.practicesoftwaretesting.com")
                            .setExtraHTTPHeaders(new HashMap<>() {{
                                put("Accept", "application/json");
                            }})
            );
        }

        @DisplayName("Check presence of known products")
        @ParameterizedTest(name = "Checking product {0}")
        @MethodSource("products")
        void checkKnownProduct(Product product) {
            page.fill("[placeholder='Search']", product.name);
//            page.getByPlaceholder("Search").fill(product.name);
            page.click("button:has-text('Search')");

            // Check that the product appears with the correct name and price

            Locator productCard = page.locator(".card")
                    .filter(
                            new Locator.FilterOptions()
                                    .setHasText(product.name)
                                    .setHasText(Double.toString(product.price))
                    );
            PlaywrightAssertions.assertThat(productCard).isVisible();
        }

        static Stream<Product> products() {
            APIResponse response = requestContext.get("/products?page=2");
            Assertions.assertThat(response.status()).isEqualTo(200);

            JsonObject jsonObject = new Gson().fromJson(response.text(), JsonObject.class);
            JsonArray data = jsonObject.getAsJsonArray("data");

            return data.asList().stream()
                    .map(jsonElement -> {
                        JsonObject productJson = jsonElement.getAsJsonObject();
                        return new Product(
                                productJson.get("name").getAsString(),
                                productJson.get("price").getAsDouble()
                        );
                    });
        }
    }
}

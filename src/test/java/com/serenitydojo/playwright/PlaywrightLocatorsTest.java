package com.serenitydojo.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Execution(ExecutionMode.SAME_THREAD)
public class PlaywrightLocatorsTest {

    protected static Playwright playwright;
    protected static Browser browser;
    protected static BrowserContext browserContext;

    Page page;

    @BeforeAll
    static void setUpBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
                        .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu"))
        );
    }

    @BeforeEach
    void setUp() {
        browserContext = browser.newContext();
        page = browserContext.newPage();
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

    @DisplayName("Locating elements using CSS")
    @Nested
    class LocatingElementsUsingCSS {

        @BeforeEach
        void openContactPage() {
            page.navigate("https://practicesoftwaretesting.com/contact");
        }

        @DisplayName("By id")
        @Test
        void locateTheFirstNameFieldByID() {
            page.locator("#first_name").fill("Pal");
            PlaywrightAssertions.assertThat(page.locator("#first_name")).hasValue("Pal");
        }

        @DisplayName("By CSS class")
        @Test
        void locateTheSendButtonByCssClass() {
            page.locator("#first_name").fill("Pal");
            page.locator(".btnSubmit").click();
            List<String> alertMessages = page.locator(".alert").allTextContents();
            assertThat(alertMessages).as("Alert messages should be displayed, but there are none")
                    .isNotEmpty();
        }

        @DisplayName("By attribute")
        @Test
        void locateTheSendButtonByAttribute() {
            String text = "Smith";
            page.locator("[placeholder='Your last name *']").fill(text);
            PlaywrightAssertions.assertThat(page.locator("#last_name")).hasValue("Smith");
        }
    }

    @DisplayName("Locating elements by text using CSS")
    @Nested
    class LocatingElementsByTextUsingCSS {

        @BeforeEach
        void openContactPage() {
            page.navigate("https://practicesoftwaretesting.com/contact");
        }

        // :has-text matches any element containing specified text somewhere inside.
        @DisplayName("Using :has-text")
        @Test
        void locateTheSendButtonByText() {
            // TODO: Make it so
        }

        // :text matches the smallest element containing specified text.
        @DisplayName("Using :text")
        @Test
        void locateAProductItemByText() {
            // TODO: Make it so
        }

        // Exact matches
        @DisplayName("Using :text-is")
        @Test
        void locateAProductItemByTextIs() {
            // TODO: Make it so
        }

        // matching with regular expressions
        @DisplayName("Using :text-matches")
        @Test
        void locateAProductItemByTextMatches() {
            // TODO: Make it so
        }
    }

    @DisplayName("Locating visible elements")
    @Nested
    class LocatingVisibleElements {
        @BeforeEach
        void openContactPage() {
            openPage();
        }

        @DisplayName("Finding visible and invisible elements")
        @Test
        void locateVisibleAndInvisibleItems() {
            // TODO: Make it so
        }

        @DisplayName("Finding only visible elements")
        @Test
        void locateVisibleItems() {
            // TODO: Make it so
        }
    }

    @DisplayName("Locating elements by role")
    @Nested
    class LocatingElementsByRole {

        @DisplayName("Using the BUTTON role")
        @Test
        void byButton() {
            // TODO: Make it so
        }

        @DisplayName("Using the HEADING role")
        @Test
        void byHeaderRole() {
            // TODO: Make it so
        }

        @DisplayName("Using the HEADING role and level")
        @Test
        void byHeaderRoleLevel() {
            // TODO: Make it so
        }

        @DisplayName("Identifying checkboxes")
        @Test
        void byCheckboxes() {
            // TODO: Make it so
        }
    }

    @DisplayName("Locating elements by placeholders and labels")
    @Nested
    class LocatingElementsByPlaceholdersAndLabels {

        @DisplayName("Using a label")
        @Test
        void byLabel() {
            // TODO: Make it so
        }

        @DisplayName("Using a placeholder text")
        @Test
        void byPlaceholder() {
            // TODO: Make it so
        }
    }

    @DisplayName("Locating elements by text")
    @Nested
    class LocatingElementsByText {

        @BeforeEach
        void openTheCatalogPage() {
            openPage();
        }

        @DisplayName("Locating an element by text contents")
        @Test
        void byText() {
            page.getByText("Bolt Cutter").click();

            PlaywrightAssertions.assertThat(page.getByText("MightyCraft Hardware")).isVisible();
        }

        @DisplayName("Using alt text")
        @Test
        void byAltText() {
            page.getByAltText("Combination Pliers").click();

            PlaywrightAssertions.assertThat(page.getByText("ForgeFlex Tools")).isVisible();

        }

        @DisplayName("Using title")
        @Test
        void byTitle() {
            page.getByAltText("Combination Pliers").click();
            page.getByTitle("Practice Software Testing - Toolshop").click();
        }
    }

    @DisplayName("Locating elements by test Id")
    @Nested
    class LocatingElementsByTestID {

        @BeforeAll
        static void setTestId() {
            playwright.selectors().setTestIdAttribute("data-test");
        }

        @DisplayName("Using a custom data-test field")
        @Test
        void byTestId() {
            // TODO: Make it so
        }
    }

    @DisplayName("Nested locators")
    @Nested
    class NestedLocators {

        @BeforeAll
        static void setTestId() {
            playwright.selectors().setTestIdAttribute("data-test");
        }

        @DisplayName("Using roles")
        @Test
        void locatingAMenuItemUsingRoles() {
            // TODO: Make it so
        }

        @DisplayName("Using roles with other strategies")
        @Test
        void locatingAMenuItemUsingRolesAndOtherStrategies() {
            // TODO: Make it so
        }

        @DisplayName("filtering locators by text")
        @Test
        void filteringMenuItems() {
            // TODO: Make it so
        }

        @DisplayName("filtering locators by locator")
        @Test
        void filteringMenuItemsByLocator() {
            // TODO: Make it so
        }
    }

    private void openPage() {
        page.navigate("https://practicesoftwaretesting.com");
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }
}

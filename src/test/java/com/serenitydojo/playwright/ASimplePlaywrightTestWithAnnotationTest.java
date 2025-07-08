package com.serenitydojo.playwright;

import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.OptionsFactory;
import com.microsoft.playwright.junit.UsePlaywright;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@UsePlaywright(ASimplePlaywrightTestWithAnnotationTest.MyOptions.class)
public class ASimplePlaywrightTestWithAnnotationTest {

    public static class MyOptions implements OptionsFactory {
        @Override
        public Options getOptions() {
            return new Options()
                    .setHeadless(false)
                    .setLaunchOptions(
                            new BrowserType.LaunchOptions()
                                    .setArgs(Arrays.asList("--no-sandbox","--disable-gpu"))
                    );
        }
    }

    @Test
    void shouldShowThePageTitle(Page page) {
        page.navigate("https://www.practicesoftwaretesting.com/");
        String title = page.title();
        assertThat(title)
                .withFailMessage("Page title is incorrect")
                .containsIgnoringCase("Practice Software Testing");
        assertThat(title).as("Page title is %s ", title)
                .contains("Practice Software Testing");
    }

    @Test
    void shouldSearchByKeyword(Page page) {
        page.navigate("https://www.practicesoftwaretesting.com/");
        page.locator("[placeholder=Search]").fill("Pliers");
        page.locator("button:has-text('Search')").click();
        int matchingSearchResults = page.locator(".card").count();
        assertThat(matchingSearchResults)
                .as("Result count should be greater than %d, but it wasn't.", 0)
                .isGreaterThan(0);
    }
}

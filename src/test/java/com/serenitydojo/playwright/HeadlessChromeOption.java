package com.serenitydojo.playwright;

import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.OptionsFactory;

import java.util.Arrays;

public class HeadlessChromeOption implements OptionsFactory {
    @Override
    public Options getOptions() {
        return new Options()
                .setLaunchOptions(
                        new BrowserType.LaunchOptions()
                                .setArgs(Arrays.asList("--no-sandbox","--disable-gpu"))
                ).setHeadless(false)
                .setTestIdAttribute("data-test")
                ;
    }
}

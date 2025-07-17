package com.serenitydojo.playwright.toolshop.catalog.pageobjects;

import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

public class NavBar {

    private final Page page;

    public NavBar(Page page) {
        this.page = page;
    }

    @Step
    public void openCart() {
        page.getByTestId("nav-cart").click();
    }

    @Step
    public void openHomepage() {
        page.navigate("https://practicesoftwaretesting.com");

//        page.getByTestId("nav-home").click();
    }
}

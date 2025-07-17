package com.serenitydojo.playwright.toolshop.catalog.pageobjects;

import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

import java.util.List;

public class ProductList {

    private final Page page;

    public ProductList(Page page) {
        this.page = page;
    }

    @Step
    public List<String> getProductNameList() {
        return page.getByTestId("product-name").allInnerTexts();
    }

    @Step
    public void viewProductDetails(String productName) {
        page.locator(".card").getByText(productName).click();
    }

    @Step
    public String getSearchCompletedMessage() {
        return page.getByTestId("search_completed").textContent();
    }
}

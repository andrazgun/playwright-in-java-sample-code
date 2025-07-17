package com.serenitydojo.playwright.toolshop.cucumber.stepdef;

import com.serenitydojo.playwright.toolshop.catalog.pageobjects.NavBar;
import com.serenitydojo.playwright.toolshop.catalog.pageobjects.ProductList;
import com.serenitydojo.playwright.toolshop.catalog.pageobjects.SearchComponent;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;

import static com.serenitydojo.playwright.toolshop.cucumber.stepdef.PlaywrightCucumberFixtures.getPage;

public class ProductCatalogStepDef {

    NavBar navBar;
    SearchComponent searchComponent;
    ProductList productList;

    @Before
    public void setupPageObjects() {
        navBar = new NavBar(getPage());
        searchComponent = new SearchComponent(getPage());
        productList = new ProductList(getPage());
    }

    @Given("Sally is on the home page")
    public void sally_is_on_the_home_page() {
        navBar.openHomepage();
    }
    @When("she searches for {string}")
    public void she_searches_for(String keyword) {
        searchComponent.searchBy(keyword);
    }
    @Then("the {string} product should be displayed")
    public void the_product_should_be_displayed(String productName) {
        var matchingProductList = productList.getProductNameList();
        Assertions.assertThat(matchingProductList).contains(productName);
    }
}

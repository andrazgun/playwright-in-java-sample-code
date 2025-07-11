package com.serenitydojo.playwright.toolshop.login;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.RequestOptions;
import com.serenitydojo.playwright.toolshop.domain.User;

public class UserAPIClient {

    private final Page page;
    private static final String BASE_URL = "https://api.practicesoftwaretesting.com";
    private static final String REGISTER_ENDPOINT = "/users/register";
    private static final String CONTENT_TYPE_JSON = "application/json";

    public UserAPIClient(Page page) {
        this.page = page;
    }

    public void registerUser(User user) {

        var response = page.request().post(
                BASE_URL+REGISTER_ENDPOINT,
                RequestOptions.create()
                        .setHeader("Content-Type", CONTENT_TYPE_JSON)
                        .setHeader("Accept", CONTENT_TYPE_JSON)
                        .setData(user));
        if (response.status() != 201) {
            throw new IllegalArgumentException("Could not create user: " + response.text());
        }
    }
}

package com.serenitydojo.playwright;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.RequestOptions;
import com.serenitydojo.playwright.toolshop.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@UsePlaywright
public class RegisterUserApiTest {

    private APIRequestContext request;
    private Gson gson = new Gson();

    private static final String BASE_URL = "https://api.practicesoftwaretesting.com";
    private static final String REGISTER_ENDPOINT = "/users/register";
    private static final String CONTENT_TYPE_JSON = "application/json";

    @BeforeEach
    void setup(Playwright playwright) {
        request = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL(BASE_URL)
        );
    }

    @AfterEach
    void tearDown() {
        if (request != null) {
            request.dispose();
        }
    }

    @Test
    void shouldRegisterUser() {
        User requestUser = User.randomUser();

        var response = request.post(REGISTER_ENDPOINT,
                RequestOptions.create()
                        .setHeader("Content-Type", CONTENT_TYPE_JSON)
                        .setData(requestUser)
        );

        String responseBody = response.text();
        User responseUser = gson.fromJson(responseBody, User.class);
        JsonObject responseObject = gson.fromJson(responseBody, JsonObject.class);

        assertSoftly(softly -> {
            softly.assertThat(response.status())
                    .as("Should return 201 status code")
                    .isEqualTo(201);
            softly.assertThat(responseUser)
                    .as("Created user should match the specified user without the password")
                    .isEqualTo(requestUser.withPassword(null));
            softly.assertThat(responseUser.address().city())
                    .as("Created user city %s should match the specified user city %s",
                            requestUser.address().city(), responseUser.address().city())
                    .isEqualTo(requestUser.address().city());
            softly.assertThat(responseObject.get("id").getAsString())
                    .as("Registered user should have an id: %s", responseObject.get("id").getAsString())
                    .isNotEmpty();
            softly.assertThat(responseObject.has("password"))
                    .as("No password should be returned")
                    .isFalse();
            softly.assertThat(response.headers().get("content-type")).contains(CONTENT_TYPE_JSON);
        });
    }

    @Test
    void emailIsRequired() {
        User requestUser = User.randomUserWithNoEmail();

        var response = request.post(REGISTER_ENDPOINT,
                RequestOptions.create()
                        .setHeader("Content-Type", CONTENT_TYPE_JSON)
                        .setData(requestUser)
        );

        String responseBody = response.text();
        JsonObject responseObject = gson.fromJson(responseBody, JsonObject.class);

        assertSoftly(softly -> {
            softly.assertThat(response.status())
                    .as("Should return 422 status code")
                    .isEqualTo(422);

            softly.assertThat(responseObject.has("email")).isTrue();

            String emailErrorMessage = responseObject.get("email").getAsString();
            softly.assertThat(emailErrorMessage)
                    .as("Error message is incorrect")
                    .isEqualTo("The email field is required.");
        });
    }
}
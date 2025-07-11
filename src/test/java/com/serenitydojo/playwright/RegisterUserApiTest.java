package com.serenitydojo.playwright;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.RequestOptions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@UsePlaywright
public class RegisterUserApiTest {

    private APIRequestContext request;

    @BeforeEach
    void setup(Playwright playwright) {
        request = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL("https://api.practicesoftwaretesting.com")
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

        var response = request.post("/users/register",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(requestUser)
        );

        String responseBody = response.text();
        Gson gson = new Gson();
        User responseUser = gson.fromJson(responseBody, User.class);

        JsonObject responseObject = gson.fromJson(responseBody, JsonObject.class);

        SoftAssertions.assertSoftly(softly -> {
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
            softly.assertThat(response.headers().get("content-type")).contains("application/json");

        });



        /*{
    "address": {
        "city": "Davidmouth",
        "country": "Vanuatu",
        "postal_code": "36852",
        "state": "Florida",
        "street": "18074 Weimann Valleys"
    },
    "created_at": "2025-07-11 10:58:50",
    "dob": "1976-06-04",
    "email": "leslie.larkin@hotmail.com",
    "first_name": "Sean",
    "id": "01jzwj6m8ehh2w8z8jcxf923vw",
    "last_name": "Lueilwitz",
    "phone": "031 0636"
}*/

    }
}

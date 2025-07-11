package com.serenitydojo.playwright;

import net.datafaker.Faker;

import java.util.Locale;

public record Address(String street,
                      String city,
                      String state,
                      String country,
                      String postal_code) {

    public static Address randomAddress() {
        Faker fake = new Faker(new Locale("ro"));
        return new Address(
                fake.address().streetAddress(),
                fake.address().city(),
                fake.address().state(),
                fake.address().country(),
                fake.address().postcode()
        );
    }
}

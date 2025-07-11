package com.serenitydojo.playwright.toolshop.domain;

/*
{
    "address": {
        "city": "City",
        "country": "Country",
        "postal_code": "1234AA",
        "state": "State",
        "street": "Street 1"
    },
    "dob": "1970-01-01",
    "email": "john@doe.example",
    "first_name": "John",
    "last_name": "Doe",
    "password": "SuperSecure@123",
    "phone": "0987654321"
}
        */

import net.datafaker.Faker;
import net.datafaker.providers.base.Text;

import java.util.Locale;

import static com.serenitydojo.playwright.toolshop.domain.Address.randomAddress;
import static net.datafaker.providers.base.Text.*;

public record User(String first_name,
                   String last_name,
                   Address address,
                   String phone,
                   String dob,
                   String password,
                   String email) {

    public static User randomUser() {
        Faker fake = new Faker(new Locale("ro"));
        return new User(
                fake.name().firstName(),
                fake.name().lastName(),
                randomAddress(),
                fake.phoneNumber().phoneNumber(),
                fake.timeAndDate().birthday(18, 50, "yyyy-MM-dd"),
                fake.text().text(Text.TextSymbolsBuilder.builder()
                        .len(8)
                        .with(EN_UPPERCASE, 1)
                        .with(EN_LOWERCASE,2)
                        .with(DIGITS, 1)
                        .with(DEFAULT_SPECIAL, 2)
                        .build()),
                fake.internet().emailAddress()
        );
    }

    public Object withPassword(String password) {
        return new User(first_name,
                last_name,
                address,
                phone,
                dob,
                password,
                email
        );
    }

    public static User randomUserWithNoEmail() {
        Faker fake = new Faker(new Locale("ro"));
        return new User(
                fake.name().firstName(),
                fake.name().lastName(),
                randomAddress(),
                fake.phoneNumber().phoneNumber(),
                fake.timeAndDate().birthday(18, 50, "yyyy-MM-dd"),
                fake.text().text(Text.TextSymbolsBuilder.builder()
                        .len(8)
                        .with(EN_UPPERCASE, 1)
                        .with(EN_LOWERCASE,2)
                        .with(DIGITS, 1)
                        .with(DEFAULT_SPECIAL, 2)
                        .build()),
                null
        );
    }

    public User randomUserWithSpecificPassword(String password) {
        return new User(this.first_name,
                this.last_name,
                this.address,
                this.phone,
                this.dob,
                password,
                this.email
        );
    }
}

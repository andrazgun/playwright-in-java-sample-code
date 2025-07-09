package com.serenitydojo.playwright;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.AriaRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@UsePlaywright(HeadlessChromeOption.class)
public class PlaywrightFormTest {

    @DisplayName("Interacting with text fields")
    @Nested
    class WhenInteractingWithTextFields {

        @BeforeEach
        void openContactPage(Page page) {
            page.navigate("https://practicesoftwaretesting.com/contact");
        }

        @DisplayName("Complete the form")
        @Test
        void completeForm(Page page) {
            String testDataFile = "src/test/resources/testData.txt";
            String attachmentFile = "src/test/resources/fileForAttachment.txt";
            Map<String, String> formData = loadFormData(testDataFile);
            String firstName = formData.get("firstName");
            String lastName = formData.get("lastName");
            String email = formData.get("email");
            String message = formData.get("message");
            String subject = "payments";

            Locator firstNameField = page.getByTestId("first-name");
            Locator lastNameField = page.getByTestId("last-name");
            Locator emailField = page.getByTestId("email");
            Locator messageField = page.getByTestId("message");
            Locator subjectOption = page.getByTestId("subject");
            Locator uploadAttachment = page.getByTestId("attachment");
            Locator submitBtn = page.getByTestId("contact-submit");
            Locator alert = page.getByRole(AriaRole.ALERT);

            firstNameField.fill(firstName);
            lastNameField.fill(lastName);
            emailField.fill(email);
            messageField.fill(message);
            subjectOption.selectOption(subject);
            uploadAttachment.setInputFiles(Paths.get(attachmentFile));

            PlaywrightAssertions.assertThat(firstNameField).hasValue(firstName);
            Assertions.assertThat(firstNameField.inputValue())
                    .as("Incorrect text.")
                    .isEqualToIgnoringCase(firstName);
            PlaywrightAssertions.assertThat(lastNameField).hasValue(lastName);
            PlaywrightAssertions.assertThat(emailField).hasValue(email);
            PlaywrightAssertions.assertThat(messageField).hasValue(message);
            PlaywrightAssertions.assertThat(subjectOption).hasValue(subject);

            String inputValue = uploadAttachment.inputValue();
            Assertions.assertThat(inputValue).endsWith("fileForAttachment.txt");

            submitBtn.click();
            Assertions.assertThat(alert.textContent().trim())
                    .as("Incorrect text.")
                    .isEqualToIgnoringCase("Thanks for your message! We will contact you shortly.");
            PlaywrightAssertions.assertThat(alert).hasText("Thanks for your message! We will contact you shortly.");
            PlaywrightAssertions.assertThat(alert).isVisible();
        }

        @DisplayName("Mandatory fields")
        @Test
        void mandatoryFields(Page page) {
            String testDataFile = "src/test/resources/testData.txt";
            Map<String, String> formData = loadFormData(testDataFile);

            List<String> expectedMessages = List.of(
                    formData.get("firstNameErrorMsg"),
                    formData.get("lastNameErrorMsg"),
                    formData.get("emailErrorMsg"),
                    formData.get("subjectErrorMsg"),
                    formData.get("messageErrorMsg")
            );

            Locator submitBtn = page.getByTestId("contact-submit");
            Locator alert = page.getByRole(AriaRole.ALERT);

            submitBtn.click();
            List<String> actualMessages = alert.all().stream()
                    .map(locator -> locator.textContent().trim())
                    .toList();

            assertAlertMessages(actualMessages, expectedMessages);
        }

        @DisplayName("Mandatory field error message")
        @ParameterizedTest
        @ValueSource(strings = {"First name","Last name","Email","Message"})
        void mandatoryFields(String fieldName, Page page) {

            Locator submitBtn = page.getByTestId("contact-submit");
            Locator alert = page.getByRole(AriaRole.ALERT);

            submitBtn.click();

            List<String> actualMessages = alert.all().stream()
                    .map(locator -> locator.textContent().trim())
                    .toList();

            Assertions.assertThat(actualMessages.stream()
                    .anyMatch(expectedMessage -> expectedMessage.equalsIgnoreCase(fieldName + " is required")))
                    .as("Incorrect error message")
                    .isTrue();
        }

        @DisplayName("Input fields")
        @Test
        void fieldsValues(Page page) {
            var firstNameField = page.getByLabel("First name");
            firstNameField.fill("Ioana");
            Locator firstNameFieldLocator = page.getByTestId("first-name");
            String text = firstNameFieldLocator.inputValue();
            assertThat(firstNameField).hasValue(text);
        }

        protected static Map<String, String> loadFormData(String filePath) {
            Map<String, String> data = new HashMap<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("=")) {
                        String[] parts = line.split("=", 2);
                        data.put(parts[0].trim(), parts[1].trim());
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to read form data from file: " + filePath, e);
            }
            return data;
        }

        private void assertAlertMessages(List<String> actualMessages, List<String> expectedMessages) {
            Assertions.assertThat(actualMessages)
                    .as("Number of alert messages does not match expected")
                    .hasSameSizeAs(expectedMessages);

            IntStream.range(0, actualMessages.size()).forEach(i -> {
                String actual = actualMessages.get(i).trim();
                String expected = expectedMessages.get(i).trim();

                Assertions.assertThat(actual)
                        .as("Verifying alert message at index " + i)
                        .isEqualToIgnoringCase(expected);
            });
        }
    }
}

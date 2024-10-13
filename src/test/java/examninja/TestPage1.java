package examninja;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class TestPage1 {

    private WireMockServer wireMockServer;
    private CloseableHttpClient httpClient;

    @BeforeClass
    public void setupWireMock() {
        // Start the WireMock server on port 8080
        wireMockServer = new WireMockServer(8082);
        wireMockServer.start();

        // Initialize the HttpClient
        httpClient = HttpClients.createDefault();

        // Test Case 1: Start test and display the first question
        wireMockServer.stubFor(get(urlEqualTo("/api/startTest"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"testName\": \"Java Certification Exam\", \"startDate\": \"Oct 10, 2024\", \"time\": \"9:00 AM\", \"firstQuestion\":" +
                                " \"Which of the following are valid Java identifiers?\", \"options\": [\"_myVar\", \"123abc\", \"$value\", \"void\"] }")));

        // Test Case 2: Deactivate Previous button on first screen
        wireMockServer.stubFor(get(urlEqualTo("/api/checkPreviousButton"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"status\": \"disabled\" }")));

        // Test Case 3: Display test details
        wireMockServer.stubFor(get(urlEqualTo("/api/testDetails"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"testName\": \"Java Certification Exam\", \"startDate\": \"Oct 10, 2024\", \"time\": \"9:00 AM\" }")));

        // Test Case 4: Display question with 4 options and radio buttons
        wireMockServer.stubFor(get(urlEqualTo("/api/questionWithOptions"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"question\": \"What is 2 + 2?\", \"options\": [\"1\", \"2\", \"3\", \"4\"] }")));

        // Test Case 5: Scroll for long questions
        wireMockServer.stubFor(get(urlEqualTo("/api/longQuestion"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"question\": \"A very long question that requires scrolling to view the full content...\"," +
                                " \"options\": [\"Option1\", \"Option2\", \"Option3\", \"Option4\"] }")));

        // Test Case 6: Deactivate NEXT button on last screen
        wireMockServer.stubFor(get(urlEqualTo("/api/checkNextButton"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"status\": \"disabled\" }")));

        // Test Case 7: NEXT and PREVIOUS button navigation
        wireMockServer.stubFor(get(urlEqualTo("/api/navigateQuestions"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"status\": \"navigated\" }")));

        // Test Case 8: Backend integration for navigation
        wireMockServer.stubFor(get(urlEqualTo("/api/backendNavigation"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"status\": \"backend integrated\" }")));

        // Test Case 9: Verify navigation to correct test page (Test URL)
        wireMockServer.stubFor(get(urlEqualTo("/api/testURL"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"url\": \"http://localhost:8082/testPage\", \"status\": \"correct\" }")));

        // Test Case 10: Verify visibility and readability of content
        wireMockServer.stubFor(get(urlEqualTo("/api/checkVisibility"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"question\": \"What is the capital of France?\", \"options\": [\"Berlin\", \"Madrid\", \"Paris\", \"Rome\"], \"visible\": true }")));

        // Test Case 11: Verify back navigation with data retention
        wireMockServer.stubFor(get(urlEqualTo("/api/backNavigation"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"answer1\": \"Paris\", \"answer2\": \"4\", \"status\": \"answers retained\" }")));

        // Test Case 12: Verify user session timeout
        wireMockServer.stubFor(get(urlEqualTo("/api/sessionTimeout"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"status\": \"session timed out\" }")));
    }

    @AfterClass
    public void tearDownWireMock() throws IOException {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }

        // Close the HttpClient
        if (httpClient != null) {
            httpClient.close();
        }
    }


    private String sendGetRequest(String endpoint) throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8082" + endpoint);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            return EntityUtils.toString(response.getEntity());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    // Test Case 1
    @Test
    public void testStartTestAndDisplayFirstQuestion() throws IOException {
        String response = sendGetRequest("/api/startTest");
        Assert.assertTrue(response.contains("Java Certification Exam"), "Test name should be 'Java Certification Exam'");
        Assert.assertTrue(response.contains("Which of the following are valid Java identifiers?"), "First question should be displayed");
    }

    // Test Case 2
    @Test
    public void testDeactivatePreviousButtonOnFirstScreen() throws IOException {
        String response = sendGetRequest("/api/checkPreviousButton");
        Assert.assertTrue(response.contains("disabled"), "Previous button should be disabled on the first screen");
    }

    // Test Case 3
    @Test
    public void testDisplayTestDetails() throws IOException {
        String response = sendGetRequest("/api/testDetails");
        Assert.assertTrue(response.contains("Java Certification Exam"), "Test name should be displayed");
        Assert.assertTrue(response.contains("Oct 10, 2024"), "Test date should be displayed");
    }

    // Test Case 4
    @Test
    public void testDisplayQuestionWithOptions() throws IOException {
        String response = sendGetRequest("/api/questionWithOptions");
        Assert.assertTrue(response.contains("What is 2 + 2?"), "Question should be displayed");
        Assert.assertTrue(response.contains("4"), "Options should be displayed correctly");
    }

    // Test Case 5
    @Test
    public void testScrollForLongQuestions() throws IOException {
        String response = sendGetRequest("/api/longQuestion");
        Assert.assertTrue(response.contains("A very long question"), "Long question should be displayed");
    }

    // Test Case 6
    @Test
    public void testDeactivateNextButtonOnLastScreen() throws IOException {
        String response = sendGetRequest("/api/checkNextButton");
        Assert.assertTrue(response.contains("disabled"), "Next button should be disabled on the last screen");
    }

    // Test Case 7
    @Test
    public void testNextAndPreviousButtonNavigation() throws IOException {
        String response = sendGetRequest("/api/navigateQuestions");
        Assert.assertTrue(response.contains("navigated"), "Buttons should navigate between questions");
    }

    // Test Case 8
    @Test
    public void testBackendIntegrationForNavigation() throws IOException {
        String response = sendGetRequest("/api/backendNavigation");
        Assert.assertTrue(response.contains("backend integrated"), "Backend should be integrated");
    }

    // Test Case 9
    @Test
    public void testVerifyCorrectTestPage() throws IOException {
        String response = sendGetRequest("/api/testURL");
        Assert.assertTrue(response.contains("http://localhost:8082/testPage"), "Correct test page URL should be displayed");
    }

    // Test Case 10
    @Test
    public void testVerifyVisibilityAndReadabilityOfContent() throws IOException {
        String response = sendGetRequest("/api/checkVisibility");
        Assert.assertTrue(response.contains("What is the capital of France?"), "Question should be visible and readable");
    }

    // Test Case 11
    @Test
    public void testVerifyBackNavigationWithDataRetention() throws IOException {
        String response = sendGetRequest("/api/backNavigation");
        Assert.assertTrue(response.contains("answers retained"), "Back navigation should retain answers");
    }
}

    // Test Case 12


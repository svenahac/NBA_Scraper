package com.example.nba_scraper;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

public class MainTest {
    private WebDriver driver;

    private static final String url = "https://api.sportsdata.io/v3/nba/scores/json/Players/DAL?key=58957574730c4ee1b809da2f53525997";

    @BeforeEach
    public void setUp() {

        // Initialize the webdriver
        System.setProperty("webdriver.gecko.driver", "geckodriver");
        driver = new FirefoxDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.get("https://www.nba.com/players");

    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void ThreePointAverage() throws IOException, InterruptedException {

        // Clears the cookies pop-up
        WebElement cookies = driver.findElement(By.cssSelector(".banner-close-button"));
        cookies.click();


        String[] player_links = generateLinks();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        // Flag that will let us know if a player doesn't meet the required average
        int flag = 0;

        // Iterating over the player links and opening them then calculating 3 point average
        for (int i = 0; i < player_links.length; i++) {

            // Navigating to player page
            driver.get(player_links[i]);

            // Checking if the last 5 matches are in the second panel on the website
            Boolean isPresent = driver.findElements(By.cssSelector("#__next > div.Layout_base__6IeUC.Layout_justNav__2H4H0 > div.Layout_mainContent__jXliI > section > div.MaxWidthContainer_mwc__ID5AG.PlayerView_pvSection__whddS > section:nth-child(2) > div > div > div > table > tbody > tr:nth-child(1) > td:nth-child(9)")).size() > 0;

            float points = 0f;
            // Loop over last 5 games that calculates the combined 3PM
            for (int j = 1; j < 6; j++) {

                WebElement pointsPerGame;

                if (isPresent) {
                    // Getting the matches from the 2nd panel
                    pointsPerGame = driver.findElement(By.cssSelector("#__next > div.Layout_base__6IeUC.Layout_justNav__2H4H0 > div.Layout_mainContent__jXliI > section > div.MaxWidthContainer_mwc__ID5AG.PlayerView_pvSection__whddS > section:nth-child(2) > div > div > div > table > tbody > tr:nth-child(" + j + ") > td:nth-child(9)"));

                } else {
                    // Else, get them from the 1st panel
                    pointsPerGame = driver.findElement(By.cssSelector("#__next > div.Layout_base__6IeUC.Layout_justNav__2H4H0 > div.Layout_mainContent__jXliI > section > div.MaxWidthContainer_mwc__ID5AG.PlayerView_pvSection__whddS > section:nth-child(1) > div > div > div > table > tbody > tr:nth-child(" + j + ") > td:nth-child(9)"));
                }


                points += Integer.parseInt(pointsPerGame.getText());
            }
            float average = points / 5 ;
            if (average <= 1) {
                flag = 1;
            }
        }

        // Test succeeds if average of 3PM is over 1
        assertTrue(flag == 0);


    }

    public String[] generateLinks() throws IOException, InterruptedException {
        // Call the api to request data about active Dallas Mavericks players
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Put the response into and array of Player objects
        Gson gson = new Gson();
        Player[] players = gson.fromJson(response.body(), Player[].class);

        ArrayList<String> playerLinks = new ArrayList<>();

        // For each player create a link that lead to their nba webpage
        for (Player player : players){
            String link;
            String firstName = player.getFirstName().toLowerCase();
            String lastName = player.getLastName().toLowerCase();
            String nbaDotComPlayerID = player.getNbaDotComPlayerID();
            String format = firstName + "-" + lastName;
            format = format.replaceAll("\\s+", "-");
            format = format.replaceAll("\\.", "");
            link = "https://www.nba.com/player/" + nbaDotComPlayerID + "/" + format;
            playerLinks.add(link);
        }

        // Transform ArrayList to a normal java
        String[] links = playerLinks.toArray(new String[playerLinks.size()]);
        return links;
    }

}

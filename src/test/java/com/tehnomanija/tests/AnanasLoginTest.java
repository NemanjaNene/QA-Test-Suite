package com.tehnomanija.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class AnanasLoginTest {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeMethod
    public void setUp() {

        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get("https://ananas.rs/");

        JavascriptExecutor js = (JavascriptExecutor) driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Zatvaranje kolačića putem JavaScript-a
        try {
            WebElement cookiesButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Slažem se']")));
            js.executeScript("arguments[0].click();", cookiesButton);
            System.out.println("Kolačići su zatvoreni.");
        } catch (Exception e) {
            System.out.println("Nije prikazana opcija za zatvaranje kolačića.");
        }

        // Zatvaranje reklame putem JavaScript-a
        try {
            WebElement adCloseButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("svg[aria-label='close']")));
            js.executeScript("arguments[0].click();", adCloseButton);
            System.out.println("Reklama je zatvorena.");
        } catch (Exception e) {
            System.out.println("Nema reklame za zatvaranje.");
        }
    }

    @Test
    public void testLoginInvalidEmail() {
        try {
            // Pokušaj zatvaranja reklame ako je vidljiva pre nego što nastavimo sa testom
            closeAdIfPresent();

            // Čekaj dok se ikonica za prijavu ne pojavi i postane klikabilna
            WebElement loginIcon = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[@alt='Sign-up icon']")));
            loginIcon.click();
            System.out.println("Kliknuto na ikonu za prijavu.");

            // Čekanje dok se stranica za prijavu ne učita
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));

            // Unos nevalidnog emaila
            WebElement emailInput = driver.findElement(By.id("username"));
            WebElement passwordInput = driver.findElement(By.id("password"));
            WebElement loginButton = driver.findElement(By.xpath("//button[contains(text(), 'Prijavi se')]"));

            // Prva kombinacija
            emailInput.sendKeys("NemanjaTesting");
            passwordInput.sendKeys("Password123");
            loginButton.click();

            // Provera greške za nevalidan email
            WebElement errorMessage = driver.findElement(By.xpath("//p[contains(text(), 'Email adresa nije ispravna.')]"));
            Assert.assertTrue(errorMessage.isDisplayed());

            // Resetovanje polja za unos
            emailInput.clear();
            passwordInput.clear();

            // Druga kombinacija
            emailInput.sendKeys("Nemanja@testing.com");
            passwordInput.sendKeys("wrongpassword");
            loginButton.click();

            // Provera greške za pogrešan unos lozinke
            WebElement errorPassword = driver.findElement(By.xpath("//p[contains(text(), 'Neispravan pokušaj prijave.')]"));
            Assert.assertTrue(errorPassword.isDisplayed());

        } catch (Exception e) {
            System.out.println("Problem sa testiranjem logovanja: " + e.getMessage());
        }
    }

    // Ova metoda pokušava da zatvori reklamu ako je prisutna
    public void closeAdIfPresent() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement closeAdButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("svg[aria-label='close']")));
            closeAdButton.click();
            System.out.println("Reklama je zatvorena.");
        } catch (Exception e) {
            System.out.println("Nema reklame za zatvaranje.");
        }
    }



    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

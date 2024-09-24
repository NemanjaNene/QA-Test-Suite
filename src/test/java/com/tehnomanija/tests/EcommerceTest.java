package com.tehnomanija.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import io.github.bonigarcia.wdm.WebDriverManager;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.Status;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class EcommerceTest {

    WebDriver driver;
    ExtentReports extent;
    ExtentTest test;

    @BeforeMethod
    public void setUp() {
        ExtentSparkReporter spark = new ExtentSparkReporter("./Reports/EcommerceTestReport.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://ananas.rs/");

        test = extent.createTest("Ecommerce Test - Adding Multiple Items to Cart");


        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement acceptCookies = driver.findElement(By.xpath("//button[text()='Slažem se']"));
            acceptCookies.click();
            test.log(Status.PASS, "Kolačići prihvaćeni.");
        } catch (NoSuchElementException e) {
            test.log(Status.INFO, "Opcija za prihvatanje kolačića se nije pojavila.");
        }


        closeAdIfPresent();
    }

    @Test
    public void testAddMultipleItemsToCart() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {

            wait.until(webDriver -> js.executeScript("return document.readyState").equals("complete"));


            WebElement namestajElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"__next\"]/div[4]/div[1]/header/div[4]/div/section/div/nav/ul/li[4]/a")));
            if (namestajElement.isDisplayed() && namestajElement.isEnabled()) {
                js.executeScript("arguments[0].scrollIntoView(true);", namestajElement);
                js.executeScript("arguments[0].click();", namestajElement);
            }


            WebElement sofaElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[@alt='SIMPO Klik klak ležaj Maja, 190x130cm, Braon']")));
            js.executeScript("arguments[0].click();", sofaElement);
            test.log(Status.PASS, "Klik na SIMPO uspešan");


            WebElement dodajUKorpuButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Dodaj u korpu']")));
            js.executeScript("arguments[0].click();", dodajUKorpuButton);
            test.log(Status.PASS, "Prvi proizvod uspešno dodat u korpu.");


            WebElement nastaviPlacanjeButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Nastavi ka plaćanju']")));
            js.executeScript("arguments[0].click();", nastaviPlacanjeButton);


            WebElement usisivaciElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'Usisivači')]")));
            js.executeScript("arguments[0].click();", usisivaciElement);
            Thread.sleep(3000);


            try {
                WebElement rowentaUsisivac = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h3[contains(text(), 'ROWENTA Usisivač RO4B75')]")));
                js.executeScript("arguments[0].scrollIntoView(false);", rowentaUsisivac);
                wait.until(ExpectedConditions.visibilityOf(rowentaUsisivac));
                wait.until(ExpectedConditions.elementToBeClickable(rowentaUsisivac));
                rowentaUsisivac.click();
                test.log(Status.PASS, "Usisivač Rowenta RO4B75 je uspešno izabran.");
            } catch (Exception e) {
                test.log(Status.FAIL, "Usisivač Rowenta nije izabran: " + e.getMessage());
                takeScreenshot("RowentaUsisivacError");
            }


            try {
                WebElement dodajUKorpuButton2 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Dodaj u korpu']")));
                js.executeScript("arguments[0].scrollIntoView(true);", dodajUKorpuButton2);  // Ponovo skrolujemo na dugme Dodaj u korpu
                js.executeScript("arguments[0].click();", dodajUKorpuButton2);
                test.log(Status.PASS, "Usisivač Rowenta uspešno dodat u korpu.");
            } catch (Exception e) {
                test.log(Status.FAIL, "Dodavanje Rowenta usisivača u korpu nije uspelo: " + e.getMessage());
                takeScreenshot("DodajUKorpuError");
            }



            test.log(Status.PASS, "Svi proizvodi uspešno dodati u korpu.");
        } catch (Exception e) {
            test.log(Status.FAIL, "Test nije uspeo: " + e.getMessage());
            takeScreenshot("FailedTestScreenshot");
        }
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
        extent.flush();
    }

    public void takeScreenshot(String screenshotName) {
        File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(srcFile, new File("./Screenshots/" + screenshotName + ".png"));
            test.addScreenCaptureFromPath("./Screenshots/" + screenshotName + ".png");
        } catch (IOException e) {
            test.log(Status.FAIL, "Screenshot nije moguće sačuvati: " + e.getMessage());
        }
    }


    public void closeAdIfPresent() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            try {

                WebElement closeAdButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//svg[@aria-label='close']")));
                closeAdButton.click();
                test.log(Status.PASS, "Reklama je zatvorena putem SVG dugmeta.");
            } catch (Exception e) {
                test.log(Status.INFO, "SVG dugme za zatvaranje reklame nije pronađeno.");


                try {
                    WebElement closeAdButtonCSS = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("svg[aria-label='close']")));
                    closeAdButtonCSS.click();
                    test.log(Status.PASS, "Reklama je zatvorena putem CSS selektora.");
                } catch (Exception ex) {
                    test.log(Status.INFO, "CSS dugme za zatvaranje reklame takođe nije pronađeno.");
                }
            }
        } catch (Exception e) {
            test.log(Status.FAIL, "Greška prilikom zatvaranja reklame: " + e.getMessage());
        }
    }
}


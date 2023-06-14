package test;

import core.SnapshotExtension;
import core.SnapshotTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

@ExtendWith(SnapshotExtension.class)
public class GoogleTest {

    private static WebDriver driver;

    @BeforeAll
    public static void setup() {
        FirefoxOptions options = new FirefoxOptions();
        options.setBinary("E:\\Programme\\Firefox\\firefox.exe");
        driver = new FirefoxDriver(options);
    }

    @SnapshotTest
    public void search() {
        driver.get("https://jmaen.github.io/");
    }

    @AfterAll
    public static void teardown() {
        driver.quit();
    }

}

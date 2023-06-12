package test;

import core.SnapshotTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class GoogleTest extends SnapshotTest {

    @BeforeAll
    public static void setup() {
        FirefoxOptions options = new FirefoxOptions();
        options.setBinary("E:\\Programme\\Firefox\\firefox.exe");
        driver = new FirefoxDriver(options);
    }

    @Test
    public void search() {
        driver.get("https://jmaen.github.io/");
    }

}

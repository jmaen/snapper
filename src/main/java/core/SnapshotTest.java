package core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

@ExtendWith(SnapshotExtension.class)
public abstract class SnapshotTest {

    protected static WebDriver driver;

    private Snapshot snapshot;

    @BeforeAll
    public static void setup() {
        driver = new FirefoxDriver();
    }

    @AfterAll
    public static void teardown() {
        driver.quit();
    }

}

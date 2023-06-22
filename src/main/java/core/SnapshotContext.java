package core;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.HasFullPageScreenshot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SnapshotContext {

    private TakesScreenshot screenshotTarget;
    private SearchContext searchTarget;

    private boolean isFullPage;
    private List<WebElement> ignoredElements = new ArrayList<>();
    private Color ignoredColor = Color.MAGENTA;

    public boolean hasTarget() {
        return screenshotTarget != null && searchTarget != null;
    }

    public void setTarget(WebDriver target) {
        screenshotTarget = (TakesScreenshot) target;
        searchTarget = target;
    }

    public void setTarget(WebElement target) {
        screenshotTarget = target;
        searchTarget = target;
    }

    public void setFullPage(boolean isFullPage) {
        this.isFullPage = isFullPage;
    }

    public void addIgnoredElement(WebElement element) {
        ignoredElements.add(element);
    }

    public void addIgnoredElements(WebElement... elements) {
        ignoredElements.addAll(Arrays.stream(elements).toList());
    }

    public void setIgnoredColor(Color color) {
        ignoredColor = color;
    }

    public BufferedImage takeScreenshot() throws IOException {
        File file;
        if(isFullPage) {
            if(screenshotTarget instanceof HasFullPageScreenshot) {
                file = ((HasFullPageScreenshot) screenshotTarget).getFullPageScreenshotAs(OutputType.FILE);
            } else {
                System.err.println("Screenshot target does not support full page screenshots, "
                        + "fell back to default screenshots");
                file = screenshotTarget.getScreenshotAs(OutputType.FILE);
            }
        } else {
           file = screenshotTarget.getScreenshotAs(OutputType.FILE);
        }
        BufferedImage screenshot = ImageIO.read(file);

        if(!ignoredElements.isEmpty()) {
            // TODO "black out" ignored elements
        }

        return screenshot;
    }

}

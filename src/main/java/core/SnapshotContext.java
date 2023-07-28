package core;

import org.openqa.selenium.*;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
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

    private TakesScreenshot target;
    private Point targetLocation = new Point(0, 0);

    private boolean isFullPage;
    private List<WebElement> ignoredElements = new ArrayList<>();
    private Color ignoredColor = Color.MAGENTA;

    public boolean hasTarget() {
        return target != null;
    }

    public void setTarget(WebDriver target) {
        if(!(target instanceof TakesScreenshot)) {
            throw new SnapshotException("WebDriver does not support screenshots");
        }
        this.target = (TakesScreenshot) target;
    }

    public void setTarget(WebElement target) {
        this.target = target;
        targetLocation = target.getLocation();
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
        File file = null;
        if(isFullPage) {
            if(target instanceof HasFullPageScreenshot) {
                file = ((HasFullPageScreenshot) target).getFullPageScreenshotAs(OutputType.FILE);
            } else {
                System.err.println(
                        "Screenshot target does not support full page screenshots, fell back to default screenshots"
                );
            }
        }
        if(file == null) {
           file = target.getScreenshotAs(OutputType.FILE);
        }
        BufferedImage screenshot = ImageIO.read(file);

        if(!ignoredElements.isEmpty()) {
            Graphics2D graphics = screenshot.createGraphics();
            graphics.setColor(ignoredColor);
            for(WebElement element : ignoredElements) {
                Rectangle rect = element.getRect();
                // TODO add padding to prevent overflowing
                graphics.fillRect(rect.x - targetLocation.x, rect.y - targetLocation.y, rect.width, rect.height);
            }
            graphics.dispose();
        }

        return screenshot;
    }

}

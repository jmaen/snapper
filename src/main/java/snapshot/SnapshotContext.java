package snapshot;

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
    private int ignoredPaddingX = 0;
    private int ignoredPaddingY = 0;

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

    public void setIgnoredPadding(int padding) {
        ignoredPaddingX = padding;
        ignoredPaddingY = padding;
    }

    public void setIgnoredPadding(int paddingX, int paddingY) {
        ignoredPaddingX = paddingX;
        ignoredPaddingY = paddingY;
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
                graphics.fillRect(
                        rect.x - targetLocation.x - ignoredPaddingX,
                        rect.y - targetLocation.y - ignoredPaddingY,
                        rect.width + 2*ignoredPaddingX,
                        rect.height + 2*ignoredPaddingY
                );
            }
            graphics.dispose();
        }

        return screenshot;
    }

}

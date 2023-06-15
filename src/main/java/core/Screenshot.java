package core;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.firefox.HasFullPageScreenshot;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Screenshot {

    private TakesScreenshot target;
    private boolean isFullPage;

    public boolean hasTarget() {
        return target != null;
    }

    public void setTarget(TakesScreenshot target) {
        this.target = target;
    }

    public void setFullPage(boolean isFullPage) {
        this.isFullPage = isFullPage;
    }

    public BufferedImage take() throws IOException {
        if(isFullPage) {
            try {
                File file = ((HasFullPageScreenshot) target).getFullPageScreenshotAs(OutputType.FILE);
                return ImageIO.read(file);
            } catch(ClassCastException e) {
                throw new SnapshotException("Screenshot target does not support full page screenshots", e);
            }
        } else {
            File file = target.getScreenshotAs(OutputType.FILE);
            return ImageIO.read(file);
        }
    }

}

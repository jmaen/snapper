package core;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Screenshot {

    private TakesScreenshot target;

    public boolean hasTarget() {
        return target != null;
    }

    public void setTarget(TakesScreenshot target) {
        this.target = target;
    }

    public BufferedImage take() throws IOException {
        File file = target.getScreenshotAs(OutputType.FILE);
        return ImageIO.read(file);
    }

}

package image;

import org.apache.commons.lang3.NotImplementedException;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageComparison {

    private final BufferedImage expected;
    private final BufferedImage actual;

    // comparison options, e.g. tolerance (max color delta)

    private DiffMode diffMode = DiffMode.PIXEL;
    private Color diffColor = Color.RED;

    // box diff options, e.g. regarding box size / distance

    private int differenceCount = 0;

    public ImageComparison(BufferedImage expected, BufferedImage actual) {
        this.expected = expected;
        this.actual = actual;
    }

    public ImageComparison withDiffMode(DiffMode diffMode) {
        this.diffMode = diffMode;
        return this;
    }

    public ImageComparison withDiffColor(Color diffColor) {
        this.diffColor = diffColor;
        return this;
    }

    public ImageComparisonResult compare() {
        boolean[][] differences = findDifferences();

        if(differences == null) {
            return new ImageComparisonResult(ImageComparisonResult.State.SIZE_MISMATCH, actual);
        } else if(differenceCount == 0) {
            return new ImageComparisonResult(ImageComparisonResult.State.MATCH, actual);
        }

        BufferedImage diff = switch(diffMode) {
            case PIXEL -> createPixelDiff(actual, differences);
            case BOX -> createBoxDiff(actual, differences);
        };

        return new ImageComparisonResult(ImageComparisonResult.State.MISMATCH, diff);
    }

    private boolean[][] findDifferences() {
        if(expected.getWidth() != actual.getWidth() || expected.getHeight() != actual.getHeight()) {
            return null;
        }

        boolean[][] differences = new boolean[expected.getWidth()][expected.getHeight()];
        for(int x = 0; x < expected.getWidth(); x++) {
            for(int y = 0; y < expected.getHeight(); y++) {
                // TODO calculate color delta
                if(expected.getRGB(x, y) != actual.getRGB(x, y)) {
                    differences[x][y] = true;
                    differenceCount++;
                }
            }
        }

        return differences;
    }

    private BufferedImage createPixelDiff(BufferedImage image, boolean[][] differences) {
        BufferedImage diff = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D graphics = diff.createGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();

        for(int x = 0; x < image.getWidth(); x++) {
            for(int y = 0; y < image.getHeight(); y++) {
                if(differences[x][y]) {
                    diff.setRGB(x, y, diffColor.getRGB());
                }
            }
        }

        return diff;
    }

    private BufferedImage createBoxDiff(BufferedImage image, boolean[][] differences) {
        // TODO box diff
        throw new NotImplementedException("Currently only pixel diffs are supported");
    }

    public enum DiffMode {
        PIXEL,
        BOX
    }

}

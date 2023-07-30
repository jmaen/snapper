package snapshot;

import image.ImageComparison;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SnapshotConfig {

    private static SnapshotConfig config;

    private Properties properties;

    private String snapshotDir;
    private String outputDir;
    private Snapshot.UpdateMode updateMode;
    private double tolerance;
    private ImageComparison.DiffMode diffMode;
    private Color diffColor;
    private int boxThreshold;
    private int boxStrokeWidth;

    private SnapshotConfig() {
        Properties defaults = new Properties();
        InputStream in = SnapshotConfig.class.getResourceAsStream("/defaults.properties");
        try {
            defaults.load(in);
        } catch (IOException e) {
            throw new SnapshotException("Failed to load default properties", e);
        }
        properties = new Properties(defaults);

        // TODO load snapper.properties

        snapshotDir = getValue("snapshot-dir");
        outputDir = getValue("output-dir");
        updateMode = Snapshot.UpdateMode.valueOf(getValue("update-mode").toUpperCase());
        tolerance = Double.parseDouble(getValue("tolerance"));
        diffMode = ImageComparison.DiffMode.valueOf(getValue("diff-mode").toUpperCase());
        diffColor = Color.decode(getValue("diff-color"));
        boxThreshold = Integer.parseInt(getValue("box-threshold"));
        boxStrokeWidth = Integer.parseInt(getValue("box-stroke-width"));
    }

    public static SnapshotConfig get() {
        if(config == null) {
            config = new SnapshotConfig();
        }
        return config;
    }

    public String getSnapshotDir() {
        return snapshotDir;
    }

    public void setSnapshotDir(String snapshotDir) {
        this.snapshotDir = snapshotDir;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public Snapshot.UpdateMode getUpdateMode() {
        return updateMode;
    }

    public void setUpdateMode(Snapshot.UpdateMode updateMode) {
        this.updateMode = updateMode;
    }

    public double getTolerance() {
        return tolerance;
    }

    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }

    public ImageComparison.DiffMode getDiffMode() {
        return diffMode;
    }

    public void setDiffMode(ImageComparison.DiffMode diffMode) {
        this.diffMode = diffMode;
    }

    public Color getDiffColor() {
        return diffColor;
    }

    public void setDiffColor(Color diffColor) {
        this.diffColor = diffColor;
    }

    public int getBoxThreshold() {
        return boxThreshold;
    }

    public void setBoxThreshold(int boxThreshold) {
        this.boxThreshold = boxThreshold;
    }

    public int getBoxStrokeWidth() {
        return boxStrokeWidth;
    }

    public void setBoxStrokeWidth(int boxStrokeWidth) {
        this.boxStrokeWidth = boxStrokeWidth;
    }

    private String getValue(String key) {
        String value = properties.getProperty(key);
        if(value == null) {
            throw new SnapshotException("Missing snapshot property for " + key);
        }
        return value;
    }

}

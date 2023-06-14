package core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SnapshotConfig {

    private static SnapshotConfig config;

    private Properties properties;

    private String snapshotDir;
    private String outputDir;
    private Snapshot.UpdateMode updateMode;

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
        updateMode = Snapshot.UpdateMode.valueOf(getValue("update-snapshots").toUpperCase());
    }

    private String getValue(String key) {
        String value = properties.getProperty(key);
        if(value == null) {
            throw new SnapshotException("Missing snapshot property for " + key);
        }
        return value;
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

}

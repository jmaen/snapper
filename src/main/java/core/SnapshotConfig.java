package core;

public class SnapshotConfig {

    private static SnapshotConfig config;

    private String snapshotDir = "src/test/java/__snapshots__";

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

}

package core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Snapshot {

    private static final SnapshotConfig config = SnapshotConfig.get();

    private final String name;
    private final Path path;

    private Snapshot(String name, Path path) {
        this.name = name;
        this.path = path;
    }

    public static Snapshot of(Method testMethod) {
        Class<?> testClass = testMethod.getDeclaringClass();

        SnapshotName nameAnnotation = testMethod.getDeclaredAnnotation(SnapshotName.class);
        String name;
        if(nameAnnotation != null) {
           name = nameAnnotation.value();
        } else {
            name = testClass.getSimpleName() + "_" + testMethod.getName();
        }

        String subDir = testClass.getPackageName().replace(".", "/");

        Path path = Paths.get(config.getSnapshotDir(), subDir, name + ".png");
        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            throw new SnapshotException("Failed to create snapshot folder structure", e);
        }

        return new Snapshot(name, path);
    }

    public void shouldMatch(BufferedImage screenshot) throws IOException {
        File file = path.toFile();
        switch(config.getUpdateMode()) {
            case ALL -> ImageIO.write(screenshot, "png", file);
            case MISSING -> {
                if(!file.exists()) {
                    ImageIO.write(screenshot, "png", file);
                }
            }
            case NONE -> {
                if(file.exists()) {
                    BufferedImage snapshot = ImageIO.read(file);

                    // TODO ImageComparison

                    throw new AssertionError("TODO");
                } else {
                    throw new SnapshotException("Snapshot not found for " + name);
                }
            }
        }
    }

    public enum UpdateMode {
        ALL,
        MISSING,
        NONE
    }

}
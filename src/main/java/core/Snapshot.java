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

        SnapshotTest annotation = testMethod.getDeclaredAnnotation(SnapshotTest.class);
        String name = annotation.name();
        if(name.isEmpty()) {
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
                if(file.exists()) {
                    compare(ImageIO.read(file), screenshot);
                } else {
                    ImageIO.write(screenshot, "png", file);
                }
            }
            case NONE -> {
                if(file.exists()) {
                    compare(ImageIO.read(file), screenshot);
                } else {
                    throw new SnapshotException("Snapshot not found for " + name);
                }
            }
        }
    }

    private void compare(BufferedImage snapshot, BufferedImage screenshot) {
        // TODO image comparison
        throw new AssertionError("TODO");
    }

    public enum UpdateMode {
        ALL,
        MISSING,
        NONE
    }

}
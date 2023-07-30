package snapshot;

import image.ImageComparisonResult;
import image.ImageComparison;

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
                    throw new SnapshotException("Snapshot not found for '" + name + "'");
                }
            }
        }
    }

    private void compare(BufferedImage snapshot, BufferedImage screenshot) throws IOException {
        ImageComparison comparison = new ImageComparison(snapshot, screenshot)
                .withTolerance(config.getTolerance())
                .withDiffMode(config.getDiffMode())
                .withDiffColor(config.getDiffColor())
                .withBoxThreshold(config.getBoxThreshold())
                .withBoxStrokeWidth(config.getBoxStrokeWidth());
        ImageComparisonResult result = comparison.compare();

        switch(result.getState()) {
            case SIZE_MISMATCH ->
                    throw new SnapshotException("Screenshot size does not match snapshot size for '" + name + "'");

            case MISMATCH -> {
                Path outputPath = Path.of(config.getOutputDir(), name + ".png");
                try {
                    Files.createDirectories(outputPath.getParent());
                } catch (IOException e) {
                    throw new SnapshotException("Failed to create output folder structure", e);
                }
                ImageIO.write(result.getDiff(), "png", outputPath.toFile());
                throw new AssertionError("Screenshot does not match snapshot '" + name + "'");
            }
        }
    }

    public enum UpdateMode {
        ALL,
        MISSING,
        NONE
    }

}
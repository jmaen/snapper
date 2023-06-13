package core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Snapshot {

    private final String name;
    private final Path path;

    private Snapshot(String folder, String name) {
        this.name = name;
        path = Paths.get(SnapshotConfig.get().getSnapshotDir(), folder, name + ".png");
        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            // TODO add own exception
            e.printStackTrace();
        }
    }

    public static Snapshot of(Method testMethod) {
        Class<?> testClass = testMethod.getDeclaringClass();
        String folder = testClass.getPackageName().replace(".", "/");
        SnapshotName nameAnnotation = testMethod.getDeclaredAnnotation(SnapshotName.class);
        if(nameAnnotation != null) {
            return new Snapshot(folder, nameAnnotation.value());
        } else {
            return new Snapshot(folder, testClass.getSimpleName() + "_" + testMethod.getName());
        }
    }

    public void shouldMatch(BufferedImage screenshot) throws IOException {
        ImageIO.write(screenshot, "png", path.toFile());
        throw new AssertionError();
    }

}
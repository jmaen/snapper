package image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ImageComparison {

    private final BufferedImage expected;
    private final BufferedImage actual;

    private double maxDelta;
    private DiffMode diffMode;
    private Color diffColor;
    private int boxThreshold;
    private int boxStrokeWidth;

    public ImageComparison(BufferedImage expected, BufferedImage actual) {
        this.expected = expected;
        this.actual = actual;
    }

    public ImageComparison withTolerance(double tolerance) {
        maxDelta = ColorDelta.MAX_DELTA * tolerance * tolerance;
        return this;
    }

    public ImageComparison withDiffMode(DiffMode diffMode) {
        this.diffMode = diffMode;
        return this;
    }

    public ImageComparison withDiffColor(Color diffColor) {
        this.diffColor = diffColor;
        return this;
    }

    public ImageComparison withBoxThreshold(int boxThreshold) {
        this.boxThreshold = boxThreshold;
        return this;
    }

    public ImageComparison withBoxStrokeWidth(int boxStrokeWidth) {
        this.boxStrokeWidth = boxStrokeWidth;
        return this;
    }

    public ImageComparisonResult compare() {
        List<Point> differences = findDifferences();

        if(differences == null) {
            return new ImageComparisonResult(ImageComparisonResult.State.SIZE_MISMATCH, actual);
        } else if(differences.size() == 0) {
            return new ImageComparisonResult(ImageComparisonResult.State.MATCH, actual);
        }

        BufferedImage diff = switch(diffMode) {
            case PIXEL -> createPixelDiff(actual, differences);
            case BOX -> createBoxDiff(actual, differences);
        };

        return new ImageComparisonResult(ImageComparisonResult.State.MISMATCH, diff);
    }

    private List<Point> findDifferences() {
        if(expected.getWidth() != actual.getWidth() || expected.getHeight() != actual.getHeight()) {
            return null;
        }

        List<Point> differences = new ArrayList<>();
        for(int x = 0; x < expected.getWidth(); x++) {
            for(int y = 0; y < expected.getHeight(); y++) {
                double delta = ColorDelta.delta(expected.getRGB(x, y), actual.getRGB(x, y));
                if(delta > maxDelta) {
                    differences.add(new Point(x, y));
                }
            }
        }

        return differences;
    }

    private BufferedImage createPixelDiff(BufferedImage image, List<Point> differences) {
        BufferedImage diff = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D graphics = diff.createGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();

        for(Point point : differences) {
            diff.setRGB(point.x, point.y, diffColor.getRGB());
        }

        return diff;
    }

    private BufferedImage createBoxDiff(BufferedImage image, List<Point> differences) {
        List<Rectangle> boxes = calculateBoxes(differences);

        BufferedImage diff = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D graphics = diff.createGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.setColor(diffColor);
        graphics.setStroke(new BasicStroke(boxStrokeWidth));
        for(Rectangle box : boxes) {
            graphics.draw(box);
        }
        graphics.dispose();

        return diff;
    }

    private List<Rectangle> calculateBoxes(List<Point> differences) {
        List<Rectangle> boxes = new ArrayList<>();

        while(differences.size() > 0) {
            // create new cluster
            List<Point> cluster = new ArrayList<>();
            cluster.add(differences.remove(0));

            while(true) {
                // calculate neighbourhood of cluster (points whose distance to the cluster is <= boxThreshold)
                Rectangle box = calculateBox(cluster);
                List<Point> neighbourhood = differences.stream()
                        .filter(p -> calculateDistance(p, box) <= boxThreshold)
                        .collect(Collectors.toList());

                if(neighbourhood.isEmpty()) {
                    // if there are no points left to add to the cluster, continue with the next one
                    boxes.add(box);
                    break;
                } else {
                    cluster.addAll(neighbourhood);
                    differences.removeAll(neighbourhood);
                }
            }
        }

        return boxes;
    }

    private Rectangle calculateBox(List<Point> cluster) {
        Point point = cluster.get(0);
        int minX = point.x;
        int maxX = point.x;
        int minY = point.y;
        int maxY = point.y;
        for(int i = 1; i < cluster.size(); i++) {
            point = cluster.get(i);
            if(point.x < minX) {
                minX = point.x;
            } else if(point.x > maxX) {
                maxX = point.x;
            }
            if(point.y < minY) {
                minY = point.y;
            } else if(point.y > maxY) {
                maxY = point.y;
            }
        }

        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    private double calculateDistance(Point point, Rectangle box) {
        int closestX = Math.min(Math.max(point.x, box.x), box.x + box.width);
        int closestY = Math.min(Math.max(point.y, box.y), box.y + box.height);

        return point.distance(closestX, closestY);
    }

    public enum DiffMode {
        PIXEL,
        BOX
    }

}

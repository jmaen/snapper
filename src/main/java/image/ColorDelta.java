package image;

public class ColorDelta {

    public static final int MAX_DELTA = 35215;

    public static double delta(int rgb1, int rgb2) {
        int r1 = (rgb1 >> 16) & 0xFF;
        int g1 = (rgb1 >> 8) & 0xFF;
        int b1 = rgb1 & 0xFF;
        int r2 = (rgb2 >> 16) & 0xFF;
        int g2 = (rgb2 >> 8) & 0xFF;
        int b2 = rgb2 & 0xFF;

        double deltaY = rgbToY(r1, g1, b1) - rgbToY(r2, g2, b2);
        double deltaI = rgbToI(r1, g1, b1) - rgbToI(r2, g2, b2);
        double deltaQ = rgbToQ(r1, g1, b1) - rgbToQ(r2, g2, b2);

        return 0.5053*deltaY*deltaY + 0.299*deltaI*deltaI + 0.1957*deltaQ*deltaQ;
    }

    private static double rgbToY(int r, int g, int b) {
        return r*0.29889531 + g*0.58662247 + b*0.11448223;
    }

    private static double rgbToI(int r, int g, int b) {
        return r*0.59597799 - g*0.27417610 - b*0.32180189;
    }

    private static double rgbToQ(int r, int g, int b) {
        return r*0.21147017 - g*0.52261711 + b*0.31114694;
    }

}

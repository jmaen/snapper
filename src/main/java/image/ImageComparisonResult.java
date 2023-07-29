package image;

import java.awt.image.BufferedImage;

public class ImageComparisonResult {

    private final State state;
    private final BufferedImage diff;

    public ImageComparisonResult(State state, BufferedImage diff) {
        this.state = state;
        this.diff = diff;
    }

    public State getState() {
        return state;
    }

    public BufferedImage getDiff() {
        return diff;
    }

    public enum State {
        MATCH,
        MISMATCH,
        SIZE_MISMATCH
    }

}

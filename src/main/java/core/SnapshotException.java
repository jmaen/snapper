package core;

public class SnapshotException extends RuntimeException {

    public SnapshotException() {
        super();
    }

    public SnapshotException(String message) {
        super(message);
    }

    public SnapshotException(Exception cause) {
        super(cause);
    }

    public SnapshotException(String message, Exception cause) {
        super(message, cause);
    }

}

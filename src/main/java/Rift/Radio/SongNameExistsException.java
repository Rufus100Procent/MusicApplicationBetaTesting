package Rift.Radio;

public class SongNameExistsException extends RuntimeException {
    public SongNameExistsException(String message) {
        super(message);
    }
}
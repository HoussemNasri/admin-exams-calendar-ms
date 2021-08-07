package fsb.aoc.adminexamscalendarms.exceptions;

public class FileStorageException extends RuntimeException {
  public FileStorageException() {
    super();
  }

  public FileStorageException(String message) {
    super(message);
  }

  public FileStorageException(String message, Throwable throwable) {
    super(message, throwable);
  }
}

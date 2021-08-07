package fsb.aoc.adminexamscalendarms.response;

public class ResponseMessage {
  private final String message;
  private final String error;

  public static ResponseMessage success(String message) {
    return new ResponseMessage(message, "");
  }

  public static ResponseMessage failed(Exception e) {
    return new ResponseMessage("", e.toString());
  }

  public static ResponseMessage failed(String errorMessage) {
    return new ResponseMessage("", errorMessage);
  }

  private ResponseMessage(String message, String error) {
    this.message = message;
    this.error = error;
  }

  public String getMessage() {
    return message;
  }

  public String getError() {
    return error;
  }
}

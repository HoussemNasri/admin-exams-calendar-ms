package fsb.aoc.adminexamscalendarms.web;

import java.util.HashMap;
import java.util.Map;

import fsb.aoc.adminexamscalendarms.entities.ExamCalendarInfo;
import fsb.aoc.adminexamscalendarms.model.ExamCalendarContext;
import fsb.aoc.adminexamscalendarms.response.ResponseMessage;
import fsb.aoc.adminexamscalendarms.services.ExamCalendarService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ExamCalendarController {
  private static final String PDF_MIME_TYPE = "application/pdf";
  private final ExamCalendarService examCalendarService;

  public ExamCalendarController(ExamCalendarService examCalendarService) {
    this.examCalendarService = examCalendarService;
  }

  @PostMapping("/examens")
  public ResponseEntity<Object> upload(
      @ModelAttribute UploadFileFormWrapper formWrapper) {
    try {
      examCalendarService.saveCalender(
          formWrapper.getFile(),
          new ExamCalendarContext(formWrapper.getSemester(), formWrapper.getSession()));
      return ResponseEntity.ok(
          createSuccessResponse(
              "File Uploaded Successfully: " + formWrapper.getFile().getOriginalFilename()));
    } catch (Exception e) {
      return ResponseEntity.internalServerError()
          .body(createErrorResponse("Upload Failed : " + e.getMessage()));
    }
  }

  @GetMapping("/examens/{id}")
  public ResponseEntity<Object> download(@PathVariable("id") Long calendarId) {
    try {
      Resource calendarFileResource = examCalendarService.loadCalenderFile(calendarId);
      return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType(PDF_MIME_TYPE))
          .header(
              HttpHeaders.CONTENT_DISPOSITION,
              "attachment; filename=\"" + calendarFileResource.getFilename() + "\"")
          .body(calendarFileResource);
    } catch (Exception e) {
      return ResponseEntity.internalServerError()
          .body(createErrorResponse("Download Failed: " + e.getMessage()));
    }
  }

  private Map<String, String> createErrorResponse(String errorMessage) {
    Map<String, String> result = new HashMap<>();
    result.put("error", errorMessage);
    return result;
  }

  private Map<String, String> createSuccessResponse(String message) {
    Map<String, String> result = new HashMap<>();
    result.put("message", message);
    return result;
  }

  @DeleteMapping("/examens/{id}")
  public ResponseEntity<ExamCalendarInfo> delete(@PathVariable("id") Long calendarId) {
    return null;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  private static class UploadFileFormWrapper {
    private MultipartFile file;
    private String semester;
    private String session;
  }
}

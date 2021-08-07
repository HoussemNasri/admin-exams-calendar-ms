package fsb.aoc.adminexamscalendarms.web;

import java.io.IOException;

import fsb.aoc.adminexamscalendarms.model.ExamCalendarContext;
import fsb.aoc.adminexamscalendarms.response.ResponseMessage;
import fsb.aoc.adminexamscalendarms.services.ExamCalendarService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
  @Autowired private ExamCalendarService examCalendarService;

  @PostMapping("/examens")
  public ResponseEntity<ResponseMessage> upload(@ModelAttribute UploadFileFormWrapper formWrapper) {
    try {
      examCalendarService.saveCalendarFile(
          formWrapper.getFile(),
          new ExamCalendarContext(formWrapper.getSemester(), formWrapper.getSession()));
      return ResponseEntity.ok(
          ResponseMessage.success(
              "File Uploaded Successfully: " + formWrapper.getFile().getOriginalFilename()));
    } catch (Exception e) {
      return ResponseEntity.internalServerError()
          .body(ResponseMessage.failed("Upload Failed : " + e.getMessage()));
    }
  }

  @GetMapping("/examens/{id}")
  public ResponseEntity<Resource> download(@PathVariable("id") Long calendarId) {
    Resource calendarFileResource = examCalendarService.loadCalendarFile(calendarId);
    if (calendarFileResource.exists()) {
      try {
        System.out.println("PATH: " + calendarFileResource.getFile().getAbsolutePath());
      } catch (IOException e) {
        e.printStackTrace();
      }
      String contentType = "application/pdf";

      return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType(contentType))
          .header(
              HttpHeaders.CONTENT_DISPOSITION,
              "attachment; filename=\"" + calendarFileResource.getFilename() + "\"")
          .body(calendarFileResource);
    } else {
      System.out.println("File do not exists");
    }
    return ResponseEntity.noContent().build();
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

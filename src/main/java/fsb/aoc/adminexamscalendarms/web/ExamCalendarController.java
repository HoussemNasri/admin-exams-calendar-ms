package fsb.aoc.adminexamscalendarms.web;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fsb.aoc.adminexamscalendarms.entities.ExamCalendarInfo;
import fsb.aoc.adminexamscalendarms.model.ExamCalendarContext;
import fsb.aoc.adminexamscalendarms.services.ExamCalendarService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
  public ResponseEntity<Object> upload(@ModelAttribute UploadFileFormWrapper formWrapper) {
    try {
      examCalendarService.saveCalender(
          formWrapper.getFile(),
          new ExamCalendarContext(formWrapper.getSemester(), formWrapper.getSession()));
      return ResponseEntity.ok(
          buildSuccessResponse(
              "File Uploaded Successfully: " + formWrapper.getFile().getOriginalFilename()));
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(buildErrorResponse("Upload Failed : " + e));
    }
  }

  @GetMapping("/examens/uploads/{id}")
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
          .body(buildErrorResponse("Download Failed: " + e.getMessage()));
    }
  }

  @GetMapping("/examens/{id}")
  public ResponseEntity<ExamCalendarInfo> getCalendarInfo(@PathVariable("id") Long calendarId) {
    try {
      ExamCalendarInfo calendarInfo = examCalendarService.getCalendarInfo(calendarId);
      System.out.println("Hello");
      if (calendarInfo != null) {
        System.out.println(calendarInfo.getFilePath());
        return ResponseEntity.ok(calendarInfo);
      } else {
        System.out.println("Not FUck");
        return ResponseEntity.notFound().build();
      }
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }

  @DeleteMapping("/examens/{id}")
  public ResponseEntity<Object> delete(@PathVariable("id") Long calendarId) {
    try {
      ExamCalendarInfo deletedCalendarInfo = examCalendarService.deleteCalender(calendarId);
      return ResponseEntity.ok(deletedCalendarInfo);
    } catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/examens")
  public ResponseEntity<Object> search(
      @RequestParam(required = false) String semester,
      @RequestParam(required = false) String session) {
    try {
      if (semester == null && session == null) {
        return ResponseEntity.ok(examCalendarService.getAllCalendarsInfo());
      }
      ExamCalendarInfo.Semester semesterEnum = parseSemester(semester);
      ExamCalendarInfo.ExamsSession sessionEnum = parseSession(session);
      List<ExamCalendarInfo> result;
      if (semesterEnum != null) {
        if (sessionEnum != null) {
          result = examCalendarService.findCalenderBy(semesterEnum, sessionEnum);
        } else {
          result = examCalendarService.findCalenderBy(semesterEnum);
        }
      } else {
        if (sessionEnum != null) {
          result = examCalendarService.findCalenderBy(sessionEnum);
        } else {
          result = Collections.emptyList();
        }
      }

      if (result == null || result.isEmpty()) {
        return ResponseEntity.noContent().build();
      }
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      return ResponseEntity.internalServerError()
          .body(buildErrorResponse("Fetching Calendars Information Failed : " + e));
    }
  }

  @PutMapping("/examens/{id}")
  public ResponseEntity<Object> rename(
      @PathVariable("id") Long toUpdateCalendarId, @RequestParam("name") String newName) {
    try {
      return ResponseEntity.ok(examCalendarService.renameCalender(toUpdateCalendarId, newName));
    } catch (Exception e) {
      return ResponseEntity.internalServerError()
          .body(
              buildErrorResponse(
                  "Cannot rename calendar of id: " + toUpdateCalendarId + " to name: " + newName));
    }
  }

  @PutMapping("/examens/uploads/{id}")
  public ResponseEntity<Object> updatedCalenderFile(
      @PathVariable("id") Long toUpdateCalendarId,
      @RequestParam("file") MultipartFile newCalendarFile) {
    try {
      System.out.println(newCalendarFile.getOriginalFilename());
      ExamCalendarInfo calendarInfo =
          examCalendarService.updateCalenderFile(toUpdateCalendarId, newCalendarFile);
      if (calendarInfo != null) {
        return ResponseEntity.ok(calendarInfo);
      } else {
        return ResponseEntity.notFound().build();
      }

    } catch (Exception e) {
      return ResponseEntity.internalServerError()
          .body(
              buildErrorResponse(
                  "Cannot update calendar document of id: "
                      + toUpdateCalendarId
                      + " because "
                      + e));
    }
  }

  private @Nullable ExamCalendarInfo.ExamsSession parseSession(String session) {
    try {
      return ExamCalendarInfo.ExamsSession.valueOf(session);
    } catch (IllegalArgumentException | NullPointerException e) {
      return null;
    }
  }

  private ExamCalendarInfo.Semester parseSemester(String semester) {
    try {
      return ExamCalendarInfo.Semester.valueOf(semester);
    } catch (IllegalArgumentException | NullPointerException e) {
      return null;
    }
  }

  private Map<String, String> buildErrorResponse(String errorMessage) {
    Map<String, String> result = new HashMap<>();
    result.put("error", errorMessage);
    return result;
  }

  private Map<String, String> buildSuccessResponse(String message) {
    Map<String, String> result = new HashMap<>();
    result.put("message", message);
    return result;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  private static class UploadFileFormWrapper {
    private MultipartFile file;
    private String semester;
    private String session;
  }

  private static class UpdateFileFormWrapper {
    private Long toUpdateCalendarId;
    private MultipartFile file;
  }
}

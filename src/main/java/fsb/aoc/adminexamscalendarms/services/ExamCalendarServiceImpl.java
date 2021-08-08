package fsb.aoc.adminexamscalendarms.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import fsb.aoc.adminexamscalendarms.entities.ExamCalendarInfo;
import fsb.aoc.adminexamscalendarms.exceptions.ExamCalendarException;
import fsb.aoc.adminexamscalendarms.model.ExamCalendarContext;
import fsb.aoc.adminexamscalendarms.repositories.ExamCalendarInfoRepository;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static fsb.aoc.adminexamscalendarms.services.FileStorageServiceImpl.CALENDAR_UPLOADS_FOLDER;

@Service
public class ExamCalendarServiceImpl implements ExamCalendarService {
  private final ExamCalendarInfoRepository calendarInfoRepository;

  private final FileStorageService fileStorageService;

  public ExamCalendarServiceImpl(
      ExamCalendarInfoRepository calendarInfoRepository, FileStorageService fileStorageService) {
    this.calendarInfoRepository = calendarInfoRepository;
    this.fileStorageService = fileStorageService;
  }

  @Override
  public void saveCalender(MultipartFile file, ExamCalendarContext calendarContext) {
    if (!isPdf(file)) {
      throw new ExamCalendarException("Exams calendar needs to be in PDF format");
    }
    try {
      if (isCalendarFileExists(file)) {
        // TODO('Fix it')
        throw new ExamCalendarException("File already exists");
      }
      fileStorageService.saveFile(file);
    } catch (IOException e) {
      throw new ExamCalendarException("Calendar couldn't be copied to the upload folder, " + e);
    }

    ExamCalendarInfo calendarInfo = new ExamCalendarInfo();
    String originalFilename = file.getOriginalFilename();
    String pureFileName =
        originalFilename.endsWith(".pdf") ? originalFilename.substring(0, originalFilename.length() - 4) : originalFilename;
    calendarInfo.setFileName(pureFileName);
    calendarInfo.setFileSize(file.getSize());
    calendarInfo.setYear(LocalDateTime.now().getYear());
    calendarInfo.setFilePath(
        CALENDAR_UPLOADS_FOLDER.resolve(file.getOriginalFilename()).toString());
    calendarInfo.setSemester(
        ExamCalendarInfo.Semester.valueOf(calendarContext.getSemesterString()));
    calendarInfo.setSession(
        ExamCalendarInfo.ExamsSession.valueOf(calendarContext.getSessionString()));
    try {
      calendarInfoRepository.save(calendarInfo);
    } catch (Exception e) {
      throw new ExamCalendarException("Error while saving calendar info to the database, " + e);
    }
  }

  @Override
  public Resource loadCalenderFile(Long id) {
    Optional<ExamCalendarInfo> calendarInfo = calendarInfoRepository.findById(id);
    if (calendarInfo.isPresent()) {
      ExamCalendarInfo calendarInfoData = calendarInfo.get();
      return fileStorageService.loadFileAsResource(calendarInfoData.getFileName());
    }
    throw new ExamCalendarException("File Not Found, id: " + id);
  }

  @Override
  public Resource loadCalenderFile(String name) {
    return null;
  }

  @Override
  public ExamCalendarInfo deleteCalender(Long id) {
    Resource calenderFile = loadCalenderFile(id);
    try {
      Files.delete(calenderFile.getFile().toPath());
      ExamCalendarInfo calendarInfo = getCalendarInfo(id);
      calendarInfoRepository.deleteById(id);
      return calendarInfo;
    } catch (IOException e) {
      throw new ExamCalendarException("Could not delete file of id: " + id + " because, " + e);
    }
  }

  @Override
  public ExamCalendarInfo deleteCalender(String name) {
    return deleteCalender(calendarInfoRepository.findExamCalendarInfoByFileName(name).getId());
  }

  @Override
  public ExamCalendarInfo renameCalender(Long calendarId, String name) {
    Optional<ExamCalendarInfo> calendarInfo = calendarInfoRepository.findById(calendarId);
    if (calendarInfo.isPresent()) {
      ExamCalendarInfo calendarData = calendarInfo.get();
      Path oldFilePath = Paths.get(calendarData.getFilePath());
      if (Files.exists(oldFilePath)) {
        if (name != null && !name.trim().isEmpty()) {
          name = name.endsWith(".pdf") ? name.substring(0, name.length() - 4) : name;
          calendarData.setFileName(name);
          calendarData.setFilePath(oldFilePath.getParent().resolve(name.concat(".pdf")).toString());
          Path newFilePath = Paths.get(calendarData.getFilePath());
          try {
            Files.move(oldFilePath, newFilePath);
            return calendarInfoRepository.save(calendarData);
          } catch (IOException ignored) {
          }
        }
      }
    }
    return null;
  }

  @Override
  public ExamCalendarInfo getCalendarInfo(Long id) {
    return calendarInfoRepository.getById(id);
  }

  @Override
  public List<ExamCalendarInfo> getAllCalendarsInfo() {
    return calendarInfoRepository.findAll();
  }

  @Override
  public List<ExamCalendarInfo> findCalenderBy(
      ExamCalendarInfo.Semester semester, ExamCalendarInfo.ExamsSession session) {
    return calendarInfoRepository.findExamCalendarInfoBySemesterAndSession(semester, session);
  }

  @Override
  public List<ExamCalendarInfo> findCalenderBy(ExamCalendarInfo.Semester semester) {
    return calendarInfoRepository.findExamCalendarInfoBySemester(semester);
  }

  @Override
  public List<ExamCalendarInfo> findCalenderBy(ExamCalendarInfo.ExamsSession session) {
    return calendarInfoRepository.findExamCalendarInfoBySession(session);
  }

  private boolean isPdf(MultipartFile file) {
    String filename = file.getOriginalFilename();
    return filename != null && filename.endsWith(".pdf");
  }

  private boolean isCalendarFileExists(MultipartFile file) {
    if (file.getOriginalFilename() == null || file.getOriginalFilename().trim().isEmpty()) {
      return true;
    }
    return CALENDAR_UPLOADS_FOLDER.resolve(file.getOriginalFilename()).toFile().exists();
  }
}

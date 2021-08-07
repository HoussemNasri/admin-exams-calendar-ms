package fsb.aoc.adminexamscalendarms.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import fsb.aoc.adminexamscalendarms.entities.ExamCalendarInfo;
import fsb.aoc.adminexamscalendarms.exceptions.ExamCalendarException;
import fsb.aoc.adminexamscalendarms.model.ExamCalendarContext;
import fsb.aoc.adminexamscalendarms.repositories.ExamCalendarInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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
    calendarInfo.setFileName(file.getOriginalFilename());
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
  public void deleteCalender(Long id) {
    Resource calenderFile = loadCalenderFile(id);
    try {
      Files.delete(calenderFile.getFile().toPath());
      calendarInfoRepository.deleteById(id);
    } catch (IOException e) {
      throw new ExamCalendarException("Could not delete file of id: " + id + " because, " + e);
    }
  }

  @Override
  public void deleteCalender(String name) {}

  @Override
  public ExamCalendarInfo getCalendarInfo(Long id) {
    return null;
  }

  @Override
  public List<ExamCalendarInfo> getAllCalendarsInfo() {
    return calendarInfoRepository.findAll();
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

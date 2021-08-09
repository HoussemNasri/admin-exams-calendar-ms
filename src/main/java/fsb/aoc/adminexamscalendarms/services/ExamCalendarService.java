package fsb.aoc.adminexamscalendarms.services;

import java.util.List;

import fsb.aoc.adminexamscalendarms.entities.ExamCalendarInfo;
import fsb.aoc.adminexamscalendarms.model.ExamCalendarContext;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

public interface ExamCalendarService {

  void saveCalender(MultipartFile file, ExamCalendarContext details);

  Resource loadCalenderFile(Long id);

  Resource loadCalenderFile(String name);

  ExamCalendarInfo deleteCalender(Long id);

  ExamCalendarInfo deleteCalender(String name);

  ExamCalendarInfo renameCalender(Long calendarId, String name);

  ExamCalendarInfo updateCalenderFile(Long id, MultipartFile file);

  ExamCalendarInfo getCalendarInfo(Long id);

  List<ExamCalendarInfo> getAllCalendarsInfo();

  List<ExamCalendarInfo> findCalenderBy(
      ExamCalendarInfo.Semester semester, ExamCalendarInfo.ExamsSession session);

  List<ExamCalendarInfo> findCalenderBy(ExamCalendarInfo.Semester semester);

  List<ExamCalendarInfo> findCalenderBy(ExamCalendarInfo.ExamsSession session);
}

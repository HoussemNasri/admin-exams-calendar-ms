package fsb.aoc.adminexamscalendarms.services;

import java.util.List;

import fsb.aoc.adminexamscalendarms.entities.ExamCalendarInfo;
import fsb.aoc.adminexamscalendarms.model.ExamCalendarContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


public interface ExamCalendarService {

  void saveCalender(MultipartFile file, ExamCalendarContext details);

  Resource loadCalenderFile(Long id);

  Resource loadCalenderFile(String name);

  void deleteCalender(Long id);

  void deleteCalender(String name);

  ExamCalendarInfo getCalendarInfo(Long id);

  List<ExamCalendarInfo> getAllCalendarsInfo();
}

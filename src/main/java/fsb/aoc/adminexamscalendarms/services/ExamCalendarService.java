package fsb.aoc.adminexamscalendarms.services;

import java.util.List;

import fsb.aoc.adminexamscalendarms.entities.ExamCalendarInfo;
import fsb.aoc.adminexamscalendarms.model.ExamCalendarContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


public interface ExamCalendarService {

  void saveCalendarFile(MultipartFile file, ExamCalendarContext details);

  Resource loadCalendarFile(Long id);

  Resource loadCalendarFile(String name);

  void deleteCalendarFile(Long id);

  void deleteCalendarFile(String name);

  ExamCalendarInfo getCalendarInfo(Long id);

  List<ExamCalendarInfo> getAllExamsCalendarInfo();
}

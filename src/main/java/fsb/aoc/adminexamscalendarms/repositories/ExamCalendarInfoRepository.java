package fsb.aoc.adminexamscalendarms.repositories;

import java.util.List;

import fsb.aoc.adminexamscalendarms.entities.ExamCalendarInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamCalendarInfoRepository extends JpaRepository<ExamCalendarInfo, Long> {
  ExamCalendarInfo findExamCalendarInfoByFileName(String fileName);

  List<ExamCalendarInfo> findExamCalendarInfoBySemester(ExamCalendarInfo.Semester semester);

  List<ExamCalendarInfo> findExamCalendarInfoBySession(ExamCalendarInfo.ExamsSession session);

  List<ExamCalendarInfo> findExamCalendarInfoBySemesterAndSession(ExamCalendarInfo.Semester semester, ExamCalendarInfo.ExamsSession session);
}

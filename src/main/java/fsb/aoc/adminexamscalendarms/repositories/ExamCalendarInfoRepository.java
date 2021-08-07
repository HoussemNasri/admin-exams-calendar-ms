package fsb.aoc.adminexamscalendarms.repositories;

import fsb.aoc.adminexamscalendarms.entities.ExamCalendarInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamCalendarInfoRepository extends JpaRepository<ExamCalendarInfo, Long> {}

package fsb.aoc.adminexamscalendarms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Additional information to save the ExamCalendar in the right location. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamCalendarContext {
  private String semesterString;
  private String sessionString;
}

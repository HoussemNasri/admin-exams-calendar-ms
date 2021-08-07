package fsb.aoc.adminexamscalendarms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Additional information for filtering calenders */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamCalendarContext {
  private String semesterString;
  private String sessionString;
}

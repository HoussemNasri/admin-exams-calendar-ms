package fsb.aoc.adminexamscalendarms.entities;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamCalendarInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String fileName;

  @JsonIgnore
  private String filePath;

  private Long fileSize;

  private Integer year;

  @Enumerated(EnumType.ORDINAL)
  private Semester semester;

  @Enumerated(EnumType.ORDINAL)
  private ExamsSession session;

  public enum ExamsSession {
    PRINCIPALE,
    RATTRAPAGE,
    COVID
  }

  public enum Semester {
    FIRST,
    SECOND
  }
}

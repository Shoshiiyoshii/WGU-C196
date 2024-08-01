package model.entities;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.ColumnInfo;

import java.time.LocalDate;

@Entity(tableName = "assessments", indices = @Index("course_id"),
        foreignKeys = @ForeignKey(entity = Course.class,
                                    parentColumns = "course_id",
                                    childColumns = "course_id",
                                    onDelete = ForeignKey.CASCADE))
public class Assessment {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "assessment_id")
    private int assessmentId;

    @ColumnInfo(name = "course_id")
    private Integer courseId;

    @ColumnInfo(name = "assessment_name")
    private String assessmentName;

    @ColumnInfo(name = "assessment_start_date")
    private LocalDate assessmentStartDate;

    @ColumnInfo(name = "assessment_due_date")
    private LocalDate assessmentDueDate;

    @ColumnInfo(name = "assessment_type")
    private String assessmentType;

    public Assessment(String assessmentName, String assessmentType, Integer courseId,
                      LocalDate assessmentStartDate, LocalDate assessmentDueDate){
        this.assessmentName = assessmentName;
        this.assessmentType = assessmentType;
        this.courseId = courseId;
        this.assessmentStartDate = assessmentStartDate;
        this.assessmentDueDate = assessmentDueDate;

    }

    //accessors
    public int getAssessmentId() {
        return assessmentId;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public String getAssessmentName() {
        return assessmentName;
    }

    public LocalDate getAssessmentStartDate() {
        return assessmentStartDate;
    }

    public LocalDate getAssessmentDueDate() {
        return assessmentDueDate;
    }

    public String getAssessmentType() {
        return assessmentType;
    }

    //mutators

    public void setAssessmentId (int assessmentId) {
        this.assessmentId = assessmentId;
    }

    public void setCourseId (Integer courseId) {
        this.courseId = courseId;
    }

    public void setAssessmentName(String assessmentName) {
        this.assessmentName = assessmentName;
    }

    public void setAssessmentStartDate(LocalDate assessmentStartDate) {
        this.assessmentStartDate = assessmentStartDate;
    }

    public void setAssessmentDueDate(LocalDate assessmentDueDate) {
        this.assessmentDueDate = assessmentDueDate;
    }

    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }
}

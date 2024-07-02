package model.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.time.LocalDate;

@Entity(tableName = "assessments", foreignKeys = @ForeignKey(entity = Course.class,
                                                                parentColumns = "course_id",
                                                                childColumns = "course_Id",
                                                                onDelete = ForeignKey.CASCADE))
public class Assessment {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "assessment_id")
    private long assessmentId;

    @ColumnInfo(name = "course_id")
    private long courseId;

    @ColumnInfo(name = "assessment_name")
    private String assessmentName;

    @ColumnInfo(name = "assessment_start_date")
    private LocalDate assessmentStartDate;

    @ColumnInfo(name = "assessment_due_date")
    private LocalDate assessmentDueDate;

    @ColumnInfo(name = "assessment_type")
    private String assessmentType;

    public Assessment(long courseId, String assessmentName, LocalDate assessmentStartDate,
                      LocalDate assessmentDueDate, String assessmentType){
        this.courseId = courseId;
        this.assessmentName = assessmentName;
        this.assessmentStartDate = assessmentStartDate;
        this.assessmentDueDate = assessmentDueDate;
        this.assessmentType = assessmentType;
    }

    //accessors
    public long getAssessmentId() {
        return assessmentId;
    }

    public long getCourseId() {
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
    public void setCourseId(long courseId) {
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

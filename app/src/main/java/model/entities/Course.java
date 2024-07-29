package model.entities;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.ColumnInfo;
import java.time.LocalDate;

@Entity(tableName = "courses", indices = {@Index("term_id")},
        foreignKeys = { @ForeignKey(entity = Term.class,
                parentColumns = "term_id",
                childColumns = "term_id",
                onDelete = ForeignKey.RESTRICT)})
public class Course {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "course_id")
    private int courseId;

    @ColumnInfo(name = "term_id")
    private Integer termId;

    @ColumnInfo(name = "course_name")
    private String courseName;

    @ColumnInfo(name = "start_date")
    private LocalDate startDate;

    @ColumnInfo(name = "end_date")
    private LocalDate endDate;

    @ColumnInfo(name = "instructor_name")
    private String instructorName;

    @ColumnInfo(name = "instructor_email")
    private String instructorEmail;

    @ColumnInfo(name = "instructor_phone")
    private String instructorPhone;

    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "course_note")
    private String courseNote;

    public Course(Integer termId, String courseName, LocalDate startDate,
                  LocalDate endDate, String instructorName, String instructorEmail,
                  String instructorPhone, String status, String courseNote){
        this.termId = termId;
        this.courseName = courseName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.instructorName = instructorName;
        this.instructorEmail = instructorEmail;
        this.instructorPhone = instructorPhone;
        this.status = status;
        this.courseNote = courseNote;
    }

    //accessors
    public int getCourseId() {
        return courseId;
    }

    public Integer getTermId() {
        return termId;
    }

    public String getCourseName() {
        return courseName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getInstructorName(){ return instructorName; }

    public String getInstructorEmail() { return instructorEmail; }

    public String getInstructorPhone() { return instructorPhone; }

    public String getStatus() {
        return status;
    }

    public String getCourseNote() { return courseNote; }


    //mutators

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public void setTermId(Integer termId) {
        this.termId = termId;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setInstructorPhone(String instructorPhone) { this.instructorPhone = instructorPhone;}

    public void setInstructorName(String instructorName) { this.instructorName = instructorName; }

    public void setInstructorEmail(String instructorEmail) { this.instructorEmail = instructorEmail; }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCourseNote(String courseNote) { this.courseNote = courseNote; }

}

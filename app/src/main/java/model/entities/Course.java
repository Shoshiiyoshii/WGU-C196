package model.entities;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.ColumnInfo;


import java.time.LocalDate;

@Entity(tableName = "courses", indices = {@Index("term_id"), @Index("instructor_id")},
        foreignKeys = { @ForeignKey(entity = Term.class,
                                    parentColumns = "term_id",
                                    childColumns = "term_id",
                                    onDelete = ForeignKey.RESTRICT),
                        @ForeignKey(entity = Instructor.class,
                                    parentColumns = "instructor_id",
                                    childColumns = "instructor_id",
                                    onDelete = ForeignKey.CASCADE)})
public class Course {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "course_id")
    private int courseId;

    @ColumnInfo(name = "term_id")
    private int termId;

    @ColumnInfo(name = "instructor_id")
    private int instructorId;

    @ColumnInfo(name = "course_name")
    private String courseName;

    @ColumnInfo(name = "start_date")
    private LocalDate startDate;

    @ColumnInfo(name = "end_date")
    private LocalDate endDate;

    @ColumnInfo(name = "status")
    private String status;

    public Course(int termId, int instructorId, String courseName, LocalDate startDate,
                  LocalDate endDate, String status){
        this.termId = termId;
        this.instructorId = instructorId;
        this.courseName = courseName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    //accessors
    public int getCourseId() {
        return courseId;
    }

    public int getTermId() {
        return termId;
    }

    public int getInstructorId() {
        return instructorId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    //mutators

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public void setTermId(int termId) {
        this.termId = termId;
    }

    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}

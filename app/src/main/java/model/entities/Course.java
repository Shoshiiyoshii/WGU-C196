package model.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.time.LocalDate;

@Entity(tableName = "courses", foreignKeys = { @ForeignKey(entity = Term.class,
                                                                    parentColumns = "term_id",
                                                                    childColumns = "term_Id",
                                                                    onDelete = ForeignKey.RESTRICT),
                                                @ForeignKey(entity = Instructor.class,
                                                                    parentColumns = "instructor_id",
                                                                    childColumns = "instructor_id",
                                                                    onDelete = ForeignKey.CASCADE)})
public class Course {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "course_id")
    private long courseId;

    @ColumnInfo(name = "term_id")
    private long termId;

    @ColumnInfo(name = "instructor_id")
    private long instructorId;

    @ColumnInfo(name = "course_name")
    private String courseName;

    @ColumnInfo(name = "start_date")
    private LocalDate startDate;

    @ColumnInfo(name = "end_date")
    private LocalDate endDate;

    @ColumnInfo(name = "status")
    private String status;

    public Course(long termId, long instructorId, String courseName, LocalDate startDate,
                  LocalDate endDate, String status){
        this.termId = termId;
        this.instructorId = instructorId;
        this.courseName = courseName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    //accessors
    public long getCourseId() {
        return courseId;
    }

    public long getTermId() {
        return termId;
    }

    public long getInstructorId() {
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
    public void setTermId(long termId) {
        this.termId = termId;
    }

    public void setInstructorId(long instructorId) {
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

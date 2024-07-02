package model.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "instructors")
public class Instructor {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "instructor_id")
    private long instructorId;

    @ColumnInfo(name = "instructor_name")
    private String instructorName;

    @ColumnInfo(name = "instructor_email")
    private String instructorEmail;

    @ColumnInfo(name = "instructor_phone")
    private String instructorPhone;

    public Instructor(String instructorName, String instructorEmail, String instructorPhone){
        this.instructorName = instructorName;
        this.instructorPhone = instructorPhone;
        this.instructorEmail = instructorEmail;
    }

    //accessors
    public long getInstructorId() {
        return instructorId;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public String getInstructorEmail() {
        return instructorEmail;
    }

    public String getInstructorPhone() {
        return instructorPhone;
    }

    //mutators
    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public void setInstructorEmail(String instructorEmail) {
        this.instructorEmail = instructorEmail;
    }

    public void setInstructorPhone(String instructorPhone) {
        this.instructorPhone = instructorPhone;
    }
}

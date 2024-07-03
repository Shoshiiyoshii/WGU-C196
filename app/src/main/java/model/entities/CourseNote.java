package model.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.ColumnInfo;

import java.time.LocalDate;

@Entity(tableName = "course_notes", foreignKeys = @ForeignKey(entity = Course.class,
                                                                parentColumns = "course_id",
                                                                childColumns = "course_id",
                                                                onDelete = ForeignKey.CASCADE))
public class CourseNote {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "note_id")
    private long noteId;

    @ColumnInfo(name = "course_id")
    private long courseId;

    @ColumnInfo(name = "note_details")
    private String noteDetails;

    public CourseNote(long courseId, String noteDetails){
        this.courseId = courseId;
        this.noteDetails = noteDetails;
    }

    //accessors
    public long getNoteId() {
        return noteId;
    }

    public long getCourseId() {
        return courseId;
    }

    public String getNoteDetails() {
        return noteDetails;
    }

    //mutators

    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public void setNoteDetails(String note) {
        this.noteDetails = noteDetails;
    }
}

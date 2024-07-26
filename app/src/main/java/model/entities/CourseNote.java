package model.entities;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.ColumnInfo;

import java.time.LocalDate;

@Entity(tableName = "course_notes", indices = @Index("course_id"),
        foreignKeys = @ForeignKey(entity = Course.class,
                                    parentColumns = "course_id",
                                    childColumns = "course_id",
                                    onDelete = ForeignKey.CASCADE))
public class CourseNote {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "note_id")
    private int noteId;

    @ColumnInfo(name = "course_id")
    private int courseId;

    @ColumnInfo(name = "note_details")
    private String noteDetails;

    public CourseNote(int courseId, String noteDetails){
        this.courseId = courseId;
        this.noteDetails = noteDetails;
    }

    //accessors
    public int getNoteId() {
        return noteId;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getNoteDetails() {
        return noteDetails;
    }

    //mutators

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public void setNoteDetails(String note) {
        this.noteDetails = noteDetails;
    }
}

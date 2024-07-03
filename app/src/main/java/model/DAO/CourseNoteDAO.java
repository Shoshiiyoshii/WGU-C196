package model.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;

import java.util.List;

import model.entities.CourseNote;

@Dao
public interface CourseNoteDAO {
    @Insert
    void insert(CourseNote courseNote);

    @Update
    void update(CourseNote courseNote);


    @Delete
    void delete(CourseNote courseNote);

    @Query("SELECT * FROM course_notes WHERE note_id = :id")
    CourseNote getCourseNoteById(long id);

    @Query("SELECT * FROM course_notes")
    List<CourseNote> getAllCourseNotes();
}


package model.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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


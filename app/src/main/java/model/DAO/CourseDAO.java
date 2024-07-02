package model.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import model.entities.Course;

@Dao
public interface CourseDAO {
    @Insert
    void insert(Course course);

    @Update
    void update(Course course);


    @Delete
    void delete(Course course);

    @Query("SELECT * FROM courses WHERE course_id = :id")
    Course getCourseById(long id);

    @Query("SELECT * FROM courses")
    List<Course> getAllCourses();
}

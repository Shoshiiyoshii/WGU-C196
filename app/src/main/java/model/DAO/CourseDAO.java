package model.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
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
    Course getCourseById(int id);

    @Query("SELECT * FROM courses WHERE term_id = :termId")
    List<Course> getCoursesForTerm(int termId);


    @Query("SELECT * FROM courses")
    List<Course> getAllCourses();
}

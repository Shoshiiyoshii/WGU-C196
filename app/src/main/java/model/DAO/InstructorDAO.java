package model.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;

import java.util.List;

import model.entities.Instructor;


@Dao
public interface InstructorDAO {
    @Insert
    void insert(Instructor instructor);

    @Update
    void update(Instructor instructor);


    @Delete
    void delete(Instructor instructor);

    @Query("SELECT * FROM instructors WHERE instructor_id = :id")
    Instructor getInstructorById(int id);

    @Query("SELECT * FROM instructors")
    List<Instructor> getAllInstructors();
}

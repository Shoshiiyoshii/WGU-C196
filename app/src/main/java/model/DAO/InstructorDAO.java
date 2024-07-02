package model.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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
    Instructor getInstructorById(long id);

    @Query("SELECT * FROM instructors")
    List<Instructor> getAllInstructors();
}

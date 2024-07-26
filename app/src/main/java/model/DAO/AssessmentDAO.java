package model.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;

import java.util.List;

import model.entities.Assessment;

@Dao
public interface AssessmentDAO {
    @Insert
    void insert(Assessment assessment);

    @Update
    void update(Assessment assessment);


    @Delete
    void delete(Assessment assessment);

    @Query("SELECT * FROM assessments WHERE assessment_id = :id")
    Assessment getAssessmentById(int id);

    @Query("SELECT * FROM assessments")
    List<Assessment> getAllAssessments();
}

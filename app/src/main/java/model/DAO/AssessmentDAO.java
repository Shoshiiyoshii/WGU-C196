package model.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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
    Assessment getAssessmentById(long id);

    @Query("SELECT * FROM assessments")
    List<Assessment> getAllAssessments();
}

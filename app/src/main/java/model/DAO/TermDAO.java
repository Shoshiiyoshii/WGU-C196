package model.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;

import java.util.List;

import model.entities.Term;

@Dao
public interface TermDAO {

    @Insert
    long insert(Term term);

    @Update
    void update(Term term);

    @Delete
    void delete(Term term);

    @Query("SELECT * FROM terms WHERE term_id = :id")
    Term getTermById(int id);

    @Query("SELECT * FROM terms")
    List<Term> getAllTerms();
}

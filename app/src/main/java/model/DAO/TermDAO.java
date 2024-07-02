package model.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import model.entities.Term;

@Dao
public interface TermDAO {

    @Insert
    void insert(Term term);

    @Update
    void update(Term term);


    @Delete
    void delete(Term term);

    @Query("SELECT * FROM terms WHERE term_id = :id")
    Term getTermById(long id);

    @Query("SELECT * FROM terms")
    List<Term> getAllTerms();
}

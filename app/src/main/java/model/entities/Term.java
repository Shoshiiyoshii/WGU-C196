package model.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.time.LocalDate;

@Entity(tableName = "terms")
public class Term {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "term_id")
    private long termId;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "start_date")
    private LocalDate startDate;

    @ColumnInfo(name = "end_date")
    private LocalDate endDate;

    public Term(String title, LocalDate startDate, LocalDate endDate){
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    //accessors
    public long getTermId() {
        return termId;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    //mutators
    public void setTitle(String title) {
        this.title = title;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}

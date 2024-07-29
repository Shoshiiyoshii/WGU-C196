package model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import model.DAO.*;
import model.entities.*;

@Database(entities = {Term.class, Course.class, Assessment.class}, version = 2, exportSchema = false)
@TypeConverters(MyTypeConverters.class)
public abstract class StudentDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "student_database.db";

    public abstract TermDAO termDAO();
    public abstract CourseDAO courseDAO();
    public abstract AssessmentDAO assessmentDAO();

    private static volatile StudentDatabase INSTANCE;

    public static StudentDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (StudentDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    StudentDatabase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

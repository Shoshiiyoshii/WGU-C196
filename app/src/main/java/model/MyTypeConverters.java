package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import androidx.room.TypeConverter;

public class MyTypeConverters {

    //Using standard American date format
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    //convert LocalDate to String to store in database
    @TypeConverter
    public static String fromLocalDate(LocalDate localDate) {
        return localDate == null ? null : localDate.format(formatter);
    }

    //convert String to LocalDate to display in app
    @TypeConverter
    public static LocalDate toLocalDate(String date) {
        if (date == null) {
            return null;
        }
            try {
                return LocalDate.parse(date, formatter);
            } catch (DateTimeParseException ignored) {

        }

        throw new IllegalArgumentException("Date format not supported: " + date);
    }
}


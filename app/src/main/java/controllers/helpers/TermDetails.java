package controllers.helpers;

import java.util.List;

import model.entities.Course;
import model.entities.Term;

public class TermDetails {
    private final Term term;
    private final List<Course> courses;

    public TermDetails(Term term, List<Course> courses) {
        this.term = term;
        this.courses = courses;
    }

    public Term getTerm() {
        return term;
    }

    public List<Course> getCourses() {
        return courses;
    }
}


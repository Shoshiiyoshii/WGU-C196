package controllers.helpers;

import java.util.List;
import java.util.concurrent.Callable;

import model.DAO.CourseDAO;
import model.DAO.TermDAO;
import model.entities.Course;
import model.entities.Term;

public class GetTermDetails implements Callable<TermDetails> {
    private final TermDAO termDAO;
    private final CourseDAO courseDAO;
    private final int termID;

    public GetTermDetails(TermDAO termDAO, int termID, CourseDAO courseDAO){
        this.termDAO = termDAO;
        this.termID = termID;
        this.courseDAO = courseDAO;
    }

    @Override
    public TermDetails call() throws Exception {
        Term term = termDAO.getTermById(termID);
        List<Course> courses = courseDAO.getCoursesForTerm(termID);
        return new TermDetails(term, courses);
    }
}

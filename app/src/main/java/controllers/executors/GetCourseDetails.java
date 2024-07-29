package controllers.executors;

import java.util.List;
import java.util.concurrent.Callable;

import model.DAO.CourseDAO;
import model.DAO.AssessmentDAO;
import model.entities.Assessment;
import model.entities.Course;
import model.entities.Term;

public class GetCourseDetails implements Callable<CourseDetails> {
    private AssessmentDAO assessmentDAO;
    private CourseDAO courseDAO;
    private int courseID;

    public GetCourseDetails(CourseDAO courseDAO, int courseID, AssessmentDAO assessmentDAO){
        this.courseDAO = courseDAO;
        this.courseID = courseID;
        this.assessmentDAO = assessmentDAO;
    }

    @Override
    public CourseDetails call() throws Exception {
        Course course = courseDAO.getCourseById(courseID);
        List<Assessment> assessments = assessmentDAO.getAssessmentsForCourse(courseID);
        return new CourseDetails(course, assessments);
    }
}

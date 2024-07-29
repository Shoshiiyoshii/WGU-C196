package controllers.executors;

import java.util.List;

import model.entities.Assessment;
import model.entities.Course;

public class CourseDetails {
    private final Course course;
    private final List<Assessment> assessments;

    public CourseDetails(Course course, List<Assessment> assessments) {
      this.course = course;
      this.assessments = assessments;
    }

    public Course getCourse() {
        return course;
    }

    public List<Assessment> getAssessments() {
        return assessments;
    }
}
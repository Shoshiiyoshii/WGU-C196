package controllers.adapters;

import android.content.Intent;


import model.entities.Course;
import controllers.views.CourseDetailsActivity; // Make sure this import matches the actual path

public class ListCourseRecyclerViewAdapter extends BaseCourseRecyclerViewAdapter {
    @Override
    protected void bindCourse(CourseViewHolder holder, Course course) {
        // Set the course name
        holder.textViewName.setText(course.getCourseName());

        // Set click listener to open CourseDetailsActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), CourseDetailsActivity.class);
            int courseId = course.getCourseId();

            intent.putExtra("COURSE_ID", courseId);
            v.getContext().startActivity(intent);
        });
    }
}

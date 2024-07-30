package controllers.adapters;

import android.view.View;
import android.widget.CheckBox;

import com.thomasmccue.c196pastudentapp.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.entities.Course;

public class CheckBoxCourseRecyclerViewAdapter extends BaseCourseRecyclerViewAdapter {
    private final Set<Course> selectedCourses = new HashSet<>();

    @Override
    protected void bindCourse(CourseViewHolder holder, Course course) {
        CheckBox checkBox = holder.itemView.findViewById(R.id.checkBox);
        checkBox.setVisibility(View.VISIBLE);
        checkBox.setChecked(selectedCourses.contains(course));

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedCourses.add(course);
            } else {
                selectedCourses.remove(course);
            }
        });
    }

    public void setSelectedCourses(List<Course> courses, Set<Integer> associatedCourseIds) {
        this.courses = courses;
        selectedCourses.clear();
        for (Course course : courses) {
            if (associatedCourseIds.contains(course.getCourseId())) {
                selectedCourses.add(course);
            }
        }
        notifyDataSetChanged();
    }

    public Set<Course> getSelectedCourses() {
        return selectedCourses;
    }
}

package controllers.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thomasmccue.c196pastudentapp.R;

import java.util.ArrayList;
import java.util.List;

import model.entities.Course;

public abstract class BaseCourseRecyclerViewAdapter extends RecyclerView.Adapter<BaseCourseRecyclerViewAdapter.CourseViewHolder> {
    protected List<Course> courses = new ArrayList<>();

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout defined in list_item.xml
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new CourseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        // Bind the data to the TextView
        Course currentCourse = courses.get(position);
        holder.textViewName.setText(currentCourse.getCourseName());
        bindCourse(holder, currentCourse);
    }

    protected abstract void bindCourse(CourseViewHolder holder, Course course);

    @Override
    public int getItemCount() {
        return courses.size();
    }

    // Method to update the list of courses
    public void setCourses(List<Course> courses) {
        this.courses = courses;
        notifyDataSetChanged();
    }

    // ViewHolder class that holds references to the views within each item in the RecyclerView
    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.itemName);
        }
    }
}

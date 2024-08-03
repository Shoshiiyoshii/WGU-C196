package controllers.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thomasmccue.c196pastudentapp.R;

import java.util.List;
import java.util.Objects;

import controllers.adapters.ListCourseRecyclerViewAdapter;
import model.StudentDatabase;
import model.entities.Course;

public class CourseActivity extends AppCompatActivity {
    private RecyclerView coursesRecyclerView;
    private ListCourseRecyclerViewAdapter coursesRecyclerViewAdapter;
    private StudentDatabase studentDatabase;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_course);

        //set up Action Bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_courses);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up RecyclerView and Adapter
        coursesRecyclerView = findViewById(R.id.recyclerViewCourses);
        coursesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        coursesRecyclerViewAdapter = new ListCourseRecyclerViewAdapter();
        coursesRecyclerView.setAdapter(coursesRecyclerViewAdapter);

        // Initialize emptyView message
        emptyView = findViewById(R.id.noCoursesText);

        // Get instance of the database
        studentDatabase = StudentDatabase.getInstance(getApplicationContext());
        // Load courses from database and update RecyclerView
        loadCourses();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu resource into the Toolbar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection from the menu
        if (item.getItemId() == R.id.nav_home) {
            startActivity(new Intent(CourseActivity.this, MainActivity.class));
            return true;
        } else if (item.getItemId() == R.id.nav_terms) {
            startActivity(new Intent(CourseActivity.this, TermActivity.class));
            return true;
        } else if (item.getItemId() == R.id.nav_courses) {
            startActivity(new Intent(CourseActivity.this, CourseActivity.class));
            return true;
        } else if (item.getItemId() == R.id.nav_assessments) {
            startActivity(new Intent(CourseActivity.this, AssessmentActivity.class));
            return true;
        } else {
            return false;
        }
    }

    private void loadCourses() {
        new Thread(() -> {
            List<Course> courses = studentDatabase.courseDAO().getAllCourses();
            runOnUiThread(() -> {
                if (courses.isEmpty()) {
                    coursesRecyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    coursesRecyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                    coursesRecyclerViewAdapter.setCourses(courses);
                }
            });
        }).start();
    }

    public void courseAddButtonClicked(View view){
        startActivity(new Intent(CourseActivity.this, AddCourseActivity.class));
    }
}
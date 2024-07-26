package controllers.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.thomasmccue.c196pastudentapp.R;

import controllers.adapters.ListCourseRecyclerViewAdapter;
import controllers.adapters.TermRecyclerViewAdapter;
import model.StudentDatabase;

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

        // Set up RecyclerView and Adapter
        coursesRecyclerView = findViewById(R.id.recyclerViewCourses);
        coursesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        coursesRecyclerViewAdapter = new ListCourseRecyclerViewAdapter();
        coursesRecyclerView.setAdapter(coursesRecyclerViewAdapter);

        // Initialize emptyView message
        emptyView = findViewById(R.id.noCoursesText);

        // Get instance of the database
        studentDatabase = StudentDatabase.getInstance(getApplicationContext());
        // Load terms from database and update RecyclerView
        loadCourses();
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_courses);

        bottomNavigationView.setOnItemSelectedListener(item -> {
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
        });
    }

    private void loadCourses() {
    }

    public void courseAddButtonClicked(View view){
        startActivity(new Intent(CourseActivity.this, AddCourseActivity.class));
    }
}
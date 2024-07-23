package controllers.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import controllers.adapters.CheckBoxCourseRecyclerViewAdapter;

import model.StudentDatabase;
import model.entities.Course;
import model.entities.Term;

public class AddTermActivity extends AppCompatActivity {
    private EditText termTitleInput;
    private EditText termStartInput;
    private EditText termEndInput;

    private RecyclerView courseRecyclerView;
    private TextView emptyView;
    private StudentDatabase studentDatabase;
    private CheckBoxCourseRecyclerViewAdapter checkListCourseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_term);

        //allow for insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //find recyclerView
        courseRecyclerView = findViewById(R.id.recyclerViewCourses);
        courseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        checkListCourseAdapter = new CheckBoxCourseRecyclerViewAdapter();
        courseRecyclerView.setAdapter(checkListCourseAdapter);

        // Initialize emptyView message
        emptyView = findViewById(R.id.noCoursesText);


        //get apps database instance
        studentDatabase = StudentDatabase.getInstance(getApplicationContext());

        courseListSetUp();

        //find text input and save button
        termTitleInput = findViewById(R.id.termTitleInput);
        termStartInput = findViewById(R.id.termStartDateInput);
        termEndInput = findViewById(R.id.termEndDateInput);
        Button saveButton = findViewById(R.id.termSaveButton);

        saveButton.setOnClickListener(v -> {
            String name = termTitleInput.getText().toString();
            String startDateString = termStartInput.getText().toString();
            String endDateString = termEndInput.getText().toString();

            // Validate and parse start date FIXME validate dates are after one another and valid
            LocalDate startDate = null;
            try {
                startDate = LocalDate.parse(startDateString, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            } catch (DateTimeParseException e) {
                // Handle invalid start date format
                termStartInput.setError("Invalid date format. Please use MM/DD/YYYY.");
                return; // Exit the click listener
            }

            // Validate and parse end date
            LocalDate endDate = null;
            try {
                endDate = LocalDate.parse(endDateString, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            } catch (DateTimeParseException e) {
                // Handle invalid end date format
                termEndInput.setError("Invalid date format. Please use MM/DD/YYYY.");
                return; // Exit the click listener
            }

            // Create a new Term object
            Term term = new Term(name, startDate, endDate);

            // Insert term into database
            new Thread(() -> {
                studentDatabase.termDAO().insert(term);
                runOnUiThread(() -> {
                    startActivity(new Intent(AddTermActivity.this, TermActivity.class));
                    finish();
                });
            }).start();
        });

        //bottom navigation listeners
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_terms);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(AddTermActivity.this, MainActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_terms) {
                startActivity(new Intent(AddTermActivity.this, TermActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_courses) {
                startActivity(new Intent(AddTermActivity.this, CourseActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_assessments) {
                startActivity(new Intent(AddTermActivity.this, AssessmentActivity.class));
                return true;
            } else {
                return false;
            }
        });
    }

    public void termSaveButtonClicked(View view){

    }

    public void termDiscardButtonClicked(View view){
        startActivity(new Intent(AddTermActivity.this, AssessmentActivity.class));
    }

    private void courseListSetUp() {
        new Thread(() -> {
            List<Course> courses = studentDatabase.courseDAO().getAllCourses();
            runOnUiThread(() -> {
                if (courses.isEmpty()) {
                    courseRecyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    courseRecyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                    checkListCourseAdapter.setCourses(courses);
                }
            });
        }).start();
    }
    
}
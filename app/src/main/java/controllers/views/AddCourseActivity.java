package controllers.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import controllers.adapters.CheckBoxAssessmentRecyclerViewAdapter;
import model.StudentDatabase;
import model.entities.Assessment;
import model.entities.Course;

public class AddCourseActivity extends AppCompatActivity {
    private EditText courseNameInput;
    private EditText courseStartDateInput;
    private EditText courseEndDateInput;
    
    private Spinner courseStatusSpinner;
    private String status;
    
    private EditText instructorNameInput;
    private EditText instructorPhoneInput;
    private EditText instructorEmailInput;

    private TextView emptyView;
    private RecyclerView assessmentRecyclerView;
    
    private EditText courseNotesInput;

    private StudentDatabase studentDatabase;
    private CheckBoxAssessmentRecyclerViewAdapter checkListAssessmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_course);

        // Allow for insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get app's database instance
        studentDatabase = StudentDatabase.getInstance(getApplicationContext());

        //find other UI elements
        courseNameInput = findViewById(R.id.courseNameInput);
        courseStartDateInput = findViewById(R.id.courseStartDateInput);
        courseEndDateInput = findViewById(R.id.courseEndDateInput);

        courseStatusSpinner = findViewById(R.id.courseStatusSpinner);

        instructorNameInput = findViewById(R.id.instructorNameInput);
        instructorPhoneInput = findViewById(R.id.instructorPhoneInput);
        instructorEmailInput = findViewById(R.id.instructorEmailInput);

        courseNotesInput = findViewById(R.id.courseNotesInput);

        // Find and set up RecyclerView
        emptyView = findViewById(R.id.noAssessmentsText);
        assessmentRecyclerView = findViewById(R.id.recyclerViewAssessments);
        assessmentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        checkListAssessmentAdapter = new CheckBoxAssessmentRecyclerViewAdapter();
        assessmentRecyclerView.setAdapter(checkListAssessmentAdapter);

        statusSpinnerSetUp();

        assessmentListSetUp();

        // Bottom navigation listeners
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_courses);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(AddCourseActivity.this, MainActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_terms) {
                startActivity(new Intent(AddCourseActivity.this, TermActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_courses) {
                startActivity(new Intent(AddCourseActivity.this, CourseActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_assessments) {
                startActivity(new Intent(AddCourseActivity.this, AssessmentActivity.class));
                return true;
            } else {
                return false;
            }
        });
    }

    private void assessmentListSetUp() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Assessment> assessments = studentDatabase.assessmentDAO().getAllAssessments();
            runOnUiThread(() -> {
                if (assessments.isEmpty()) {
                    assessmentRecyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    assessmentRecyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                    checkListAssessmentAdapter.setAssessments(assessments);
                }
            });

            executor.shutdown();
        });
    }

    private void statusSpinnerSetUp() {
        // Set up course status drop down using ArrayAdapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.course_status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseStatusSpinner.setAdapter(adapter);
        courseStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                status = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                status = null;
            }
        });
    }

    public void courseSaveButtonClicked(View view) {
        String courseName = courseNameInput.getText().toString();
        String startDateString = courseStartDateInput.getText().toString();
        String endDateString = courseEndDateInput.getText().toString();

        // Validate and parse start date
        LocalDate startDate = null;
        try {
            startDate = LocalDate.parse(startDateString, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } catch (DateTimeParseException e) {
            // Handle invalid start date format
            courseStartDateInput.setError("Invalid date format. Please use MM/DD/YYYY.");
            return; // Exit the click listener
        }

        // Validate and parse end date
        LocalDate endDate = null;
        try {
            endDate = LocalDate.parse(endDateString, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } catch (DateTimeParseException e) {
            // Handle invalid end date format
            courseEndDateInput.setError("Invalid date format. Please use MM/DD/YYYY.");
            return; // Exit the click listener
        }

        //Validate dates are after one another and valid
        if (startDate.isAfter(endDate)) {
            courseEndDateInput.setError("End date must be after start date.");
            return; // Exit the click listener
        }

        String instructorName = instructorNameInput.getText().toString();
        String instructorEmail = instructorEmailInput.getText().toString();
        String instructorPhone = instructorPhoneInput.getText().toString();

        String courseNote = courseNotesInput.getText().toString();

        // Create a new Course object, termId will always be null on create, since termId
        // is added from the term page
        Course course = new Course(null, courseName, startDate, endDate, instructorName,
                instructorEmail, instructorPhone, status, courseNote);

        // Insert course into database and update associated assessments
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Insert course into database and get the generated course ID
            long courseIdLong = studentDatabase.courseDAO().insert(course);

            // Cast courseIdLong to int
            int courseId = (int) courseIdLong;

            // Update associated assessments with the new course ID
            Set<Assessment> selectedAssessments = checkListAssessmentAdapter.getSelectedAssessments();
            for (Assessment assessment : selectedAssessments) {
                assessment.setCourseId(courseId);
                studentDatabase.assessmentDAO().update(assessment);
            }

            // Update UI on the main thread
            runOnUiThread(() -> {
                startActivity(new Intent(AddCourseActivity.this, CourseActivity.class));
                finish();
            });
            executor.shutdown();
        });
    }

    public void courseDiscardButtonClicked(View view) {
        startActivity(new Intent(AddCourseActivity.this, CourseActivity.class));
    }
}

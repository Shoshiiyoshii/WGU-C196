package controllers.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.thomasmccue.c196pastudentapp.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import model.DAO.AssessmentDAO;
import model.DAO.CourseDAO;
import model.StudentDatabase;
import model.entities.Assessment;
import model.entities.Course;

public class AddAssessmentActivity extends AppCompatActivity {
    private EditText assessmentNameInput;
    private Spinner typeSpinner;
    private String type;
    private EditText startDateInput;
    private EditText endDateInput;

    private StudentDatabase studentDatabase;
    private AssessmentDAO assessmentDAO;
    private CourseDAO courseDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_assessment);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        studentDatabase = StudentDatabase.getInstance(getApplicationContext());
        assessmentDAO = studentDatabase.assessmentDAO();
        courseDAO = studentDatabase.courseDAO();

        // find UI elements
        assessmentNameInput = findViewById(R.id.assessmentNameInput);

        setUpTypeSpinner();

        startDateInput = findViewById(R.id.assessmentStartDateInput);
        endDateInput = findViewById(R.id.assessmentEndDateInput);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_assessments);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(AddAssessmentActivity.this, MainActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_terms) {
                startActivity(new Intent(AddAssessmentActivity.this, TermActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_courses) {
                startActivity(new Intent(AddAssessmentActivity.this, CourseActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_assessments) {
                startActivity(new Intent(AddAssessmentActivity.this, AssessmentActivity.class));
                return true;
            } else {
                return false;
            }
        });
    }

    private void setUpTypeSpinner() {
        typeSpinner = findViewById(R.id.assessmentTypeSpinner);
        // Set up course status drop down using ArrayAdapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.assessment_type_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                type = null;
            }
        });
    }

    public void assessmentSaveButtonClicked(View view){
        String assessmentName = assessmentNameInput.getText().toString();
        String startDateString = startDateInput.getText().toString();
        String endDateString = endDateInput.getText().toString();

        // Validate and parse start date
        LocalDate startDate = null;
        try {
            startDate = LocalDate.parse(startDateString, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } catch (DateTimeParseException e) {
            // Handle invalid start date format
            startDateInput.setError("Invalid date format. Please use MM/DD/YYYY.");
            return; // Exit the click listener
        }

        // Validate and parse end date
        LocalDate endDate = null;
        try {
            endDate = LocalDate.parse(endDateString, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } catch (DateTimeParseException e) {
            // Handle invalid end date format
            endDateInput.setError("Invalid date format. Please use MM/DD/YYYY.");
            return; // Exit the click listener
        }

        //Validate dates are after one another and valid
        if (startDate.isAfter(endDate)) {
            endDateInput.setError("End date must be after start date.");
            return; // Exit the click listener
        }

        // Create a new Assessment object, courseId will be null until assigned in CourseEditActivity
        Assessment assessment = new Assessment(assessmentName, type, null, startDate, endDate);

        // Insert course into database and update associated assessments
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Insert assessment into database
           studentDatabase.assessmentDAO().insert(assessment);

            // Update UI on the main thread
            runOnUiThread(() -> {
                Toast.makeText(this, "Assessment added successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddAssessmentActivity.this, AssessmentActivity.class));
                finish();
            });
            executor.shutdown();
        });
    }

    public void assessmentDiscardButtonClicked(View view){
        Toast.makeText(this, "New Assessment Discarded", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(AddAssessmentActivity.this, AssessmentActivity.class));
    }
}
package controllers.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.thomasmccue.c196pastudentapp.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import model.DAO.AssessmentDAO;
import model.StudentDatabase;
import model.entities.Assessment;

public class EditAssessmentActivity extends AppCompatActivity {
    private int assessmentId;
    private EditText assessmentNameInput;
    private String assessmentName;

    private Spinner assessmentTypeSpinner;
    private String assessmentType;
    private String updatedType;

    private long startDateLong;
    private long endDateLong;
    private EditText startDateInput;
    private EditText endDateInput;

    private StudentDatabase studentDatabase;
    private AssessmentDAO assessmentDAO;
    private Assessment assessment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_assessment);


        // Allow for insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        assessmentNameInput = findViewById(R.id.assessmentNameInput);
        startDateInput = findViewById(R.id.assessmentStartDateInput);
        endDateInput = findViewById(R.id.assessmentEndDateInput);
        assessmentTypeSpinner = findViewById(R.id.assessmentTypeSpinner);


        studentDatabase = StudentDatabase.getInstance(getApplicationContext());
        assessmentDAO = studentDatabase.assessmentDAO();

        // Retrieve assessment details from intent
        Intent intent = getIntent();
        assessmentId = intent.getIntExtra("ASSESSMENT_ID", -1);
        assessmentName = intent.getStringExtra("ASSESSMENT_TITLE");
        assessmentType = intent.getStringExtra("ASSESSMENT_TYPE");
        startDateLong = intent.getLongExtra("ASSESSMENT_START_DATE", -1);
        endDateLong = intent.getLongExtra("ASSESSMENT_END_DATE", -1);

        // Populate the text inputs
        populateTextInputs(assessmentName, startDateLong, endDateLong);
        
        setUpTypeSpinner(assessmentType);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_assessments);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(EditAssessmentActivity.this, MainActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_terms) {
                startActivity(new Intent(EditAssessmentActivity.this, TermActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_courses) {
                startActivity(new Intent(EditAssessmentActivity.this, CourseActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_assessments) {
                startActivity(new Intent(EditAssessmentActivity.this, AssessmentActivity.class));
                return true;
            } else {
                return false;
            }
        });
    }

    private void setUpTypeSpinner(String assessmentType) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.assessment_type_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assessmentTypeSpinner.setAdapter(adapter);

        // Set the spinner to this assessments type
        if (assessmentType != null) {
            int position = adapter.getPosition(assessmentType);
            if (position >= 0) { // Ensure the position is valid
                assessmentTypeSpinner.setSelection(position);
            }
        }

        // set listener for type selection
        assessmentTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updatedType = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updatedType = null;
            }
        });
    }

    private void populateTextInputs(String assessmentName, long startDateLong, long endDateLong) {
        assessmentNameInput.setText(assessmentName);

        LocalDate assessmentStart = LocalDate.ofEpochDay(startDateLong);
        LocalDate assessmentEnd = LocalDate.ofEpochDay(endDateLong);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String formattedStartDate = assessmentStart.format(formatter);
        String formattedEndDate = assessmentEnd.format(formatter);

        startDateInput.setText(formattedStartDate);
        endDateInput.setText(formattedEndDate);
    }

    public void assessmentSaveButtonClicked(View view){
        String updatedAssessmentName = assessmentNameInput.getText().toString();
        String updatedStartDateString = startDateInput.getText().toString();
        String updatedEndDateString = endDateInput.getText().toString();

        // Validate and parse start date
        LocalDate updatedStartDate = null;
        try {
            updatedStartDate = LocalDate.parse(updatedStartDateString, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } catch (DateTimeParseException e) {
            // Handle invalid start date format
            startDateInput.setError("Invalid date format. Please use MM/DD/YYYY.");
            return; // Exit the click listener
        }

        // Validate and parse end date
        LocalDate updatedEndDate = null;
        try {
            updatedEndDate = LocalDate.parse(updatedEndDateString, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } catch (DateTimeParseException e) {
            // Handle invalid end date format
            endDateInput.setError("Invalid date format. Please use MM/DD/YYYY.");
            return; // Exit the click listener
        }

        //Validate dates are after one another and valid
        if (updatedStartDate.isAfter(updatedEndDate)) {
            endDateInput.setError("End date must be after start date.");
            return; // Exit the click listener
        }

        // Insert course into database and update associated assessments
        ExecutorService executor = Executors.newSingleThreadExecutor();
        LocalDate finalUpdatedStartDate = updatedStartDate;
        LocalDate finalUpdatedEndDate = updatedEndDate;
        executor.execute(() -> {
            // Insert assessment into database
            Assessment existingAssessment = assessmentDAO.getAssessmentById(assessmentId);

            existingAssessment.setAssessmentName(updatedAssessmentName);
            existingAssessment.setAssessmentType(updatedType);
            existingAssessment.setAssessmentStartDate(finalUpdatedStartDate);
            existingAssessment.setAssessmentDueDate(finalUpdatedEndDate);

            studentDatabase.assessmentDAO().update(existingAssessment);

            // Update UI on the main thread
            runOnUiThread(() -> {
                Toast.makeText(this, "Assessment Updated Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(EditAssessmentActivity.this, AssessmentActivity.class));
                finish();
            });
            executor.shutdown();
        });
    }

    public void assessmentDiscardButtonClicked(View view){
        Toast.makeText(this, "Assessment Updates Discarded", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(EditAssessmentActivity.this, AssessmentActivity.class));
    }
}

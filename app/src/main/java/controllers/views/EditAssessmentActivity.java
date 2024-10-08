package controllers.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.thomasmccue.c196pastudentapp.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import controllers.helpers.NotificationScheduler;
import model.DAO.AssessmentDAO;
import model.StudentDatabase;
import model.entities.Assessment;

public class EditAssessmentActivity extends AppCompatActivity {
    private int assessmentId;
    private EditText assessmentNameInput;

    private Spinner assessmentTypeSpinner;
    private String updatedType;

    private EditText startDateInput;
    private SwitchCompat assessmentStartNotificationSwitch;
    private boolean turnStartNotificationOn;

    private EditText endDateInput;
    private SwitchCompat assessmentEndNotificationSwitch;
    private boolean turnEndNotificationOn;

    private StudentDatabase studentDatabase;
    private AssessmentDAO assessmentDAO;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_assessment);

        // Set up action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.assessment_edit);

        // Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        assessmentNameInput = findViewById(R.id.assessmentNameInput);

        startDateInput = findViewById(R.id.assessmentStartDateInput);
        assessmentStartNotificationSwitch = findViewById(R.id.assessmentStartNotificationSwitch);
        setupStartNotificationSwitchListener();

        endDateInput = findViewById(R.id.assessmentEndDateInput);
        assessmentEndNotificationSwitch = findViewById(R.id.assessmentEndNotificationSwitch);
        setupEndNotificationSwitchListener();

        assessmentTypeSpinner = findViewById(R.id.assessmentTypeSpinner);


        studentDatabase = StudentDatabase.getInstance(getApplicationContext());
        assessmentDAO = studentDatabase.assessmentDAO();

        // Retrieve assessment details from intent
        Intent intent = getIntent();
        assessmentId = intent.getIntExtra("ASSESSMENT_ID", -1);
        String assessmentName = intent.getStringExtra("ASSESSMENT_TITLE");
        String assessmentType = intent.getStringExtra("ASSESSMENT_TYPE");
        long startDateLong = intent.getLongExtra("ASSESSMENT_START_DATE", -1);
        long endDateLong = intent.getLongExtra("ASSESSMENT_END_DATE", -1);

        // Populate the text inputs
        populateTextInputs(assessmentName, startDateLong, endDateLong);
        
        setUpTypeSpinner(assessmentType);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
    }

    private void setupStartNotificationSwitchListener() {
        // Set initial state
        assessmentStartNotificationSwitch.setChecked(false);

        // Set a listener to detect changes
        assessmentStartNotificationSwitch.setOnCheckedChangeListener((buttonView, notificationsOn) -> {
            // Switch is on or switch is off
            turnStartNotificationOn = notificationsOn;
        });
    }

    private void setupEndNotificationSwitchListener() {
        // Set initial state
        assessmentEndNotificationSwitch.setChecked(false);

        // Set a listener to detect changes
        assessmentEndNotificationSwitch.setOnCheckedChangeListener((buttonView, notificationsOn) -> {
            // Switch is on or switch is off
            turnEndNotificationOn = notificationsOn;
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
            if (position >= 0) {
                assessmentTypeSpinner.setSelection(position);
            }
        }

        // Set listener for type selection
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
        LocalDate updatedStartDate;
        try {
            updatedStartDate = LocalDate.parse(updatedStartDateString, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } catch (DateTimeParseException e) {
            // Handle invalid start date format
            startDateInput.setError("Invalid date format. Please use MM/DD/YYYY.");
            return; // Exit if invalid
        }

        // Validate and parse end date
        LocalDate updatedEndDate;
        try {
            updatedEndDate = LocalDate.parse(updatedEndDateString, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } catch (DateTimeParseException e) {
            // Handle invalid end date format
            endDateInput.setError("Invalid date format. Please use MM/DD/YYYY.");
            return; // Exit if invalid
        }

        //Validate dates are after one another and valid
        if (updatedStartDate.isAfter(updatedEndDate)) {
            endDateInput.setError("End date must be after start date.");
            return; // Exit if invalid
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

            // Set notifications
            NotificationScheduler notificationScheduler = new NotificationScheduler(getApplicationContext());

            if(turnStartNotificationOn) {
                // Set start notifications on
                notificationScheduler.setAssessmentStartNotificationOn(existingAssessment);
            }else{
                // Turn start notifications off
                notificationScheduler.setAssessmentStartNotificationOff(existingAssessment);
            }

            if(turnEndNotificationOn) {
                // Set end notifications on
                notificationScheduler.setAssessmentEndNotificationOn(existingAssessment);
            }else{
                // Turn end notifications off
                notificationScheduler.setAssessmentEndNotificationOff(existingAssessment);
            }

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

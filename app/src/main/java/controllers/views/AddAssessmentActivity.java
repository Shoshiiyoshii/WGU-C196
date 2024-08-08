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

import androidx.activity.EdgeToEdge;
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
import model.StudentDatabase;
import model.entities.Assessment;


public class AddAssessmentActivity extends AppCompatActivity {
    private EditText assessmentNameInput;
    private String type;

    private EditText startDateInput;
    private SwitchCompat assessmentStartNotificationSwitch;
    private boolean turnStartNotificationOn;

    private EditText endDateInput;
    private SwitchCompat assessmentEndNotificationSwitch;
    private boolean turnEndNotificationOn;

    private StudentDatabase studentDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_assessment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.assessment_add);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        studentDatabase = StudentDatabase.getInstance(getApplicationContext());

        // Find UI elements
        assessmentNameInput = findViewById(R.id.assessmentNameInput);

        setUpTypeSpinner();

        startDateInput = findViewById(R.id.assessmentStartDateInput);
        assessmentStartNotificationSwitch = findViewById(R.id.assessmentStartNotificationSwitch);
        setupStartNotificationSwitchListener();

        endDateInput = findViewById(R.id.assessmentEndDateInput);
        assessmentEndNotificationSwitch = findViewById(R.id.assessmentEndNotificationSwitch);
        setupEndNotificationSwitchListener();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the navigation menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection from the nav menu
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

    private void setUpTypeSpinner() {
        Spinner typeSpinner = findViewById(R.id.assessmentTypeSpinner);
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
        LocalDate startDate;
        try {
            startDate = LocalDate.parse(startDateString, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } catch (DateTimeParseException e) {
            // Handle invalid start date format
            startDateInput.setError("Invalid date format. Please use MM/DD/YYYY.");
            return; // if invalid, exit
        }

        // Validate and parse end date
        LocalDate endDate;
        try {
            endDate = LocalDate.parse(endDateString, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } catch (DateTimeParseException e) {
            // Handle invalid end date format
            endDateInput.setError("Invalid date format. Please use MM/DD/YYYY.");
            return; // if invalid, exit
        }

        //Validate dates are after one another and valid
        if (startDate.isAfter(endDate)) {
            endDateInput.setError("End date must be after start date.");
            return; // if invalid, exit
        }

        // Create a new Assessment object, courseId will be null until assigned in CourseEditActivity
        Assessment assessment = new Assessment(assessmentName, type, null, startDate, endDate);

        // Insert course into database and update associated assessments
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Insert assessment into database
            long assessmentId = studentDatabase.assessmentDAO().insert(assessment);

            assessment.setAssessmentId((int) assessmentId);

            // Set notifications
            NotificationScheduler notificationScheduler = new NotificationScheduler(getApplicationContext());

            if(turnStartNotificationOn) {
                // Set start notifications on
                notificationScheduler.setAssessmentStartNotificationOn(assessment);
            }else{
                // Turn start notifications off
                notificationScheduler.setAssessmentStartNotificationOff(assessment);
            }

            if(turnEndNotificationOn) {
                // Set end notifications on
                notificationScheduler.setAssessmentEndNotificationOn(assessment);
            }else{
                // Turn end notifications off
                notificationScheduler.setAssessmentEndNotificationOff(assessment);
            }

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
package controllers.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thomasmccue.c196pastudentapp.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import controllers.adapters.CheckBoxAssessmentRecyclerViewAdapter;
import controllers.helpers.NotificationScheduler;
import model.StudentDatabase;
import model.entities.Assessment;
import model.entities.Course;

public class AddCourseActivity extends AppCompatActivity {
    private EditText courseNameInput;

    private EditText courseStartDateInput;
    private SwitchCompat courseStartNotificationSwitch;
    private boolean turnStartNotificationOn;

    private EditText courseEndDateInput;
    private SwitchCompat courseEndNotificationSwitch;
    private boolean turnEndNotificationOn;
    
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

        // Set up Action Bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.course_add);

        // Allow for insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get app's database instance
        studentDatabase = StudentDatabase.getInstance(getApplicationContext());

        // Find UI elements
        courseNameInput = findViewById(R.id.courseNameInput);

        courseStartDateInput = findViewById(R.id.courseStartDateInput);
        courseStartNotificationSwitch = findViewById(R.id.courseStartNotificationSwitch);
        setupStartNotificationSwitchListener();

        courseEndDateInput = findViewById(R.id.courseEndDateInput);
        courseEndNotificationSwitch = findViewById(R.id.courseEndNotificationSwitch);
        setupEndNotificationSwitchListener();

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
        // Handle item selection in nav menu
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

    private void setupStartNotificationSwitchListener() {
        // Set initial state
        courseStartNotificationSwitch.setChecked(false);

        // Set a listener to detect changes
        courseStartNotificationSwitch.setOnCheckedChangeListener((buttonView, notificationsOn) -> {
            // Switch is on or switch is off
            turnStartNotificationOn = notificationsOn;
        });
    }

    private void setupEndNotificationSwitchListener() {
        // Set initial state
        courseEndNotificationSwitch.setChecked(false);

        // Set a listener to detect changes
        courseEndNotificationSwitch.setOnCheckedChangeListener((buttonView, notificationsOn) -> {
            // Switch is on or switch is off
            turnEndNotificationOn = notificationsOn;
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
        LocalDate startDate;
        try {
            startDate = LocalDate.parse(startDateString, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } catch (DateTimeParseException e) {
            // Handle invalid start date format
            courseStartDateInput.setError("Invalid date format. Please use MM/DD/YYYY.");
            return; // if invalid, exit
        }

        // Validate and parse end date
        LocalDate endDate;
        try {
            endDate = LocalDate.parse(endDateString, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } catch (DateTimeParseException e) {
            // Handle invalid end date format
            courseEndDateInput.setError("Invalid date format. Please use MM/DD/YYYY.");
            return; // if invalid, exit
        }

        //Validate dates are after one another and valid
        if (startDate.isAfter(endDate)) {
            courseEndDateInput.setError("End date must be after start date.");
            return; // if invalid, exit
        }

        String instructorName = instructorNameInput.getText().toString();

        String instructorEmail = instructorEmailInput.getText().toString();
        // Validate email format
        if (!Patterns.EMAIL_ADDRESS.matcher(instructorEmail).matches()) {
            instructorEmailInput.setError("Invalid email format.");
            return; // if invalid, exit
        }

        String instructorPhone = instructorPhoneInput.getText().toString();
        // Validate phone number format
        if (!instructorPhone.matches("\\(\\d{3}\\)\\d{3}-\\d{4}")) {
            instructorPhoneInput.setError("Invalid phone number format. Please use (xxx)xxx-xxxx.");
            return; // if invalid, exit
        }

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

            course.setCourseId((int) courseIdLong);

            // Update associated assessments with the new course ID
            Set<Assessment> selectedAssessments = checkListAssessmentAdapter.getSelectedAssessments();
            for (Assessment assessment : selectedAssessments) {
                assessment.setCourseId(course.getCourseId());
                studentDatabase.assessmentDAO().update(assessment);
            }

            // Set notifications
            NotificationScheduler notificationScheduler = new NotificationScheduler(getApplicationContext());

            if(turnStartNotificationOn) {
                // Set start notifications on
                notificationScheduler.setCourseStartNotificationOn(course);
            }else{
                // Turn start notifications off
                notificationScheduler.setCourseStartNotificationOff(course);
            }

            if(turnEndNotificationOn) {
                // Set end notifications on
                notificationScheduler.setCourseEndNotificationOn(course);
            }else{
                // Turn end notifications off
                notificationScheduler.setCourseEndNotificationOff(course);
            }

            // Update UI on the main thread
            runOnUiThread(() -> {
                Toast.makeText(this, "Course added successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddCourseActivity.this, CourseActivity.class));
                finish();
            });
            executor.shutdown();
        });
    }

    public void courseDiscardButtonClicked(View view) {
        Toast.makeText(this, "New Course Discarded", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(AddCourseActivity.this, CourseActivity.class));
    }
}

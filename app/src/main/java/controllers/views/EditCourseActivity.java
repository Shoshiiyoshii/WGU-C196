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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import controllers.adapters.CheckBoxAssessmentRecyclerViewAdapter;
import controllers.helpers.NotificationScheduler;
import model.DAO.AssessmentDAO;
import model.DAO.CourseDAO;
import model.StudentDatabase;
import model.entities.Assessment;
import model.entities.Course;

public class EditCourseActivity extends AppCompatActivity {
    private int courseId;

    private EditText courseNameInput;
    private String courseName;

    private EditText courseStartDateInput;
    private long startDateLong;

    private EditText courseEndDateInput;
    private long endDateLong;

    private Spinner courseStatusSpinner;
    private String status;
    private String updatedStatus;

    private EditText instructorNameInput;
    private String instructorName;

    private EditText instructorPhoneInput;
    private String instructorPhone;

    private EditText instructorEmailInput;
    private String instructorEmail;

    private CheckBoxAssessmentRecyclerViewAdapter checkListAssessmentAdapter;
    private Set<Integer> associatedAssessmentIds;
    private List<Assessment> allAssessments;

    private EditText courseNotesInput;
    private String courseNote;

    private CourseDAO courseDAO;
    private AssessmentDAO assessmentDAO;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_course);

        //set up Action Bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.course_edit);

        // Allow for insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        courseNameInput = findViewById(R.id.courseNameInput);
        courseStartDateInput = findViewById(R.id.courseStartDateInput);
        courseEndDateInput = findViewById(R.id.courseEndDateInput);
        courseStatusSpinner = findViewById(R.id.courseStatusSpinner);
        instructorNameInput = findViewById(R.id.instructorNameInput);
        instructorPhoneInput = findViewById(R.id.instructorPhoneInput);
        instructorEmailInput = findViewById(R.id.instructorEmailInput);
        courseNotesInput = findViewById(R.id.courseNotesInput);
        RecyclerView assessmentRecyclerView = findViewById(R.id.recyclerViewAssessments);

        // Get app's database instance
        StudentDatabase studentDatabase = StudentDatabase.getInstance(getApplicationContext());
        courseDAO = studentDatabase.courseDAO();
        assessmentDAO = studentDatabase.assessmentDAO();

        // Retrieve course details from intent
        Intent intent = getIntent();
        courseId = intent.getIntExtra("COURSE_ID", -1);
        courseName = intent.getStringExtra("COURSE_NAME");
        startDateLong = intent.getLongExtra("COURSE_START_DATE", -1);
        endDateLong = intent.getLongExtra("COURSE_END_DATE", -1);
        status = intent.getStringExtra("COURSE_STATUS");
        instructorName = intent.getStringExtra("COURSE_INSTRUCTOR_NAME");
        instructorPhone = intent.getStringExtra("COURSE_INSTRUCTOR_PHONE");
        instructorEmail = intent.getStringExtra("COURSE_INSTRUCTOR_EMAIL");
        courseNote = intent.getStringExtra("COURSE_NOTE");


        // Set up the assessment recycler view
        ArrayList<Integer> associatedAssessmentIdsList = intent.getIntegerArrayListExtra("ASSOCIATED_ASSESSMENT_IDS");
        associatedAssessmentIds = new HashSet<>(associatedAssessmentIdsList);

        checkListAssessmentAdapter = new CheckBoxAssessmentRecyclerViewAdapter();
        assessmentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        assessmentRecyclerView.setAdapter(checkListAssessmentAdapter);

        // Load all courses and update the adapter
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            allAssessments = assessmentDAO.getAllAssessments();
            runOnUiThread(() -> checkListAssessmentAdapter.setSelectedAssessments(allAssessments, associatedAssessmentIds));
        });
        executor.shutdown();

        populateViews();

        statusSpinnerSetUp();
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
            startActivity(new Intent(EditCourseActivity.this, MainActivity.class));
            return true;
        } else if (item.getItemId() == R.id.nav_terms) {
            startActivity(new Intent(EditCourseActivity.this, TermActivity.class));
            return true;
        } else if (item.getItemId() == R.id.nav_courses) {
            startActivity(new Intent(EditCourseActivity.this, CourseActivity.class));
            return true;
        } else if (item.getItemId() == R.id.nav_assessments) {
            startActivity(new Intent(EditCourseActivity.this, AssessmentActivity.class));
            return true;
        } else {
            return false;
        }
    }

    private void statusSpinnerSetUp() {
        // Set up course status drop down using ArrayAdapter, and set to show status from selected course
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.course_status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseStatusSpinner.setAdapter(adapter);
        if (status != null) {
            int position = adapter.getPosition(status);
            if (position >= 0) {
                courseStatusSpinner.setSelection(position);
            }
        }
        //set listener for status spinner
        courseStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updatedStatus = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updatedStatus = null;
            }
        });
    }

    private void populateViews() {
        //convert to local dates
        LocalDate courseStart = LocalDate.ofEpochDay(startDateLong);
        LocalDate courseEnd = LocalDate.ofEpochDay(endDateLong);

        // Format dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String formattedStartDate = courseStart.format(formatter);
        String formattedEndDate = courseEnd.format(formatter);

        // Populate the text inputs
        courseNameInput.setText(courseName);
        courseStartDateInput.setText(formattedStartDate);
        courseEndDateInput.setText(formattedEndDate);
        instructorNameInput.setText(instructorName);
        instructorPhoneInput.setText(instructorPhone);
        instructorEmailInput.setText(instructorEmail);
        courseNotesInput.setText(courseNote);
    }

    public void courseSaveButtonClicked(View view) {
        String updatedCourseName = courseNameInput.getText().toString();
        String updatedStartDateString = courseStartDateInput.getText().toString();
        String updatedEndDateString = courseEndDateInput.getText().toString();
        String updatedInstructorName = instructorNameInput.getText().toString();
        String updatedInstructorPhone = instructorPhoneInput.getText().toString();
        String updatedInstructorEmail = instructorEmailInput.getText().toString();
        String updatedCourseNote = courseNotesInput.getText().toString();

        LocalDate updatedStartDate;
        try {
            updatedStartDate = LocalDate.parse(updatedStartDateString, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } catch (DateTimeParseException e) {
            courseStartDateInput.setError("Invalid date format. Please use MM/DD/YYYY.");
            return;
        }

        LocalDate updatedEndDate;
        try {
            updatedEndDate = LocalDate.parse(updatedEndDateString, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } catch (DateTimeParseException e) {
            courseEndDateInput.setError("Invalid date format. Please use MM/DD/YYYY.");
            return;
        }

        if (updatedStartDate.isAfter(updatedEndDate)) {
            courseEndDateInput.setError("End date must be after start date.");
            return;
        }

        // Lambda wants final variables
        LocalDate finalUpdatedStartDate = updatedStartDate;
        LocalDate finalUpdatedEndDate = updatedEndDate;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {

            Course existingCourse = courseDAO.getCourseById(courseId);

            existingCourse.setCourseName(updatedCourseName);
            existingCourse.setStartDate(finalUpdatedStartDate);
            existingCourse.setEndDate(finalUpdatedEndDate);
            existingCourse.setStatus(updatedStatus);
            existingCourse.setInstructorName(updatedInstructorName);
            existingCourse.setInstructorPhone(updatedInstructorPhone);
            existingCourse.setInstructorEmail(updatedInstructorEmail);
            existingCourse.setCourseNote(updatedCourseNote);

            courseDAO.update(existingCourse);

            // Set notifications
            NotificationScheduler notificationScheduler = new NotificationScheduler(getApplicationContext());
            notificationScheduler.setCourseNotifications(existingCourse);

            // Update associated assessments with the new course ID
            Set<Assessment> selectedAssessments = checkListAssessmentAdapter.getSelectedAssessments();
            Set<Integer> selectedAssessmentIds = new HashSet<>();
            for (Assessment assessment : selectedAssessments) {
                selectedAssessmentIds.add(assessment.getAssessmentId());
            }

            // Remove courseID from assessments that were unchecked
            for (int assessmentId : associatedAssessmentIds) {
                if (!selectedAssessmentIds.contains(assessmentId)) {
                    Assessment assessment = assessmentDAO.getAssessmentById(assessmentId);
                    assessment.setCourseId(null);
                    assessmentDAO.update(assessment);
                }
            }

            // Add courseId to assessments that were newly checked
            for (Assessment assessment : selectedAssessments) {
                if (!associatedAssessmentIds.contains(assessment.getAssessmentId())) {
                    assessment.setCourseId(courseId);
                    assessmentDAO.update(assessment);
                }
            }

            // Notify user and return to course activity on UI thread
            runOnUiThread(() -> {
                Toast.makeText(this, "Course Updated Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditCourseActivity.this, CourseActivity.class);
                intent.putExtra("COURSE_ID", courseId);
                startActivity(intent);
                finish();
            });
        });
        executor.shutdown();
    }

    public void courseDiscardButtonClicked(View view) {
        Toast.makeText(this, "Course Updates Discarded", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(EditCourseActivity.this, CourseActivity.class));
    }
}

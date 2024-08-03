package controllers.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import controllers.adapters.ListAssessmentRecyclerViewAdapter;
import controllers.helpers.CourseDetails;
import controllers.helpers.GetCourseDetails;
import model.DAO.AssessmentDAO;
import model.DAO.CourseDAO;
import model.StudentDatabase;
import model.entities.Assessment;

public class CourseDetailsActivity extends AppCompatActivity {
    private TextView courseTitle;
    private TextView courseStartDate;
    private TextView courseEndDate;

    private TextView courseStatus;

    private TextView instructorName;
    private TextView instructorPhone;
    private TextView instructorEmail;

    private TextView emptyView;
    private RecyclerView assessmentRecyclerView;

    private TextView courseNote;

    private CourseDAO courseDAO;
    private AssessmentDAO assessmentDAO;
    private ListAssessmentRecyclerViewAdapter assessmentAdapter;
    private CourseDetails courseDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_course_details);

        //set up Action Bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_course_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        StudentDatabase studentDatabase = StudentDatabase.getInstance(getApplicationContext());
        courseDAO = studentDatabase.courseDAO();
        assessmentDAO = studentDatabase.assessmentDAO();

        //find UI elements
        courseTitle = findViewById(R.id.courseTitle);

        courseStartDate = findViewById(R.id.courseStartDate);
        courseEndDate = findViewById(R.id.courseEndDate);

        courseStatus = findViewById(R.id.courseStatus);

        instructorName = findViewById(R.id.instructorName);
        instructorPhone = findViewById(R.id.instructorPhone);
        instructorEmail = findViewById(R.id.instructorEmail);

        emptyView = findViewById(R.id.noAssessmentsText);
        assessmentRecyclerView = findViewById(R.id.recyclerViewAssessments);

        //set up recycler view for assessments and adapter
        assessmentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        assessmentAdapter = new ListAssessmentRecyclerViewAdapter();
        assessmentRecyclerView.setAdapter(assessmentAdapter);

        courseNote = findViewById(R.id.courseNote);

        // Retrieve the course ID from the intent
        int courseId = getIntent().getIntExtra("COURSE_ID", -1);

        if (courseId != -1) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<CourseDetails> future = executor.submit(new GetCourseDetails(courseDAO, courseId, assessmentDAO));
            try {
                // This will block until the task completes and returns the result
                CourseDetails courseDetails = future.get();
                // Update the UI on the main thread
                runOnUiThread(() -> displayCourseDetails(courseDetails));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } finally {
                executor.shutdown();
            }
        }
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
            startActivity(new Intent(CourseDetailsActivity.this, MainActivity.class));
            return true;
        } else if (item.getItemId() == R.id.nav_terms) {
            startActivity(new Intent(CourseDetailsActivity.this, TermActivity.class));
            return true;
        } else if (item.getItemId() == R.id.nav_courses) {
            startActivity(new Intent(CourseDetailsActivity.this, CourseActivity.class));
            return true;
        } else if (item.getItemId() == R.id.nav_assessments) {
            startActivity(new Intent(CourseDetailsActivity.this, AssessmentActivity.class));
            return true;
        } else {
            return false;
        }
    }

    public void displayCourseDetails(CourseDetails courseDetails){
        this.courseDetails = courseDetails;
        assessmentAdapter.setAssessments(courseDetails.getAssessments());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        LocalDate startDate = courseDetails.getCourse().getStartDate();
        String startString = startDate.format(formatter);

        LocalDate endDate = courseDetails.getCourse().getEndDate();
        String endString = endDate.format(formatter);

        courseTitle.setText(courseDetails.getCourse().getCourseName());
        courseStartDate.setText(startString);
        courseEndDate.setText(endString);

        courseStatus.setText(courseDetails.getCourse().getStatus());

        instructorName.setText(courseDetails.getCourse().getInstructorName());
        instructorEmail.setText(courseDetails.getCourse().getInstructorEmail());
        instructorPhone.setText(courseDetails.getCourse().getInstructorPhone());

        if (courseDetails.getAssessments().isEmpty()) {
            assessmentRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            assessmentRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        courseNote.setText(courseDetails.getCourse().getCourseNote());
    }

    public void shareNoteButtonClicked(View view) {
        String courseNote = courseDetails.getCourse().getCourseNote();
        EditText shareInput = findViewById(R.id.shareInput);
        String phoneNumber = shareInput.getText().toString();

        // Check for empty input
        if (phoneNumber.isEmpty()) {
            shareInput.setError("Please enter a phone number to share.");
            return;
        }

        // Remove all non-digit characters to normalize the phone number
        String validPhoneNumber = phoneNumber.replaceAll("\\D", "");

        // Check if the normalized phone number has an invalid length
        if (validPhoneNumber.length() != 10) {
            shareInput.setError("Invalid phone number");
            return;
        }

        // Clear error if the input is valid
        shareInput.setError(null);

        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setData(Uri.parse("smsto:" + validPhoneNumber));
        smsIntent.putExtra("sms_body", courseNote);

        if (smsIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(smsIntent);
        } else {
            Toast.makeText(this, "No SMS app found", Toast.LENGTH_SHORT).show();
        }
    }


    public void courseEditButtonClicked(View view){
        Intent intent = new Intent(CourseDetailsActivity.this, EditCourseActivity.class);
        intent.putExtra("COURSE_ID", courseDetails.getCourse().getCourseId());
        intent.putExtra("COURSE_NAME", courseDetails.getCourse().getCourseName());
        intent.putExtra("COURSE_START_DATE", courseDetails.getCourse().getStartDate().toEpochDay());
        intent.putExtra("COURSE_END_DATE", courseDetails.getCourse().getEndDate().toEpochDay());
        intent.putExtra("COURSE_STATUS", courseDetails.getCourse().getStatus());
        intent.putExtra("COURSE_INSTRUCTOR_NAME", courseDetails.getCourse().getInstructorName());
        intent.putExtra("COURSE_INSTRUCTOR_PHONE", courseDetails.getCourse().getInstructorPhone());
        intent.putExtra("COURSE_INSTRUCTOR_EMAIL", courseDetails.getCourse().getInstructorEmail());

        ArrayList<Integer> associatedAssessmentIds = new ArrayList<>();
        for (Assessment assessment : courseDetails.getAssessments()) {
            associatedAssessmentIds.add(assessment.getAssessmentId());
        }
        intent.putIntegerArrayListExtra("ASSOCIATED_ASSESSMENT_IDS", associatedAssessmentIds);

        intent.putExtra("COURSE_NOTE", courseDetails.getCourse().getCourseNote());

        startActivity(intent);
    }

    public void courseDeleteButtonClicked(View view) {
        // Execute database operations on a background thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Check if there are any associated assessments
            List<Assessment> assessments = assessmentDAO.getAssessmentsForCourse(courseDetails.getCourse().getCourseId());
            if (assessments.isEmpty()) {
                // No associated assessments, delete the course
                courseDAO.delete(courseDetails.getCourse());
                runOnUiThread(() -> {
                    Toast.makeText(this, "Course Deleted Successfully", Toast.LENGTH_SHORT).show();
                    // Navigate back to CourseActivity on the main thread
                    startActivity(new Intent(CourseDetailsActivity.this, CourseActivity.class));
                    finish();
                });
            } else {
                // There are associated assessments, show error message
                runOnUiThread(() -> Toast.makeText(this, "Delete Failed. Please remove all " +
                        "associated assessments before deleting this course",
                        Toast.LENGTH_LONG).show());
            }
            executor.shutdown();
        });
    }
}
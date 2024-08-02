package controllers.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.thomasmccue.c196pastudentapp.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import controllers.executors.CourseDetails;
import controllers.executors.GetCourseDetails;
import model.DAO.AssessmentDAO;
import model.DAO.CourseDAO;
import model.StudentDatabase;
import model.entities.Assessment;
import model.entities.Course;

public class AssessmentDetailsActivity extends AppCompatActivity {
    private TextView assessmentName;
    private TextView assessmentType;
    private TextView startDateView;
    private TextView endDateView;

    private StudentDatabase studentDatabase;
    private AssessmentDAO assessmentDAO;
    private Assessment assessment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_assessment_details);

        //set up Action Bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_assessment_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        studentDatabase = StudentDatabase.getInstance(getApplicationContext());
        assessmentDAO = studentDatabase.assessmentDAO();


        // Find UI elements
        assessmentName = findViewById(R.id.assessmentTitle);
        assessmentType = findViewById(R.id.assessmentType);
        startDateView = findViewById(R.id.assessmentStartDate);
        endDateView = findViewById(R.id.assessmentEndDate);

        int assessmentId = getIntent().getIntExtra("ASSESSMENT_ID", -1);

        if (assessmentId != -1) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try {
                    // Fetch the assessment from the database
                    assessment = assessmentDAO.getAssessmentById(assessmentId);
                    // Update the UI on the main thread
                    runOnUiThread(() -> displayAssessmentDetails(assessment));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    executor.shutdown();
                }
            });
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
            startActivity(new Intent(AssessmentDetailsActivity.this, MainActivity.class));
            return true;
        } else if (item.getItemId() == R.id.nav_terms) {
            startActivity(new Intent(AssessmentDetailsActivity.this, TermActivity.class));
            return true;
        } else if (item.getItemId() == R.id.nav_courses) {
            startActivity(new Intent(AssessmentDetailsActivity.this, CourseActivity.class));
            return true;
        } else if (item.getItemId() == R.id.nav_assessments) {
            startActivity(new Intent(AssessmentDetailsActivity.this, AssessmentActivity.class));
            return true;
        } else {
            return false;
        }
    }

    private void displayAssessmentDetails(Assessment assessment) {
        if (assessment != null) {
            assessmentName.setText(assessment.getAssessmentName());
            assessmentType.setText(assessment.getAssessmentType());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

            LocalDate startDate = assessment.getAssessmentStartDate();
            String startString = startDate.format(formatter);
            startDateView.setText(startString);


            LocalDate endDate = assessment.getAssessmentDueDate();
            String endString = endDate.format(formatter);
            endDateView.setText(endString);
        }
    }

    public void assessmentEditButtonClicked(View view){
        Intent intent = new Intent(AssessmentDetailsActivity.this, EditAssessmentActivity.class);
        intent.putExtra("ASSESSMENT_ID", assessment.getAssessmentId());
        intent.putExtra("ASSESSMENT_TITLE", assessment.getAssessmentName());
        intent.putExtra("ASSESSMENT_TYPE", assessment.getAssessmentType());
        intent.putExtra("ASSESSMENT_START_DATE", assessment.getAssessmentStartDate().toEpochDay());
        intent.putExtra("ASSESSMENT_END_DATE", assessment.getAssessmentDueDate().toEpochDay());

        startActivity(intent);
    }

    public void assessmentDeleteButtonClicked(View view){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                assessmentDAO.delete(assessment);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Assessment Deleted Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AssessmentDetailsActivity.this, AssessmentActivity.class));
                    finish();
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                executor.shutdown();
            }
        });
    }
}

package controllers.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

import model.DAO.CourseDAO;
import model.DAO.TermDAO;
import model.StudentDatabase;
import model.entities.Course;
import model.entities.Term;
import controllers.adapters.CheckBoxCourseRecyclerViewAdapter;

public class EditTermActivity extends AppCompatActivity {
    private EditText termTitleInput;
    private EditText termStartDateInput;
    private EditText termEndDateInput;
    private RecyclerView courseRecyclerView;

    private TermDAO termDAO;
    private CourseDAO courseDAO;
    private int termId;
    private CheckBoxCourseRecyclerViewAdapter courseAdapter;

    private List<Course> allCourses;
    private Set<Integer> associatedCourseIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_term);

        // Set up Action Bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.terms_edit_title);

        // Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        termTitleInput = findViewById(R.id.termTitleInput);
        termStartDateInput = findViewById(R.id.termStartDateInput);
        termEndDateInput = findViewById(R.id.termEndDateInput);
        courseRecyclerView = findViewById(R.id.recyclerViewCourses);

        StudentDatabase studentDatabase = StudentDatabase.getInstance(getApplicationContext());
        termDAO = studentDatabase.termDAO();
        courseDAO = studentDatabase.courseDAO();

        // Retrieve term details and associated course IDs from intent
        Intent intent = getIntent();
        termId = intent.getIntExtra("TERM_ID", -1);
        String termTitle = intent.getStringExtra("TERM_TITLE");
        long termStartDate = intent.getLongExtra("TERM_START_DATE",-1);
        long termEndDate = intent.getLongExtra("TERM_END_DATE",-1);
        ArrayList<Integer> associatedCourseIdsList = intent.getIntegerArrayListExtra("ASSOCIATED_COURSE_IDS");

        // Populate the text inputs
        populateTextInputs(termTitle, termStartDate, termEndDate);

        setUpRecyclerView(associatedCourseIdsList);
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
            startActivity(new Intent(EditTermActivity.this, MainActivity.class));
            return true;
        } else if (item.getItemId() == R.id.nav_terms) {
            startActivity(new Intent(EditTermActivity.this, TermActivity.class));
            return true;
        } else if (item.getItemId() == R.id.nav_courses) {
            startActivity(new Intent(EditTermActivity.this, CourseActivity.class));
            return true;
        } else if (item.getItemId() == R.id.nav_assessments) {
            startActivity(new Intent(EditTermActivity.this, AssessmentActivity.class));
            return true;
        } else {
            return false;
        }
    }

    private void setUpRecyclerView(ArrayList<Integer> associatedCourseIdsList) {
        // Set up the course recycler view
        associatedCourseIds = new HashSet<>(associatedCourseIdsList);

        courseAdapter = new CheckBoxCourseRecyclerViewAdapter();
        courseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        courseRecyclerView.setAdapter(courseAdapter);

        // Load all courses and update the adapter
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            allCourses = courseDAO.getAllCourses();
            runOnUiThread(() -> courseAdapter.setSelectedCourses(allCourses, associatedCourseIds));
        });
        executor.shutdown();
    }

    private void populateTextInputs(String termTitle, long termStartDate, long termEndDate) {
        //convert to local dates
        LocalDate termStart = LocalDate.ofEpochDay(termStartDate);
        LocalDate termEnd = LocalDate.ofEpochDay(termEndDate);

        // Format dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String formattedStartDate = termStart.format(formatter);
        String formattedEndDate = termEnd.format(formatter);

        // Populate the text inputs
        termTitleInput.setText(termTitle);
        termStartDateInput.setText(formattedStartDate);
        termEndDateInput.setText(formattedEndDate);
    }

    public void termSaveButtonClicked(View view) {
        String updatedName = termTitleInput.getText().toString();
        String startDateString = termStartDateInput.getText().toString();
        String endDateString = termEndDateInput.getText().toString();

        // Validate and parse start date
        LocalDate updatedStartDate;
        try {
            updatedStartDate = LocalDate.parse(startDateString, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } catch (DateTimeParseException e) {
            // Handle invalid start date format
            termStartDateInput.setError("Invalid date format. Please use MM/DD/YYYY.");
            return; // Exit if invalid
        }

        // Validate and parse end date
        LocalDate updatedEndDate;
        try {
            updatedEndDate = LocalDate.parse(endDateString, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } catch (DateTimeParseException e) {
            // Handle invalid end date format
            termEndDateInput.setError("Invalid date format. Please use MM/DD/YYYY.");
            return; // Exit if invalid
        }

        // Validate dates are after one another and valid
        if (updatedStartDate.isAfter(updatedEndDate)) {
            termEndDateInput.setError("End date must be after start date.");
            return; // Exit if invalid
        }

        // Lambda wants final variables
        LocalDate finalUpdatedStartDate = updatedStartDate;
        LocalDate finalUpdatedEndDate = updatedEndDate;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Retrieve the term from the database
            Term existingTerm = termDAO.getTermById(termId);

            // Update the term with new values
            existingTerm.setTitle(updatedName);
            existingTerm.setStartDate(finalUpdatedStartDate);
            existingTerm.setEndDate(finalUpdatedEndDate);

            // Update the term in the database
            termDAO.update(existingTerm);

            // Update associated courses with the new term ID
            Set<Course> selectedCourses = courseAdapter.getSelectedCourses();
            Set<Integer> selectedCourseIds = new HashSet<>();
            for (Course course : selectedCourses) {
                selectedCourseIds.add(course.getCourseId());
            }

            // Remove termId from courses that were unchecked
            for (int courseId : associatedCourseIds) {
                if (!selectedCourseIds.contains(courseId)) {
                    Course course = courseDAO.getCourseById(courseId);
                    course.setTermId(null);
                    courseDAO.update(course);
                }
            }

            // Add termId to courses that were newly checked
            for (Course course : selectedCourses) {
                if (!associatedCourseIds.contains(course.getCourseId())) {
                    course.setTermId(termId);
                    courseDAO.update(course);
                }
            }
            // Update UI on the main thread
            runOnUiThread(() -> {
                Toast.makeText(this, "Term Updated Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditTermActivity.this, TermDetailsActivity.class);
                intent.putExtra("TERM_ID", termId);
                startActivity(intent);
                finish();
            });
        });
        executor.shutdown();
    }

    public void termDiscardButtonClicked(View view) {
        runOnUiThread(() -> {
            Toast.makeText(this, "Term Changes Discarded", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditTermActivity.this, TermDetailsActivity.class);
            intent.putExtra("TERM_ID", termId);
            startActivity(intent);
            finish();
        });
    }
}

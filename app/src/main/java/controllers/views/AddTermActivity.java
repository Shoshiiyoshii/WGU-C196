package controllers.views;

import android.content.Intent;
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
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import controllers.adapters.CheckBoxCourseRecyclerViewAdapter;

import model.StudentDatabase;
import model.entities.Course;
import model.entities.Term;

public class AddTermActivity extends AppCompatActivity {
    private EditText termTitleInput;
    private EditText termStartInput;
    private EditText termEndInput;

    private RecyclerView courseRecyclerView;
    private TextView emptyView;
    private StudentDatabase studentDatabase;
    private CheckBoxCourseRecyclerViewAdapter checkListCourseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_term);

        // Set up Action Bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.term_add);

        // Allow for insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find and set up recyclerView
        emptyView = findViewById(R.id.noCoursesText);
        courseRecyclerView = findViewById(R.id.recyclerViewCourses);
        courseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        checkListCourseAdapter = new CheckBoxCourseRecyclerViewAdapter();
        courseRecyclerView.setAdapter(checkListCourseAdapter);
        courseListSetUp();

        // Get apps database instance
        studentDatabase = StudentDatabase.getInstance(getApplicationContext());

        // Find text input and save button
        termTitleInput = findViewById(R.id.termTitleInput);
        termStartInput = findViewById(R.id.termStartDateInput);
        termEndInput = findViewById(R.id.termEndDateInput);
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
            startActivity(new Intent(AddTermActivity.this, MainActivity.class));
            return true;
        } else if (item.getItemId() == R.id.nav_terms) {
            startActivity(new Intent(AddTermActivity.this, TermActivity.class));
            return true;
        } else if (item.getItemId() == R.id.nav_courses) {
            startActivity(new Intent(AddTermActivity.this, CourseActivity.class));
            return true;
        } else if (item.getItemId() == R.id.nav_assessments) {
            startActivity(new Intent(AddTermActivity.this, AssessmentActivity.class));
            return true;
        } else {
            return false;
        }
    }

    public void termSaveButtonClicked(View view) {
        String name = termTitleInput.getText().toString();
        String startDateString = termStartInput.getText().toString();
        String endDateString = termEndInput.getText().toString();

        // Validate and parse start date
        LocalDate startDate;
        try {
            startDate = LocalDate.parse(startDateString, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } catch (DateTimeParseException e) {
            // Handle invalid start date format
            termStartInput.setError("Invalid date format. Please use MM/DD/YYYY.");
            return; // if date is invalid, exit click listener
        }

        // Validate and parse end date
        LocalDate endDate;
        try {
            endDate = LocalDate.parse(endDateString, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } catch (DateTimeParseException e) {
            // Handle invalid end date format
            termEndInput.setError("Invalid date format. Please use MM/DD/YYYY.");
            return; // if date is invalid, exit click listener
        }

        //Validate dates are after one another and valid
        if (startDate.isAfter(endDate)) {
            termEndInput.setError("End date must be after start date.");
            return; // if date is invalid, exit click listener
        }

        // Make term object
        Term term = new Term(name, startDate, endDate);

        // Insert term into database and update associated courses
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Insert term into database and get the generated term ID
            long termIdLong = studentDatabase.termDAO().insert(term);

            // Cast termIdLong to int
            int termId = (int) termIdLong;

            // Update associated courses with the new term ID
            Set<Course> selectedCourses = checkListCourseAdapter.getSelectedCourses();
            for (Course course : selectedCourses) {
                course.setTermId(termId);
                studentDatabase.courseDAO().update(course);
            }

            // Update UI on the main thread
            runOnUiThread(() -> {
                Toast.makeText(this, "Term Added Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddTermActivity.this, TermActivity.class));
                finish();
            });

            executor.shutdown();
        });
    }

    public void termDiscardButtonClicked(View view) {
        Toast.makeText(this, "New Term Discarded", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(AddTermActivity.this, TermActivity.class));
    }

    private void courseListSetUp() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Course> courses = studentDatabase.courseDAO().getAllCourses();
            runOnUiThread(() -> {
                if (courses.isEmpty()) {
                    courseRecyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    courseRecyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                    checkListCourseAdapter.setCourses(courses);
                }
            });

            executor.shutdown();
        });
    }
}

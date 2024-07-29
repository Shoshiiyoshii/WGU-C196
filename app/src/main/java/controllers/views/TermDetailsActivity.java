package controllers.views;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.thomasmccue.c196pastudentapp.R;

import java.lang.ref.WeakReference;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import controllers.adapters.ListCourseRecyclerViewAdapter;
import controllers.executors.GetTermDetails;
import controllers.executors.TermDetails;
import model.DAO.CourseDAO;
import model.StudentDatabase;
import model.entities.Course;
import model.entities.Term;
import model.DAO.TermDAO;

public class TermDetailsActivity extends AppCompatActivity {
    private TextView title;
    private TextView termStart;
    private TextView termEnd;
    private RecyclerView courseRecycler;
    private TextView emptyView;
    private TextView deleteError;

    private TermDAO termDAO;
    private CourseDAO courseDAO;
    private ListCourseRecyclerViewAdapter courseAdapter;
    private TermDetails termDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_term_details);

        StudentDatabase studentDatabase = StudentDatabase.getInstance(getApplicationContext());
        termDAO = studentDatabase.termDAO();
        courseDAO = studentDatabase.courseDAO();

        title = findViewById(R.id.termTitle);
        termStart = findViewById(R.id.termStartDate);
        termEnd = findViewById(R.id.termEndDate);
        courseRecycler = findViewById(R.id.recyclerViewCourses);
        emptyView = findViewById(R.id.noCoursesText);
        deleteError = findViewById(R.id.deleteError);

        courseAdapter = new ListCourseRecyclerViewAdapter();
        courseRecycler.setLayoutManager(new LinearLayoutManager(this));
        courseRecycler.setAdapter(courseAdapter);

        // Retrieve the term ID from the intent
        int termId = getIntent().getIntExtra("TERM_ID", -1);

        if (termId != -1) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<TermDetails> future = executor.submit(new GetTermDetails(termDAO, termId, courseDAO));
            try {
                // This will block until the task completes and returns the result
                TermDetails termDetails = future.get();
                // Update the UI on the main thread
                runOnUiThread(() -> displayTermDetails(termDetails));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } finally {
                executor.shutdown();
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_terms);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(TermDetailsActivity.this, MainActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_terms) {
                startActivity(new Intent(TermDetailsActivity.this, TermActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_courses) {
                startActivity(new Intent(TermDetailsActivity.this, CourseActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_assessments) {
                startActivity(new Intent(TermDetailsActivity.this, AssessmentActivity.class));
                return true;
            } else {
                return false;
            }
        });
    }

    public void termEditButtonClicked(View view) {

    }

    public void termDeleteButtonClicked(View view) {
        // Execute database operations on a background thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Check if there are any associated courses
            List<Course> courses = courseDAO.getCoursesForTerm(termDetails.getTerm().getTermId());
            if (courses.isEmpty()) {
                // No associated courses, delete the term
                termDAO.delete(termDetails.getTerm());
                runOnUiThread(() -> {
                    // Navigate back to TermActivity on the main thread
                    startActivity(new Intent(TermDetailsActivity.this, TermActivity.class));
                    finish();
                });
            } else {
                // There are associated courses, show error message
                runOnUiThread(() -> deleteError.setVisibility(View.VISIBLE));
            }
        });
    }

    public void displayTermDetails(TermDetails termDetails){
        this.termDetails = termDetails;
        courseAdapter.setCourses(termDetails.getCourses());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        LocalDate startDate = termDetails.getTerm().getStartDate();
        String startString = startDate.format(formatter);

        LocalDate endDate = termDetails.getTerm().getEndDate();
        String endString = endDate.format(formatter);

        title.setText(termDetails.getTerm().getTitle());
        termStart.setText(startString);
        termEnd.setText(endString);

        if (termDetails.getCourses().isEmpty()) {
            courseRecycler.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            courseRecycler.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}

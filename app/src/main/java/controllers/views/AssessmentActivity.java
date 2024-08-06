package controllers.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thomasmccue.c196pastudentapp.R;

import java.util.List;
import java.util.Objects;

import controllers.adapters.ListAssessmentRecyclerViewAdapter;
import model.StudentDatabase;
import model.entities.Assessment;

public class AssessmentActivity extends AppCompatActivity {
    private RecyclerView assessmentsRecyclerView;
    private ListAssessmentRecyclerViewAdapter assessmentsRecyclerViewAdapter;
    private StudentDatabase studentDatabase;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_assessment);

        //set up Action Bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_assessments);

        // Set up RecyclerView and Adapter
        assessmentsRecyclerView = findViewById(R.id.recyclerViewAssessments);
        assessmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        assessmentsRecyclerViewAdapter = new ListAssessmentRecyclerViewAdapter();
        assessmentsRecyclerView.setAdapter(assessmentsRecyclerViewAdapter);

        // Initialize emptyView message
        emptyView = findViewById(R.id.noAssessmentsText);

        // Get instance of the database
        studentDatabase = StudentDatabase.getInstance(getApplicationContext());
        // Load courses from database and update RecyclerView
        loadAssessments();

        // insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
        // Handle item selection from nav menu
        if (item.getItemId() == R.id.nav_home) {
            startActivity(new Intent(AssessmentActivity.this, MainActivity.class));
            return true;
        } else if (item.getItemId() == R.id.nav_terms) {
            startActivity(new Intent(AssessmentActivity.this, TermActivity.class));
            return true;
        } else if (item.getItemId() == R.id.nav_courses) {
            startActivity(new Intent(AssessmentActivity.this, CourseActivity.class));
            return true;
        } else if (item.getItemId() == R.id.nav_assessments) {
            startActivity(new Intent(AssessmentActivity.this, AssessmentActivity.class));
            return true;
        } else {
            return false;
        }
    }

    private void loadAssessments() {
        new Thread(() -> {
            List<Assessment> assessments = studentDatabase.assessmentDAO().getAllAssessments();
            runOnUiThread(() -> {
                if (assessments.isEmpty()) {
                    assessmentsRecyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    assessmentsRecyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                    assessmentsRecyclerViewAdapter.setAssessments(assessments);
                }
            });
        }).start();
    }

    public void assessmentAddButtonClicked(View view){
        startActivity(new Intent(AssessmentActivity.this, AddAssessmentActivity.class));
    }
}
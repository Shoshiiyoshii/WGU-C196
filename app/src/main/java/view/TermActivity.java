package view;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.thomasmccue.c196pastudentapp.R;

import java.util.List;

import controller.TermAdapter;
import model.StudentDatabase;
import model.entities.Term;

public class TermActivity extends AppCompatActivity {
    private RecyclerView termsRecyclerView;
    private TermAdapter termAdapter;
    private StudentDatabase studentDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_term);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        findViewById(R.id.termAddButton).setOnClickListener(view -> {
            startActivity(new Intent(TermActivity.this, AddTermActivity.class));
        });

        //populate the recyclerview with terms
        termsRecyclerView = findViewById(R.id.recyclerViewTerms);
        termsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        termAdapter = new TermAdapter();
        termsRecyclerView.setAdapter(termAdapter);

        studentDatabase = StudentDatabase.getInstance(this);

        // Load terms from database and update RecyclerView
        loadTerms();

    }

    private void loadTerms() {
        new Thread(() -> {
            List<Term> terms = studentDatabase.termDAO().getAllTerms();
            runOnUiThread(() -> {
                termAdapter.setTerms(terms);
            });
        }).start();

        //listeners for the bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_terms);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(TermActivity.this, MainActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_terms) {
                startActivity(new Intent(TermActivity.this, TermActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_courses) {
                startActivity(new Intent(TermActivity.this, CourseActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_assessments) {
                startActivity(new Intent(TermActivity.this, AssessmentActivity.class));
                return true;
            } else {
                return false;
            }
        });
    }
}
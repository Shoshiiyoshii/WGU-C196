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

import controllers.adapters.TermRecyclerViewAdapter;
import model.StudentDatabase;
import model.entities.Term;

public class TermActivity extends AppCompatActivity {
    private RecyclerView termsRecyclerView;
    private TermRecyclerViewAdapter termRecyclerViewAdapter;
    private StudentDatabase studentDatabase;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_term);

        //set up Action Bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_terms);

        //insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up RecyclerView and Adapter
        termsRecyclerView = findViewById(R.id.recyclerViewTerms);
        termsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        termRecyclerViewAdapter = new TermRecyclerViewAdapter();
        termsRecyclerView.setAdapter(termRecyclerViewAdapter);

        // Initialize emptyView message
        emptyView = findViewById(R.id.noTermsText);

        // Get instance of the database
        studentDatabase = StudentDatabase.getInstance(getApplicationContext());
        // Load terms from database and update RecyclerView
        loadTerms();
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
        // Handle item selection nav menu
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
    }

    public void termAddButtonClicked(View view){
        startActivity(new Intent(TermActivity.this, AddTermActivity.class));

    }

    private void loadTerms() {
        new Thread(() -> {
            List<Term> terms = studentDatabase.termDAO().getAllTerms();
            runOnUiThread(() -> {
                if (terms.isEmpty()) {
                    termsRecyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    termsRecyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                    termRecyclerViewAdapter.setTerms(terms);
                }
            });
        }).start();
    }
}

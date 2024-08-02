package controllers.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.thomasmccue.c196pastudentapp.R;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_name);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
            startActivity(new Intent(MainActivity.this, MainActivity.class));
            return true;
        } else if (item.getItemId() == R.id.nav_terms) {
            startActivity(new Intent(MainActivity.this, TermActivity.class));
            return true;
        } else if (item.getItemId() == R.id.nav_courses) {
            startActivity(new Intent(MainActivity.this, CourseActivity.class));
            return true;
        } else if (item.getItemId() == R.id.nav_assessments) {
            startActivity(new Intent(MainActivity.this, AssessmentActivity.class));
            return true;
        } else {
            return false;
        }
}

    public void mainGoToTermsButtonClicked(View view){
        startActivity(new Intent(MainActivity.this, TermActivity.class));
    }

    public void mainGoToCoursesButtonClicked(View view){
        startActivity(new Intent(MainActivity.this, CourseActivity.class));
    }

    public void mainGoToAssessmentsButtonClicked(View view){
        startActivity(new Intent(MainActivity.this, AssessmentActivity.class));
    }
}

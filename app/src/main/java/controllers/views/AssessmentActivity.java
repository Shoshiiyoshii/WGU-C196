package controllers.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.thomasmccue.c196pastudentapp.R;

public class AssessmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_assessment);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_assessments);

        bottomNavigationView.setOnItemSelectedListener(item -> {
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
        });

    }

    public void assessmentAddButtonClicked(View view){
        startActivity(new Intent(AssessmentActivity.this, AddAssessmentActivity.class));
    }
}
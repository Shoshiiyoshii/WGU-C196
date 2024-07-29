package controllers.adapters;

import android.content.Intent;
import android.view.View;

import model.entities.Assessment;
import controllers.views.AssessmentDetailsActivity;

public class ListAssessmentRecyclerViewAdapter extends BaseAssessmentRecyclerViewAdapter {
    @Override
    protected void bindAssessment(BaseAssessmentRecyclerViewAdapter.AssessmentViewHolder holder,
                                  Assessment assessment) {
        // Set the course name
        holder.textViewName.setText(assessment.getAssessmentName());

        // Set click listener to open CourseDetailsActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AssessmentDetailsActivity.class);
            int assessmentId = assessment.getAssessmentId();

            intent.putExtra("ASSESSMENT_ID", assessmentId);
            v.getContext().startActivity(intent);
        });
    }
}

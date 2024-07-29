package controllers.adapters;

import android.view.View;
import android.widget.CheckBox;

import com.thomasmccue.c196pastudentapp.R;

import java.util.HashSet;
import java.util.Set;

import model.entities.Assessment;

public class CheckBoxAssessmentRecyclerViewAdapter extends BaseAssessmentRecyclerViewAdapter{
    private final Set<Assessment> selectedAssessments = new HashSet<>();

    @Override
    protected void bindAssessment(AssessmentViewHolder holder, Assessment assessment) {
            CheckBox checkBox = holder.itemView.findViewById(R.id.checkBox);
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setChecked(selectedAssessments.contains(assessment));

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedAssessments.add(assessment);
                } else {
                    selectedAssessments.remove(assessment);
                }
            });
        }

        public Set<Assessment> getSelectedAssessments() {
            return selectedAssessments;
        }
}

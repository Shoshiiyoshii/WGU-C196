package controllers.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thomasmccue.c196pastudentapp.R;

import java.util.ArrayList;
import java.util.List;

import model.entities.Assessment;

public abstract class BaseAssessmentRecyclerViewAdapter extends RecyclerView.Adapter<BaseAssessmentRecyclerViewAdapter.AssessmentViewHolder> {
    protected List<Assessment> assessments = new ArrayList<>();

    @NonNull
    @Override
    public BaseAssessmentRecyclerViewAdapter.AssessmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout defined in list_item.xml
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new BaseAssessmentRecyclerViewAdapter.AssessmentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseAssessmentRecyclerViewAdapter.AssessmentViewHolder holder, int position) {
        // Bind the data to the TextView
        Assessment currentAssessment = assessments.get(position);
        holder.textViewName.setText(currentAssessment.getAssessmentName());
        bindAssessment(holder, currentAssessment);
    }

    protected abstract void bindAssessment(BaseAssessmentRecyclerViewAdapter.AssessmentViewHolder holder, Assessment assessment);

    @Override
    public int getItemCount() {
        return assessments.size();
    }

    // Method to update the list of Assessments
    public void setAssessments(List<Assessment> assessments) {
        this.assessments = assessments;
        notifyDataSetChanged();
    }

    // ViewHolder class that holds references to the views within each item in the RecyclerView
    public static class AssessmentViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName;

        public AssessmentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.itemName);
        }
    }
}

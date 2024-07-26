package controllers.adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thomasmccue.c196pastudentapp.R;

import java.util.ArrayList;
import java.util.List;

import controllers.views.TermDetailsActivity;
import model.entities.Term;

//a class to bind term data to the appropriate recycler.
public class TermRecyclerViewAdapter extends RecyclerView.Adapter<TermRecyclerViewAdapter.TermViewHolder> {
    private List<Term> terms = new ArrayList<>();

    //creates the layout in list_item for each item in the ArrayList or terms so that it can be displayed in
    //the terms recycle Controllers in the correct format
    @NonNull
    @Override
    public TermViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout defined in list_item.xml
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new TermViewHolder(itemView);
    }

    //sets each item in the recycler Controllers to show the title of the term that is bound to it
    @Override
    public void onBindViewHolder(@NonNull TermViewHolder holder, int position) {
        Term currentTerm = terms.get(position);
        holder.textViewName.setText(currentTerm.getTitle());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), TermDetailsActivity.class);
            int termId = currentTerm.getTermId();

            intent.putExtra("TERM_ID", termId);
            v.getContext().startActivity(intent);
        });
    }


    //gets how many terms are in the ArrayList
    @Override
    public int getItemCount() {
        return terms.size();
    }

    //resets the terms ArrayList so that it has the most current terms after a new term is added?
    public void setTerms(List<Term> terms) {
        this.terms = terms;
        notifyDataSetChanged();
    }

    // ViewHolder class that holds references to the views within each item in the RecyclerView
    public static class TermViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName;

        public TermViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.itemName);
        }
    }
}



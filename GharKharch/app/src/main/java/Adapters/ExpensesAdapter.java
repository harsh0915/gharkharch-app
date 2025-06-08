package Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.ViewHolder> {

    Context context;
    ArrayList<Integer> expensesList;

    public ExpensesAdapter(Context context, ArrayList<Integer> data) {
    }

    @NonNull
    @Override
    public ExpensesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ExpensesAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return expensesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

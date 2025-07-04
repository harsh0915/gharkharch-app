package Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itmart.gharkharch.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context context;
    private final List<String> category_list;
    private List<String> backup_list;
    private final CategoryItemClicked interface_CategoryItemCLicked;

    public interface CategoryItemClicked {
        public void on_item_clicked(String cat_name);
    }

    public CategoryAdapter(Context context, List<String> category_list, CategoryItemClicked interface_CategoryItemCLicked) {
        this.context = context;
        this.category_list = category_list;
        this.interface_CategoryItemCLicked = interface_CategoryItemCLicked;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        String cat_name = category_list.get(position);
        holder.category_name.setText(cat_name);
        holder.itemView.setOnClickListener(v -> {
            interface_CategoryItemCLicked.on_item_clicked(cat_name);
        });
    }

    @Override
    public int getItemCount() {
        return category_list.size();
    }

    public void update_list(List<String> updated_list) {
        category_list.clear();
        category_list.addAll(updated_list);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView category_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            category_name = itemView.findViewById(R.id.category_name);
        }
    }
}

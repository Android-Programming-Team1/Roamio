package com.team1.roamio.view.ui.list_view_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.team1.roamio.R;
import com.team1.roamio.view.ui.list_view_item.PlanDataListViewItem;

import java.util.List;

public class PlanDataListViewAdapter extends RecyclerView.Adapter<PlanDataListViewAdapter.ViewHolder> {

    private Context context;
    private List<PlanDataListViewItem> items;

    public PlanDataListViewAdapter(Context context, List<PlanDataListViewItem> items) {
        this.context = context;
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
        }

        public void bind(PlanDataListViewItem item) {
            tvTitle.setText(item.getTitle());
            tvSubtitle.setText(item.getSubtitle());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_plan_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlanDataListViewItem item = items.get(position);
        holder.bind(item);

        holder.itemView.setOnClickListener(v ->
                Toast.makeText(context, "클릭: " + item.getTitle(), Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
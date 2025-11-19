package com.team1.roamio.utility.stamp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.team1.roamio.R;
import com.team1.roamio.data.Stamp;

import java.util.List;

public class StampAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_STAMP = 1;
    private static final int VIEW_TYPE_ADD = 2;

    private List<Stamp> items;
    private final Context context;
    private final OnAddClickListener addClickListener;

    public interface OnAddClickListener {
        void onAddClick();
    }

    public StampAdapter(Context context, List<Stamp> items, OnAddClickListener listener) {
        this.context = context;
        this.items = items;
        this.addClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == items.size()) {
            return VIEW_TYPE_ADD;
        }
        return VIEW_TYPE_STAMP;
    }

    @Override
    public int getItemCount() {
        return items.size() + 1;  // 마지막은 +추가 버튼
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_STAMP) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_stamp, parent, false);
            return new StampViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_stamp, parent, false);
            return new AddButtonViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof StampViewHolder) {
            Stamp stamp = items.get(position);
            ((StampViewHolder) holder).bind(stamp);
        } else if (holder instanceof AddButtonViewHolder) {
            ((AddButtonViewHolder) holder).bind(addClickListener);
        }
    }

    public static class StampViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public StampViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.stamp_image);
        }

        public void bind(Stamp stamp) {
            img.setImageResource(stamp.getImageResId());
        }
    }

    public static class AddButtonViewHolder extends RecyclerView.ViewHolder {
        LinearLayout addButton;

        public AddButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            addButton = itemView.findViewById(R.id.add_button_layout);
        }

        public void bind(OnAddClickListener listener) {
            addButton.setOnClickListener(v -> listener.onAddClick());
        }
    }
}

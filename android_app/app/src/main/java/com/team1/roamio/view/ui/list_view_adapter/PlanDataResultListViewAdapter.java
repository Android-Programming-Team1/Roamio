package com.team1.roamio.view.ui.list_view_adapter;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.team1.roamio.R;
import com.team1.roamio.view.ui.holder.ChannelItemHolder;
import com.team1.roamio.view.ui.holder.PlaceItemHolder;

import java.util.List;

public class PlanDataResultListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_A = 0;
    private static final int TYPE_B = 1;

    private List<Pair<String, String>> items;

    public interface OnItemClickListener {
        void onItemClick(int position, Pair<String, String> item);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public PlanDataResultListViewAdapter(List<Pair<String, String>> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return (position % 2 == 0) ? TYPE_A : TYPE_B;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_A) {
            View view = inflater.inflate(R.layout.layout_resource_1, parent, false);
            return new PlaceItemHolder(view);
        } else {
            View view = inflater.inflate(R.layout.layout_resource_2, parent, false);
            return new ChannelItemHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecyclerView.ViewHolder holder, int position) {

        Pair<String, String> item = items.get(position);
        String text = item.first; // 텍스트

        if (holder instanceof PlaceItemHolder) {
            ((PlaceItemHolder) holder).textView.setText(text);

        }
        else if (holder instanceof ChannelItemHolder) {
            ((ChannelItemHolder) holder).textView.setText(text);
        }

        // 클릭 이벤트 추가!
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position, item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}


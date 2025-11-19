package com.team1.roamio.utility.planner;
//두 가지 뷰 타입을 처리하고 CRUD 이벤트를 전달
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_LOCATION = 0;
    private static final int TYPE_TRANSPORT = 1;

    private List<ScheduleItem> items;
    private OnItemClickListener listener;

    // 클릭 리스너 인터페이스 (CRUD용)
    public interface OnItemClickListener {
        void onItemClick(int position, ScheduleItem item);
    }

    public ScheduleAdapter(List<ScheduleItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if ("location".equals(items.get(position).getType())) {
            return TYPE_LOCATION;
        } else {
            return TYPE_TRANSPORT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_LOCATION) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule_location, parent, false);
            return new LocationViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule_transport, parent, false);
            return new TransportViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ScheduleItem item = items.get(position);

        // 클릭 시 수정/삭제 다이얼로그 등을 띄우기 위한 이벤트 연결
        holder.itemView.setOnClickListener(v -> listener.onItemClick(position, item));

        if (holder instanceof LocationViewHolder) {
            ((LocationViewHolder) holder).bind(item);
        } else if (holder instanceof TransportViewHolder) {
            ((TransportViewHolder) holder).bind(item);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // --- ViewHolders ---
    static class LocationViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        LocationViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_location_name);
        }

        void bind(ScheduleItem item) {
            tvName.setText(item.getDescription());
            // 여기서 item.description에 따라 아이콘을 변경하는 로직 추가 가능 (예: 공항, 호텔 등)
        }
    }

    static class TransportViewHolder extends RecyclerView.ViewHolder {
        TextView tvDesc;

        TransportViewHolder(View itemView) {
            super(itemView);
            tvDesc = itemView.findViewById(R.id.tv_transport_desc);
        }

        void bind(ScheduleItem item) {
            tvDesc.setText(item.getDescription());
        }
    }

    // --- CRUD Helper Methods ---
    public void addItem(ScheduleItem item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void updateItem(int position, String newDescription) {
        items.get(position).setDescription(newDescription);
        notifyItemChanged(position);
    }

    public void deleteItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }
}
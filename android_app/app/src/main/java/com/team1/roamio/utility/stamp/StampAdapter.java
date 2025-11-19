package com.team1.roamio.utility.stamp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.team1.roamio.R;
import com.team1.roamio.data.Stamp;

import java.util.List;

public class StampAdapter extends RecyclerView.Adapter<StampAdapter.StampViewHolder> {

    private final Context context;
    private List<Stamp> stampList;

    private OnAddClickListener addClickListener;

    public interface OnAddClickListener {
        void onAddClick();
    }

    public StampAdapter(Context context, List<Stamp> stampList) {
        this.context = context;
        this.stampList = stampList;
    }

    public void setOnAddClickListener(OnAddClickListener listener) {
        this.addClickListener = listener;
    }

    @NonNull
    @Override
    public StampViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_stamp, parent, false);
        return new StampViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StampViewHolder holder, int position) {
        Stamp stamp = stampList.get(position);

        holder.txtName.setText(stamp.getImageName());

        // Drawable 리소스 ID 찾기
        int resId = context.getResources().getIdentifier(
                stamp.getImageName(),
                "drawable",
                context.getPackageName()
        );

        if (resId != 0) {
            holder.stampImage.setImageResource(resId);
        } else {
            holder.stampImage.setImageResource(R.drawable.stamp_00);
        }

        holder.itemView.setOnClickListener(v -> {
            if (addClickListener != null) {
                addClickListener.onAddClick();
            }
        });
    }

    @Override
    public int getItemCount() {
        return stampList != null ? stampList.size() : 0;
    }

    public void updateList(List<Stamp> newList) {
        this.stampList = newList;
        notifyDataSetChanged();
    }

    static class StampViewHolder extends RecyclerView.ViewHolder {

        ImageView stampImage;
        TextView txtName;

        public StampViewHolder(@NonNull View itemView) {
            super(itemView);
            stampImage = itemView.findViewById(R.id.stamp_image);
            txtName = itemView.findViewById(R.id.text_country);
        }
    }
}

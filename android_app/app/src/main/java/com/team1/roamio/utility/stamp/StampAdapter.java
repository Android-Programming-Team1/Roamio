package com.team1.roamio.utility.stamp;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.res.Resources;

import androidx.core.content.ContextCompat;

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
        try {
            Drawable drawable = getResource(stamp.getImageName(), context);
            holder.stampImage.setImageDrawable(drawable);
        } catch (NameNotFoundException e) {
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

    private static Drawable getResource(String resName, Context context)
            throws NameNotFoundException {

        Context resContext = context.createPackageContext(context.getPackageName(), 0);
        Resources res = resContext.getResources();

        // "48" suffix 붙이기
        int id = res.getIdentifier(resName + "48", "drawable", context.getPackageName());

        if (id == 0) {
            // 기본 이미지 (존재하는 이미지 이름으로 변경 가능)
            return ContextCompat.getDrawable(context, R.drawable.stamp_00);
        } else {
            return ContextCompat.getDrawable(context, id);
        }
    }
}

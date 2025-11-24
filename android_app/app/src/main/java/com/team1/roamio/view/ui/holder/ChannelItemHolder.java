package com.team1.roamio.view.ui.holder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.team1.roamio.R;

public class ChannelItemHolder extends RecyclerView.ViewHolder {
    public TextView textView;

    public ChannelItemHolder(View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.tv_channel);
    }
}

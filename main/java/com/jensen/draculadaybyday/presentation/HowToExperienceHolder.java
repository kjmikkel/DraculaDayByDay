package com.jensen.draculadaybyday.presentation;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jensen.draculadaybyday.R;

public class HowToExperienceHolder extends RecyclerView.ViewHolder {

    final public TextView textView;
    final public ImageView imageView;

    public HowToExperienceHolder(View view) {
        super(view);

        textView = (TextView) view.findViewById(R.id.experience_bar_text);
        imageView = (ImageView) view.findViewById(R.id.experience_bar_image);
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public void setImageViewText(String text) {
        this.imageView.setContentDescription(text);
    }
}

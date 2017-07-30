package com.jensen.draculadaybyday.entries;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jensen.draculadaybyday.entry.Entry;
import com.jensen.draculadaybyday.R;
import com.jensen.draculadaybyday.presentation.SquareImageView;

public class EntryViewHolder extends RecyclerView.ViewHolder {
    public final View mView;

    private final Context context;

    // The header
    private final TextView mDate;
    private final TextView mName;
    private final SquareImageView mNewsType;

    // The content pane
//    public final TextView mContentView;

    public Entry entry;

    public EntryViewHolder(View view) {
        super(view);
        mView = view;

        this.context = view.getContext();

        // Header
        mDate = (TextView) view.findViewById(R.id.date);
        mName = (TextView) view.findViewById(R.id.name);
        mNewsType = (SquareImageView) view.findViewById(R.id.news_type);

      //  setIsRecyclable(false);

        // Content
      //  mContentView = (TextView) view.findViewById(R.id.content);
    }

    public void setViews() {
        try {
            // Header
            mDate.setText(entry.getDateString());
            mName.setText(entry.getPerson());

            Drawable drawable;
            String imageDescription;

            mNewsType.setScaleType(ImageView.ScaleType.FIT_XY);

            switch (entry.getType()) {
                case DIARY_ENTRY:
                    drawable = context.getDrawable(R.drawable.ic_diary_icon);
                    imageDescription = context.getString(R.string.news_type_diary);
                    break;
                case LETTER:
                    drawable = context.getDrawable(R.drawable.ic_envelope_icon);
                    imageDescription = context.getString(R.string.news_type_letter);
                    break;
                case NEWSPAPER:
                    drawable = context.getDrawable(R.drawable.ic_newspaper_icon);
                    imageDescription = context.getString(R.string.news_type_newspaper);
                    break;
                case NOTE:
                    drawable = context.getDrawable(R.drawable.ic_note_icon);
                    imageDescription = context.getString(R.string.news_type_note);
                    break;
                case PHONOGRAPH:
                    drawable = context.getDrawable(R.drawable.ic_phonograph_icon);
                    imageDescription = context.getString(R.string.news_type_phonograph);
                    break;
                case TELEGRAM:
                default:
                    drawable = context.getDrawable(R.drawable.ic_telegram_icon);
                    imageDescription = context.getString(R.string.news_type_telegram);
                    break;
            }

            if (drawable != null) {
                mNewsType.setBackground(drawable);
                mNewsType.setContentDescription(imageDescription);
            }
        } catch (Exception e) {
            Log.d("setViews", e.getMessage());
        }
    }

    @Override
    public String toString() {
        return super.toString() + " '";
                //+ mContentView.getText() + "'";
    }
}

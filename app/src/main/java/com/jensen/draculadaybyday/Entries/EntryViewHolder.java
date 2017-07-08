package com.jensen.draculadaybyday.Entries;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jensen.draculadaybyday.Entry.Entry;
import com.jensen.draculadaybyday.R;

public class EntryViewHolder extends RecyclerView.ViewHolder {
    public final View mView;

    private final Context context;

    // The header
    public final TextView mDate;
    public final TextView mName;
    public final ImageView mNewsType;

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
        mNewsType = (ImageView) view.findViewById(R.id.news_type);

        // Content
      //  mContentView = (TextView) view.findViewById(R.id.content);
    }

    public void setViews() {
        // Header
        mDate.setText("\n\n" + entry.getDateString());
        mName.setText("\n\n" + entry.getPerson());

        mNewsType.getLayoutParams().height = mNewsType.getWidth();
        mNewsType.requestLayout();

        Drawable drawable = null;
        switch (entry.getType()) {
            case DIARY_ENTRY:
                drawable = context.getDrawable(R.drawable.ic_diary);
                break;
            case LETTER:
                drawable = context.getDrawable(R.drawable.ic_envelope);
                break;
            case NEWSPAPER:
                drawable = context.getDrawable(R.mipmap.ic_newspaper_icon);
                break;
            case NOTE:
                drawable = context.getDrawable(R.mipmap.ic_note_icon);
                break;
            case PHONOGRAPH:
                drawable = context.getDrawable(R.mipmap.ic_phonograph_icon);
                break;
            case TELEGRAM:
            default:
                drawable = context.getDrawable(R.mipmap.ic_telegram_icon);
                break;
        }

        if (drawable != null) {
            mNewsType.setScaleType(ImageView.ScaleType.FIT_XY);
            mNewsType.setBackground(drawable);
        }
        // Set image

        // Content
     //   mContentView.setText(mEntry.getTextEntry());
    }

    @Override
    public String toString() {
        return super.toString() + " '";
                //+ mContentView.getText() + "'";
    }
}

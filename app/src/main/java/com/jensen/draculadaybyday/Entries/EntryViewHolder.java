package com.jensen.draculadaybyday.Entries;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jensen.draculadaybyday.Fragment.FragmentEntry;
import com.jensen.draculadaybyday.R;

public class EntryViewHolder extends RecyclerView.ViewHolder {
    public final View mView;

    private final Context context;

    // The header
    public final TextView mDate;
    public final TextView mName;
    public final ImageView mNewsType;

    // The content pange
//    public final TextView mContentView;

    public FragmentEntry fragmentEntry;

    public EntryViewHolder(View view, Context context) {
        super(view);
        mView = view;

        this.context = context;

        // Header
        mDate = (TextView) view.findViewById(R.id.date);
        mName = (TextView) view.findViewById(R.id.name);
        mNewsType = (ImageView) view.findViewById(R.id.news_type);

        // Content
      //  mContentView = (TextView) view.findViewById(R.id.content);
    }

    public void setViews() {
        // Header
        mDate.setText(fragmentEntry.getDateString());
        mName.setText(fragmentEntry.getPerson());

        Drawable drawable;
        switch (fragmentEntry.getType()) {
            case DIARY_ENTRY:
                drawable = context.getDrawable(R.mipmap.ic_diary_icon);
                break;
            case LETTER:
                drawable = context.getDrawable(R.mipmap.ic_letter_icon);
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
        mNewsType.setScaleType(ImageView.ScaleType.FIT_XY);
        mNewsType.setBackground(drawable);


        // Set image

        // Content
     //   mContentView.setText(fragmentEntry.getFragmentEntry());
    }

    @Override
    public String toString() {
        return super.toString() + " '";
                //+ mContentView.getText() + "'";
    }
}

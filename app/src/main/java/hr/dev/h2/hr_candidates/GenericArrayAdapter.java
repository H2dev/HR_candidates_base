package hr.dev.h2.hr_candidates;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.Image;
import android.media.Rating;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hrvoje on 21.11.2016..
 */

public abstract class GenericArrayAdapter<T> extends ArrayAdapter<T> {

    // Vars
    private LayoutInflater mInflater;

    public GenericArrayAdapter(Context context, List<T> objects) {
        super(context, 0, objects);
        init(context);
    }

    // Headers
    public abstract void drawText(TextView textView, T object);

    private void init(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    @Override public View getView(int position, View row, ViewGroup parent) {
        final ViewHolder vh;
        if (row == null) {
            row = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            vh = new ViewHolder(row);
            row.setTag(vh);
        } else {
            vh = (ViewHolder) row.getTag();
        }

        drawText(vh.textView, getItem(position));

        return row;
    }

    static class ViewHolder {

        TextView textView;

        public ViewHolder(View rootView) {
            textView = (TextView) rootView.findViewById(android.R.id.text1);
            textView.setHeight(44);
//            textView.setTextColor(Color.parseColor("#7dd22c"));
        }

        public TextView getTextView() {
            return textView;
        }

        public void setTextView(TextView textView) {
            this.textView = textView;
        }
    }
}
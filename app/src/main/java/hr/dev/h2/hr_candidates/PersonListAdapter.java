package hr.dev.h2.hr_candidates;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import hr.dev.h2.hr_candidates.model.Person;

/**
 * Created by Hrvoje on 21.11.2016..
 */

public class PersonListAdapter extends ArrayAdapter<Person> {

    Context context;
    List<Person> personListQuery;
    private static LayoutInflater inflater = null;
    Bitmap bm;

    public PersonListAdapter(Context context, List<Person> objects) {
        super(context, 0, objects);
        this.context = context;
        personListQuery = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    class ViewHolder {
        TextView tvPersonNameAndSurname;
        RatingBar ratingBarSmall;
        ImageView photoSmall;
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {

        ViewHolder viewHolder = new ViewHolder();

        View rowView;
        rowView = inflater.inflate(R.layout.person_list_item, parent, false);

        viewHolder.tvPersonNameAndSurname = (TextView) rowView.findViewById(R.id.tvPersonNameAndSurname);
        viewHolder.ratingBarSmall = (RatingBar) rowView.findViewById(R.id.ratingBarSmall);
        viewHolder.photoSmall = (ImageView) rowView.findViewById(R.id.ivSmallPhoto);

        if (personListQuery.get(position).getImageBytes() != null) {
            bm = BitmapFactory.decodeByteArray(personListQuery.get(position).getImageBytes(), 0, personListQuery.get(position).getImageBytes().length);
            viewHolder.photoSmall.setImageBitmap(bm);
        }
        viewHolder.tvPersonNameAndSurname.setText(personListQuery.get(position).getName() + " " + personListQuery.get(position).getSurname());
        viewHolder.ratingBarSmall.setRating(personListQuery.get(position).getRating());

        return rowView;
    }

}
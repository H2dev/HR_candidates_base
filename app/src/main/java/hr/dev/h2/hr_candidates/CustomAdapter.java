package hr.dev.h2.hr_candidates;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import hr.dev.h2.hr_candidates.R;
import hr.dev.h2.hr_candidates.model.JobVacancy;

/**
 * Created by Hrvoje on 5.12.2016..
 */

public class CustomAdapter extends BaseAdapter {

    List<JobVacancy> jobVacanciesListQuery;
    Context context;
    private static LayoutInflater inflater = null;

    public CustomAdapter(Context context, List<JobVacancy> jobVacanciesListQuery) {
        // TODO Auto-generated constructor stub
        this.jobVacanciesListQuery = jobVacanciesListQuery;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return jobVacanciesListQuery.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class Holder
    {
        TextView tv;
        ImageView img;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder = new Holder();

        View rowView;
        rowView = inflater.inflate(R.layout.job_vacancy_list_item, null);
        holder.tv=(TextView) rowView.findViewById(R.id.tvJobVacancyTitle);
        holder.tv.setText(jobVacanciesListQuery.get(position).getTitle());

        return rowView;
    }
}

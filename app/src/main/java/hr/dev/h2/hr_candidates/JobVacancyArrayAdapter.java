package hr.dev.h2.hr_candidates;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.j256.ormlite.dao.ForeignCollection;

import java.util.Iterator;
import java.util.List;

import hr.dev.h2.hr_candidates.model.JobVacancy;
import hr.dev.h2.hr_candidates.model.Person;

/**
 * Created by Hrvoje on 22.11.2016..
 */

public class JobVacancyArrayAdapter extends ArrayAdapter<JobVacancy> {

    Context context;
    List<JobVacancy> jobVacanciesListQuery;
    private static LayoutInflater inflater = null;

    public JobVacancyArrayAdapter(Context context, List<JobVacancy> objects) {
        super(context, 0, objects);
        this.context = context;
        jobVacanciesListQuery = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    class ViewHolder {
        TextView tvJobVacancyTitle;
        TextView tvNumberOfCandidates;
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {

        ViewHolder viewHolder = new ViewHolder();

        View rowView;
        rowView = inflater.inflate(R.layout.job_vacancy_list_item, parent, false);
        viewHolder.tvJobVacancyTitle = (TextView) rowView.findViewById(R.id.tvJobVacancyTitle);
        viewHolder.tvNumberOfCandidates = (TextView) rowView.findViewById(R.id.tvNumberOfCandidates);
        viewHolder.tvJobVacancyTitle.setText(jobVacanciesListQuery.get(position).getTitle());
        viewHolder.tvNumberOfCandidates.setText(getNumberOfCandidates(jobVacanciesListQuery.get(position)).toString());

        ViewGroup.LayoutParams params = rowView.getLayoutParams();
        params.height = 150;
        rowView.setLayoutParams(params);

        return rowView;
    }

    private Integer getNumberOfCandidates(JobVacancy jobVacancy) {
        ForeignCollection<Person> fc = jobVacancy.getPersons();
        return (fc.size());
    }

}

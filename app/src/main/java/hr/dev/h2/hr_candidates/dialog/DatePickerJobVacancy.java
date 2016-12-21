package hr.dev.h2.hr_candidates.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import java.util.Calendar;

import hr.dev.h2.hr_candidates.activity.EditJobVacancyActivity;

/**
 * Created by Hrvoje on 18.10.2016..
 */
public class DatePickerJobVacancy extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);

        return datePickerDialog;
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "." + (monthOfYear + 1) + "." + year;
        EditJobVacancyActivity editJobVacancyActivity = (EditJobVacancyActivity) getActivity();
        editJobVacancyActivity.getEtJobAdEndDate().setText(date);
    }

}

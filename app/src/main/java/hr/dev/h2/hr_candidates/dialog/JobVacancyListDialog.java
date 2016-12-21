package hr.dev.h2.hr_candidates.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import hr.dev.h2.hr_candidates.ConstantsHR;
import hr.dev.h2.hr_candidates.activity.MainActivity;
import hr.dev.h2.hr_candidates.R;
import hr.dev.h2.hr_candidates.activity.PersonsListActivity;

/**
 * Created by Hrvoje on 22.11.2016..
 */

public class JobVacancyListDialog extends DialogFragment {

    private String[] jobOptionsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jobOptionsList = getResources().getStringArray(R.array.jobOptionsMenu);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.options));
        builder.setItems(jobOptionsList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String chosenOption = jobOptionsList[i];
                Integer jobVacancySelectedId = getArguments().getInt(ConstantsHR.JOB_VACANCY_SELECTED_ID);
                //if chosen Candidates list
                if (chosenOption.equals(getResources().getString(R.string.view_candidates))) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), PersonsListActivity.class);
                    intent.putExtra(ConstantsHR.JOB_VACANCY_ID, jobVacancySelectedId);
                    startActivity(intent);
                }
                //if chosen View job jacancy
                else if (chosenOption.equals(getResources().getString(R.string.view_job_vacancy))) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.viewJobVacancy(jobVacancySelectedId);
                }
                //if chosen Edit job vacancy
                else if (chosenOption.equals(getResources().getString(R.string.edit_job))) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.editJobVacancy(jobVacancySelectedId);
                }
                //if chosen Delete
                else if (chosenOption.equals(getResources().getString(R.string.delete_job))) {
                    showConfirmationDialogDeleteJobVacancy(jobVacancySelectedId);
                }
                dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        return dialog;
    }

    private void showConfirmationDialogDeleteJobVacancy(Integer jobVacancySelectedId) {
        Bundle bundle = new Bundle(3);
        bundle.putInt(ConstantsHR.JOB_VACANCY_SELECTED_ID, jobVacancySelectedId);
        bundle.putString(ConstantsHR.TYPE, ConstantsHR.DELETE_JOB_VACANCY);
        bundle.putString(ConstantsHR.WARNING_MESSAGE, getString(R.string.are_you_sure_delete_job));
        ConfirmationDialog cd = new ConfirmationDialog();
        cd.setArguments(bundle);
        cd.show(getFragmentManager(), ConstantsHR.WARNING_DIALOG);
    }


}

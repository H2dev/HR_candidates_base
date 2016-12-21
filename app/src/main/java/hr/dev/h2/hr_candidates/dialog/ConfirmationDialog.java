package hr.dev.h2.hr_candidates.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import hr.dev.h2.hr_candidates.ConstantsHR;
import hr.dev.h2.hr_candidates.R;
import hr.dev.h2.hr_candidates.activity.MainActivity;
import hr.dev.h2.hr_candidates.activity.EditJobVacancyActivity;
import hr.dev.h2.hr_candidates.activity.EditPersonActivity;
import hr.dev.h2.hr_candidates.activity.PersonsListActivity;

/**
 * Created by Hrvoje on 23.11.2016..
 */

public class ConfirmationDialog extends DialogFragment {

    String message = "";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        message = getArguments().getString(ConstantsHR.WARNING_MESSAGE);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (getArguments().getString(ConstantsHR.TYPE).equals(ConstantsHR.DELETE_JOB_VACANCY)) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.deleteJobVacancy(getArguments().getInt(ConstantsHR.JOB_VACANCY_SELECTED_ID));
                } else if (getArguments().getString(ConstantsHR.TYPE).equals(ConstantsHR.DELETE_ALL_JOB_VACANCIES)) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.deleteAllJobVacancies();
                } else if (getArguments().getString(ConstantsHR.TYPE).equals(ConstantsHR.EXIT_EDIT_JOB_VACANCY)) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    getActivity().finish();
                    startActivity(intent);
                } else if (getArguments().getString(ConstantsHR.TYPE).equals(ConstantsHR.EXIT_EDIT_PERSON)) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), PersonsListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    getActivity().finish();
                    startActivity(intent);
                } else if (getArguments().getString(ConstantsHR.TYPE).equals(ConstantsHR.DELETE_PERSON)) {
                    PersonsListActivity personsListActivity = (PersonsListActivity) getActivity();
                    personsListActivity.deletePerson(getArguments().getInt(ConstantsHR.PERSON_SELECTED_ID));
                    Intent intent = new Intent(getActivity().getApplicationContext(), PersonsListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                } else if (getArguments().getString(ConstantsHR.TYPE).equals(ConstantsHR.SAVE_PERSON_DETAILS)) {
                    EditPersonActivity editPersonActivity = (EditPersonActivity) getActivity();
                    editPersonActivity.savePerson();
                    Intent intent = new Intent(getActivity().getApplicationContext(), PersonsListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                } else if (getArguments().getString(ConstantsHR.TYPE).equals(ConstantsHR.SAVE_JOB_VACANCY_DETAILS)) {
                    EditJobVacancyActivity editJobVacancyActivity = (EditJobVacancyActivity) getActivity();
                    editJobVacancyActivity.saveJobVacancy();
                    Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    getActivity().finish();
                    startActivity(intent);
                }
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        return alertDialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }
}

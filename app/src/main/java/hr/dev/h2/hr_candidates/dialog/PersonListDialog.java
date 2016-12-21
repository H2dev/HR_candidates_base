package hr.dev.h2.hr_candidates.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import hr.dev.h2.hr_candidates.ConstantsHR;
import hr.dev.h2.hr_candidates.R;
import hr.dev.h2.hr_candidates.activity.MainActivity;
import hr.dev.h2.hr_candidates.activity.PersonsListActivity;

/**
 * Created by Hrvoje on 25.11.2016..
 */

public class PersonListDialog extends DialogFragment {

    private String[] personOptionsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        personOptionsList = getResources().getStringArray(R.array.personOptionsMenu);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.options));
        builder.setItems(personOptionsList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String chosenOption = personOptionsList[i];
                Integer personSelectedId = getArguments().getInt(ConstantsHR.PERSON_SELECTED_ID);
                //if chosen View job jacancy
                if (chosenOption.equals(getResources().getString(R.string.view_person))) {
                    PersonsListActivity personsListActivity = (PersonsListActivity) getActivity();
                    personsListActivity.viewPerson(personSelectedId);
                }
                //if chosen Edit person details
                else if (chosenOption.equals(getResources().getString(R.string.edit_person))) {
                    PersonsListActivity personListActivity = (PersonsListActivity) getActivity();
                    personListActivity.editPersonDetails(personSelectedId);
                }
                //if chosen Delete
                else if (chosenOption.equals(getResources().getString(R.string.delete_person))) {
                    showConfirmationDialogDeletePerson(personSelectedId);
                }
                dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        return dialog;
    }

    private void showConfirmationDialogDeletePerson(Integer personSelectedId) {
        Bundle bundle = new Bundle(3);
        bundle.putInt(ConstantsHR.PERSON_SELECTED_ID, personSelectedId);
        bundle.putString(ConstantsHR.TYPE, ConstantsHR.DELETE_PERSON);
        bundle.putString(ConstantsHR.WARNING_MESSAGE, getString(R.string.are_you_sure_delete_person));
        ConfirmationDialog cd = new ConfirmationDialog();
        cd.setArguments(bundle);
        cd.show(getFragmentManager(), ConstantsHR.WARNING_DIALOG);
    }

}

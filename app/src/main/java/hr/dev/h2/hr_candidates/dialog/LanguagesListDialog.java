package hr.dev.h2.hr_candidates.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import hr.dev.h2.hr_candidates.ConstantsHR;
import hr.dev.h2.hr_candidates.R;
import hr.dev.h2.hr_candidates.activity.MainActivity;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Hrvoje on 27.11.2016..
 */

public class LanguagesListDialog extends DialogFragment {

    private String[] languagesList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        languagesList = getResources().getStringArray(R.array.languagesMenu);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.options));
        builder.setItems(languagesList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String chosenOption = languagesList[i];
                //if chosen English
                if (chosenOption.equals(getResources().getString(R.string.english))) {
                    SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(ConstantsHR.PREFERENCES_LANGUAGE, getResources().getString(R.string.english));
                    editor.commit();
                    MainActivity mainActivity = (MainActivity)getActivity();
                    mainActivity.restartActivity();
                }
                //if chosen Hrvatski
                else if (chosenOption.equals(getResources().getString(R.string.hrvatski))) {
                    SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(ConstantsHR.PREFERENCES_LANGUAGE, getResources().getString(R.string.hrvatski));
                    editor.commit();
                    MainActivity mainActivity = (MainActivity)getActivity();
                    mainActivity.restartActivity();
                }
                dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        return dialog;
    }

}

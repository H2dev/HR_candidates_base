package hr.dev.h2.hr_candidates.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import hr.dev.h2.hr_candidates.ConstantsHR;
import hr.dev.h2.hr_candidates.R;

/**
 * Created by Hrvoje on 21.11.2016..
 */

public class WarningDialog extends DialogFragment {

    String message = "";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        message = getArguments().getString(ConstantsHR.WARNING_MESSAGE);
        builder.setMessage(message);
        builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        return alertDialog;
    }
}

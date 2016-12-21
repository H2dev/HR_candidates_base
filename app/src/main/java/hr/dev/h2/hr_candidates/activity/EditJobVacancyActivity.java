package hr.dev.h2.hr_candidates.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import hr.dev.h2.hr_candidates.CandidatesDatabaseHelper;
import hr.dev.h2.hr_candidates.ConstantsHR;
import hr.dev.h2.hr_candidates.R;
import hr.dev.h2.hr_candidates.dialog.ConfirmationDialog;
import hr.dev.h2.hr_candidates.dialog.DatePickerJobVacancy;
import hr.dev.h2.hr_candidates.dialog.WarningDialog;
import hr.dev.h2.hr_candidates.model.JobVacancy;

import static hr.dev.h2.hr_candidates.ConstantsHR.JOB_VACANCY_SELECTED_ID;

public class EditJobVacancyActivity extends AppCompatActivity {

    Integer jobVacancySelectedId = null;
    boolean isNewAdd = true;

    CandidatesDatabaseHelper candidatesDatabaseHelper = null;
    Dao<JobVacancy, Integer> jobVacancyDao = null;

    private TextView tvJobTitle;
    private TextView tvJobDescription;
    private TextView tvJobAdEndDate;
    private EditText etJobTitle;
    private EditText etJobDescription;
    private EditText etJobAdEndDate;
    private Button btJobSave;
    private Button btJobSaveCancel;
    private Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_job_vacancy);

        initWidgets();
        initConnection();
        setupListeners();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent.hasExtra(ConstantsHR.JOB_VACANCY_SELECTED_ID)) {
            isNewAdd = false;
            jobVacancySelectedId = intent.getIntExtra(JOB_VACANCY_SELECTED_ID, 0);
            initJobVacancyDataView();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showConfirmationDialogExitEditJobVacancy();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showConfirmationDialogExitEditJobVacancy();
    }

    private void initWidgets() {
        tvJobTitle = (TextView) findViewById(R.id.tvJobTitle);
        tvJobDescription = (TextView) findViewById(R.id.tvJobDescription);
        tvJobAdEndDate = (TextView) findViewById(R.id.tvJobAdEndDate);
        etJobTitle = (EditText) findViewById(R.id.etJobTitle);
        etJobDescription = (EditText) findViewById(R.id.etJobDescription);
        etJobAdEndDate = (EditText) findViewById(R.id.etJobAdEndDate);
        btJobSave = (Button) findViewById(R.id.btJobSave);
        btJobSaveCancel = (Button) findViewById(R.id.btJobSaveCancel);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.activity_edit_job_vacancy);
        linearLayout.requestFocus();
    }

    private void initConnection() {
        candidatesDatabaseHelper = new CandidatesDatabaseHelper(this);
        try {
            jobVacancyDao = candidatesDatabaseHelper.getJobVacancyDao();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    private void initJobVacancyDataView() {
        try {
            JobVacancy jobVacancySelected = jobVacancyDao.queryForId(jobVacancySelectedId);
            etJobTitle.setText(jobVacancySelected.getTitle());
            etJobDescription.setText(jobVacancySelected.getDescription());
            etJobAdEndDate.setText(jobVacancySelected.getJobAdEndDate());
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    private void setupListeners() {
        btJobSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etJobTitle.getText().toString().isEmpty()) {
                    showDialogTitleMustBeEntered();
                } else {
                    showConfirmationDialogSaveJobVacancy();
                }
            }
        });
        btJobSaveCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDialogExitEditJobVacancy();
            }
        });
        etJobAdEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerJobVacancy datePickerJobVacancy = new DatePickerJobVacancy();
                datePickerJobVacancy.show(getFragmentManager(), getString(R.string.date));
            }
        });

    }

    public void saveJobVacancy() {
        if (isNewAdd) {
            JobVacancy jobVacancyToAdd = new JobVacancy();
            jobVacancyToAdd.setTitle(etJobTitle.getText().toString());
            jobVacancyToAdd.setDescription(etJobDescription.getText().toString());
            jobVacancyToAdd.setJobAdEndDate(etJobAdEndDate.getText().toString());
            try {
                jobVacancyDao.create(jobVacancyToAdd);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                JobVacancy jobVacancySelected = jobVacancyDao.queryForId(jobVacancySelectedId);
                jobVacancySelected.setTitle(etJobTitle.getText().toString());
                jobVacancySelected.setDescription(etJobDescription.getText().toString());
                jobVacancySelected.setJobAdEndDate(etJobAdEndDate.getText().toString());
                jobVacancyDao.update(jobVacancySelected);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        Toast.makeText(EditJobVacancyActivity.this, R.string.job_vacancy_data_saved, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void showConfirmationDialogSaveJobVacancy() {
        Bundle bundle = new Bundle(2);
        bundle.putString(ConstantsHR.TYPE, ConstantsHR.SAVE_JOB_VACANCY_DETAILS);
        bundle.putString(ConstantsHR.WARNING_MESSAGE, getString(R.string.are_you_sure_save_job));
        ConfirmationDialog cd = new ConfirmationDialog();
        cd.setArguments(bundle);
        cd.show(getFragmentManager(), ConstantsHR.WARNING_DIALOG);
    }

    private void showConfirmationDialogExitEditJobVacancy() {
        Bundle bundle = new Bundle(2);
        bundle.putString(ConstantsHR.TYPE, ConstantsHR.EXIT_EDIT_JOB_VACANCY);
        bundle.putString(ConstantsHR.WARNING_MESSAGE, getString(R.string.are_you_sure_exit_edit_job));
        ConfirmationDialog cd = new ConfirmationDialog();
        cd.setArguments(bundle);
        cd.show(getFragmentManager(), ConstantsHR.WARNING_DIALOG);
    }

    private void showDialogTitleMustBeEntered() {
        Bundle bundle = new Bundle(1);
        bundle.putString(ConstantsHR.WARNING_MESSAGE, getString(R.string.title_must_be_entered));
        WarningDialog wd = new WarningDialog();
        wd.setArguments(bundle);
        wd.show(getFragmentManager(), ConstantsHR.WARNING_DIALOG);
    }

    public EditText getEtJobAdEndDate() {
        return etJobAdEndDate;
    }

    public void setEtJobAdEndDate(EditText etJobAdEndDate) {
        this.etJobAdEndDate = etJobAdEndDate;
    }

}

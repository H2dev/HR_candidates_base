package hr.dev.h2.hr_candidates.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import hr.dev.h2.hr_candidates.CandidatesDatabaseHelper;
import hr.dev.h2.hr_candidates.ConstantsHR;
import hr.dev.h2.hr_candidates.R;
import hr.dev.h2.hr_candidates.model.JobVacancy;

import static hr.dev.h2.hr_candidates.ConstantsHR.JOB_VACANCY_SELECTED_ID;

public class ViewJobVacancyActivity extends AppCompatActivity {

    Integer jobVacancySelectedId = null;

    CandidatesDatabaseHelper candidatesDatabaseHelper = null;
    Dao<JobVacancy, Integer> jobVacancyDao = null;

    private TextView tvPrintJobTitle;
    private TextView tvPrintJobDescription;
    private TextView tvPrintJobAdEndDate;
    private Button btJobGoBack;
    private Button btJobEdit;
    private Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_job_vacancy);

        initConnection();
        initWidgets();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        try {
            jobVacancySelectedId = intent.getIntExtra(JOB_VACANCY_SELECTED_ID, 0);
            initJobVacancyDataView();
        } catch (Exception e) {
            e.printStackTrace();
            Intent intentRestart = new Intent(this, MainActivity.class);
            startActivity(intentRestart);
        }

        setupListeners();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initJobVacancyDataView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initJobVacancyDataView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
//        NavUtils.navigateUpFromSameTask(this);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        finish();
        startActivity(intent);
    }

    private void initWidgets() {
        tvPrintJobTitle = (TextView) findViewById(R.id.tvPrintJobTitle);
        tvPrintJobDescription = (TextView) findViewById(R.id.tvPrintJobDescription);
        tvPrintJobAdEndDate = (TextView) findViewById(R.id.tvPrintJobAdEndDate);
        btJobGoBack = (Button) findViewById(R.id.btJobGoBack);
        btJobEdit = (Button) findViewById(R.id.btJobEdit);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
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
            tvPrintJobTitle.setText(jobVacancySelected.getTitle());
            tvPrintJobDescription.setText(jobVacancySelected.getDescription());
            tvPrintJobAdEndDate.setText(jobVacancySelected.getJobAdEndDate());
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    private void setupListeners() {
        btJobEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditJobVacancyActivity.class);
                intent.putExtra(ConstantsHR.JOB_VACANCY_SELECTED_ID, jobVacancySelectedId);
                finish();
                startActivity(intent);
            }
        });
        btJobGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
    }

}

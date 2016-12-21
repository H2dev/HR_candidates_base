package hr.dev.h2.hr_candidates.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import hr.dev.h2.hr_candidates.CandidatesDatabaseHelper;
import hr.dev.h2.hr_candidates.ConstantsHR;
import hr.dev.h2.hr_candidates.R;
import hr.dev.h2.hr_candidates.model.Person;

import static hr.dev.h2.hr_candidates.ConstantsHR.PERSON_SELECTED_ID;

public class ViewPersonActivity extends AppCompatActivity {

    Integer personSelectedId = null;

    CandidatesDatabaseHelper candidatesDatabaseHelper = null;
    Dao<Person, Integer> personDao = null;

    private TextView tvPrintName;
    private TextView tvPrintSurname;
    private TextView tvPrintDateOfBirth;
    private TextView tvPrintQualifications;
    private TextView tvPrintPastWorkExperience;
    private TextView tvPrintAddress;
    private TextView tvPrintTelNum;
    private TextView tvPrintEmail;
    private TextView tvPrintInterviewNotes;

    private RatingBar ratingBarView;
    private Button btPersonGoBack;
    private Button btPersonEdit;
    private ImageView ivViewPersonPhoto;
    private Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_person);

        initConnection();
        initWidgets();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        try {
            personSelectedId = intent.getIntExtra(PERSON_SELECTED_ID, 0);
            initPersonDataView();
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
        initPersonDataView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPersonDataView();
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
        Intent intent = new Intent(getApplicationContext(), PersonsListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        finish();
        startActivity(intent);
    }

    private void initWidgets() {
        tvPrintName = (TextView) findViewById(R.id.tvPrintName);
        tvPrintSurname = (TextView) findViewById(R.id.tvPrintSurname);
        tvPrintDateOfBirth = (TextView) findViewById(R.id.tvPrintDateOfBirth);
        tvPrintQualifications = (TextView) findViewById(R.id.tvPrintQualifications);
        tvPrintPastWorkExperience = (TextView) findViewById(R.id.tvPrintWorkExperience);
        tvPrintAddress = (TextView) findViewById(R.id.tvPrintAddress);
        tvPrintTelNum = (TextView) findViewById(R.id.tvPrintTelNum);
        tvPrintEmail = (TextView) findViewById(R.id.tvPrintEmail);
        tvPrintInterviewNotes = (TextView) findViewById(R.id.tvPrintInterviewNotes);
        ratingBarView = (RatingBar) findViewById(R.id.ratingBarView);
        btPersonEdit = (Button) findViewById(R.id.btPersonEdit);
        btPersonGoBack = (Button) findViewById(R.id.btPersonGoBack);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ivViewPersonPhoto = (ImageView) findViewById(R.id.ivViewPersonPhoto);
    }

    private void initConnection() {
        candidatesDatabaseHelper = new CandidatesDatabaseHelper(this);
        try {
            personDao = candidatesDatabaseHelper.getPersonDao();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    private void initPersonDataView() {
        try {
            Person personSelected = personDao.queryForId(personSelectedId);
            tvPrintName.setText(personSelected.getName());
            tvPrintSurname.setText(personSelected.getSurname());
            tvPrintDateOfBirth.setText(personSelected.getDateOfBirth());
            tvPrintQualifications.setText(personSelected.getQualifications());
            tvPrintPastWorkExperience.setText(personSelected.getPastWorkExperience());
            tvPrintAddress.setText(personSelected.getAddress());
            tvPrintTelNum.setText(personSelected.getTelNum());
            tvPrintEmail.setText(personSelected.getEmail());
            tvPrintInterviewNotes.setText(personSelected.getInterviewNotes());
            ratingBarView.setRating(personSelected.getRating());
            fetchPhoto(personSelected);
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    private void fetchPhoto(Person personSelected) {
        if (personSelected.getImageBytes() != null) {
            try {
                Bitmap bm = BitmapFactory.decodeByteArray(personSelected.getImageBytes(), 0, personSelected.getImageBytes().length);
                ivViewPersonPhoto.setImageBitmap(bm);
            } catch (Exception e) {
                e.printStackTrace();
                fetchDefaultPhoto();
            }
        } else {
            fetchDefaultPhoto();
        }
    }

    private void fetchDefaultPhoto() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_photo);
        ivViewPersonPhoto.setImageBitmap(bitmap);
    }

    private void setupListeners() {
        btPersonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditPersonActivity.class);
                intent.putExtra(ConstantsHR.PERSON_SELECTED_ID, personSelectedId);
                finish();
                startActivity(intent);
            }
        });
        btPersonGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
    }


}

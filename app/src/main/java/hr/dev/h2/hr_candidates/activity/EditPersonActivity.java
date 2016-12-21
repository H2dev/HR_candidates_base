package hr.dev.h2.hr_candidates.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import hr.dev.h2.hr_candidates.CandidatesDatabaseHelper;
import hr.dev.h2.hr_candidates.ConstantsHR;
import hr.dev.h2.hr_candidates.dialog.ConfirmationDialog;
import hr.dev.h2.hr_candidates.R;
import hr.dev.h2.hr_candidates.dialog.DatePickerJobVacancy;
import hr.dev.h2.hr_candidates.dialog.DatePickerPerson;
import hr.dev.h2.hr_candidates.dialog.WarningDialog;
import hr.dev.h2.hr_candidates.model.JobVacancy;
import hr.dev.h2.hr_candidates.model.Person;

public class EditPersonActivity extends AppCompatActivity {

    public static final int MAX_BITMAP_BYTE_SIZE = 1200000;

    private Integer personSelectedId;
    private boolean isNewAdd = true;
    private byte[] byteArrayPhotoToSave;

    CandidatesDatabaseHelper candidatesDatabaseHelper = null;
    Dao<Person, Integer> personDao = null;
    Dao<JobVacancy, Integer> jobVacancyDao = null;

    private EditText etName;
    private EditText etSurname;
    private EditText etDateOfBirth;
    private EditText etQualifications;
    private EditText etPastWorkExperience;
    private EditText etAddress;
    private EditText etTelNum;
    private EditText etEmail;
    private EditText etInterviewNotes;

    private Button btPersonSave;
    private Button btPersonSaveCancel;
    private Toolbar myToolbar;
    private Button btChangePhoto;
    private ImageView ivEditPersonPhoto;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_person);

        initWidgets();
        initConnection();
        setupListeners();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent.hasExtra(ConstantsHR.PERSON_SELECTED_ID)) {
            isNewAdd = false;
            personSelectedId = intent.getIntExtra(ConstantsHR.PERSON_SELECTED_ID, 0);
            initPersonDataView();
        } else {
            fetchDefaultPhoto();
        }
    }

    private void initWidgets() {
        etName = (EditText) findViewById(R.id.etName);
        etSurname = (EditText) findViewById(R.id.etSurname);
        etDateOfBirth = (EditText) findViewById(R.id.etDateOfBirth);
        etQualifications = (EditText) findViewById(R.id.etQualifications);
        etPastWorkExperience = (EditText) findViewById(R.id.etPastWorkExperience);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etTelNum = (EditText) findViewById(R.id.etTelNum);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etInterviewNotes = (EditText) findViewById(R.id.etInterviewNotes);
        btPersonSave = (Button) findViewById(R.id.btPersonSave);
        btPersonSaveCancel = (Button) findViewById(R.id.btPersonSaveCancel);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ivEditPersonPhoto = (ImageView) findViewById(R.id.ivEditPersonPhoto);
        btChangePhoto = (Button) findViewById(R.id.btChangePhoto);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.activity_edit_person);
        linearLayout.requestFocus();
    }

    private void initConnection() {
        candidatesDatabaseHelper = new CandidatesDatabaseHelper(this);
        try {
            personDao = candidatesDatabaseHelper.getPersonDao();
            jobVacancyDao = candidatesDatabaseHelper.getJobVacancyDao();
        } catch (java.sql.SQLException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showConfirmationDialogExitEditPerson();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showConfirmationDialogExitEditPerson();
    }


    private void initPersonDataView() {
        try {
            Person personSelected = personDao.queryForId(personSelectedId);
            etName.setText(personSelected.getName());
            etSurname.setText(personSelected.getSurname());
            etDateOfBirth.setText(personSelected.getDateOfBirth());
            etQualifications.setText(personSelected.getQualifications());
            etPastWorkExperience.setText(personSelected.getPastWorkExperience());
            etAddress.setText(personSelected.getAddress());
            etTelNum.setText(personSelected.getTelNum());
            etEmail.setText(personSelected.getEmail());
            etInterviewNotes.setText(personSelected.getInterviewNotes());

            fetchPhoto(personSelected);
            ratingBar.setRating(personSelected.getRating());
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
                ivEditPersonPhoto.setImageBitmap(bm);
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
        ivEditPersonPhoto.setImageBitmap(bitmap);
    }

    private void setupListeners() {
        btPersonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etName.getText().toString().isEmpty()) {
                    showDialogNameMustBeEntered();
                } else {
                    showConfirmationDialogSavePerson();
                }
            }
        });
        btPersonSaveCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDialogExitEditPerson();
            }
        });
        btChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageChooser();
            }
        });
        etDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerPerson datePickerPerson = new DatePickerPerson();
                datePickerPerson.show(getFragmentManager(), getString(R.string.date));
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), ConstantsHR.REQUEST_CODE_SELECT_FROM_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == ConstantsHR.REQUEST_CODE_SELECT_FROM_GALLERY) {
                Uri selectedImageUri = data.getData();
                //save photo in the database
                try {
                    InputStream inStream = getContentResolver().openInputStream(selectedImageUri);
//                    Bitmap bm = BitmapFactory.decodeByteArray(byteArrayPhotoToSave, 0, byteArrayPhotoToSave.length);
                    Bitmap bm = BitmapFactory.decodeStream(inStream);
                    Bitmap bmToSave;

                    //scale the image if too big
                    if (bm.getByteCount() > MAX_BITMAP_BYTE_SIZE) {
                        int startSize = bm.getByteCount();
                        double factor = Math.sqrt((double)MAX_BITMAP_BYTE_SIZE / (double)startSize);
                        bmToSave = Bitmap.createScaledBitmap(bm, (int) (bm.getWidth() * factor), (int) (bm.getHeight() * factor), true);
                    }
                    else {
                        bmToSave = bm;
                    }

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmToSave.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    ivEditPersonPhoto.setImageBitmap(bmToSave);
                    byteArrayPhotoToSave = stream.toByteArray();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    /* Get the real path from the URI */
    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public void savePerson() {
        if (isNewAdd) {
            Person personToAdd = new Person();
            try {
                personToAdd.setName(etName.getText().toString());
                personToAdd.setSurname(etSurname.getText().toString());
                personToAdd.setDateOfBirth(etDateOfBirth.getText().toString());
                personToAdd.setQualifications(etQualifications.getText().toString());
                personToAdd.setPastWorkExperience(etPastWorkExperience.getText().toString());
                personToAdd.setAddress(etAddress.getText().toString());
                personToAdd.setTelNum(etTelNum.getText().toString());
                personToAdd.setEmail(etEmail.getText().toString());
                personToAdd.setInterviewNotes(etInterviewNotes.getText().toString());
                personToAdd.setJobVacancy(jobVacancyDao.queryForId(getIntent().getIntExtra("jobVacancyId", 0)));
                if (byteArrayPhotoToSave != null)
                    personToAdd.setImageBytes(byteArrayPhotoToSave);
                personToAdd.setRating(ratingBar.getRating());
                personDao.create(personToAdd);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Person personSelected = personDao.queryForId(personSelectedId);
                personSelected.setName(etName.getText().toString());
                personSelected.setSurname(etSurname.getText().toString());
                personSelected.setDateOfBirth(etDateOfBirth.getText().toString());
                personSelected.setQualifications(etQualifications.getText().toString());
                personSelected.setPastWorkExperience(etPastWorkExperience.getText().toString());
                personSelected.setAddress(etAddress.getText().toString());
                personSelected.setTelNum(etTelNum.getText().toString());
                personSelected.setEmail(etEmail.getText().toString());
                personSelected.setInterviewNotes(etInterviewNotes.getText().toString());
                personSelected.setName(etName.getText().toString());
                if (byteArrayPhotoToSave != null)
                    personSelected.setImageBytes(byteArrayPhotoToSave);
                personSelected.setRating(ratingBar.getRating());
                personDao.update(personSelected);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent(getApplicationContext(), PersonsListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        Toast.makeText(EditPersonActivity.this, R.string.person_data_saved, Toast.LENGTH_SHORT).show();
        finish();
    }

    public EditText getEtDateOfBirth() {
        return etDateOfBirth;
    }

    public void setEtDateOfBirth(EditText etDateOfBirth) {
        this.etDateOfBirth = etDateOfBirth;
    }

    private void showDialogNameMustBeEntered() {
        Bundle bundle = new Bundle(1);
        bundle.putString(ConstantsHR.WARNING_MESSAGE, getString(R.string.name_must_be_entered));
        WarningDialog wd = new WarningDialog();
        wd.setArguments(bundle);
        wd.show(getFragmentManager(), ConstantsHR.WARNING_DIALOG);
    }

    private void showConfirmationDialogExitEditPerson() {
        Bundle bundle = new Bundle(2);
        bundle.putString(ConstantsHR.TYPE, ConstantsHR.EXIT_EDIT_PERSON);
        bundle.putString(ConstantsHR.WARNING_MESSAGE, getString(R.string.are_you_sure_exit_edit_person));
        ConfirmationDialog cd = new ConfirmationDialog();
        cd.setArguments(bundle);
        cd.show(getFragmentManager(), ConstantsHR.WARNING_DIALOG);
    }

    private void showConfirmationDialogSavePerson() {
        Bundle bundle = new Bundle(2);
        bundle.putString(ConstantsHR.TYPE, ConstantsHR.SAVE_PERSON_DETAILS);
        bundle.putString(ConstantsHR.WARNING_MESSAGE, getString(R.string.are_you_sure_save_person));
        ConfirmationDialog cd = new ConfirmationDialog();
        cd.setArguments(bundle);
        cd.show(getFragmentManager(), ConstantsHR.WARNING_DIALOG);
    }

}

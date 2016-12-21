package hr.dev.h2.hr_candidates.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import hr.dev.h2.hr_candidates.CandidatesDatabaseHelper;
import hr.dev.h2.hr_candidates.ConstantsHR;
import hr.dev.h2.hr_candidates.PersonListAdapter;
import hr.dev.h2.hr_candidates.dialog.PersonListDialog;
import hr.dev.h2.hr_candidates.model.JobVacancy;
import hr.dev.h2.hr_candidates.model.Person;
import hr.dev.h2.hr_candidates.R;

public class PersonsListActivity extends AppCompatActivity {

    private ListView lvPersons;
    private FloatingActionButton floatingAddButton;
    private Toolbar myToolbar;
    private Spinner spinnerPersonSort;

    Person personSelected = new Person();
    private static Integer jobVacancyId;
    private Integer spinnerChosenPosition;
    private String orderByCriterionFieldPerson = "";
    private boolean booleanAsc = false;
    private SharedPreferences preferences;

    CandidatesDatabaseHelper candidatesDatabaseHelper = null;
    Dao<Person, Integer> personDao = null;
    Dao<JobVacancy, Integer> jobVacancyDao = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSharedPreferences();
        setContentView(R.layout.activity_persons_list);

        if (savedInstanceState != null) {
            jobVacancyId = savedInstanceState.getInt(ConstantsHR.JOB_VACANCY_ID);
        }
        if (getIntent().hasExtra(ConstantsHR.JOB_VACANCY_ID)) {
            jobVacancyId = getIntent().getIntExtra(ConstantsHR.JOB_VACANCY_ID, 0);
        }

        initWidgets();
        initConnection();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        refreshList();
        setupListeners();
    }

    private void initSharedPreferences() {
        this.preferences = getPreferences(Context.MODE_PRIVATE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ConstantsHR.JOB_VACANCY_ID, jobVacancyId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        jobVacancyId = savedInstanceState.getInt(ConstantsHR.JOB_VACANCY_ID);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }

    private void initConnection() {
        candidatesDatabaseHelper = new CandidatesDatabaseHelper(this);
        try {
            jobVacancyDao = candidatesDatabaseHelper.getJobVacancyDao();
            personDao = candidatesDatabaseHelper.getPersonDao();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    private void initWidgets() {
        lvPersons = (ListView) findViewById(R.id.lvPersons);
        floatingAddButton = (FloatingActionButton) findViewById(R.id.floatingAddButtonPerson);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //spinner init
        spinnerPersonSort = (Spinner) findViewById(R.id.spinnerPersonSort);
        ArrayAdapter spinnerArrayAdapter = ArrayAdapter.createFromResource(this, R.array.personSortMenu, R.layout.spinner_sort_criterion);
        spinnerPersonSort.setAdapter(spinnerArrayAdapter);
        spinnerPersonSort.setSelection(Adapter.NO_SELECTION, false);
    }

    private void setupListeners() {

        lvPersons.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                personSelected = (Person) adapterView.getItemAtPosition(i);
                PersonListDialog listDialog = new PersonListDialog();
                Bundle bundle = new Bundle(1);
                bundle.putInt(ConstantsHR.PERSON_SELECTED_ID, personSelected.getId());
                listDialog.setArguments(bundle);
                listDialog.show(getFragmentManager(), ConstantsHR.PERSON_OPTIONS_DIALOG);
                return true;
            }
        });
        floatingAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPerson();
            }
        });
        spinnerPersonSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String chosenCriterion = adapterView.getItemAtPosition(i).toString();
                if (chosenCriterion.equals(getResources().getString(R.string.sortPersonSurnameAsc))) {
                    orderByCriterionFieldPerson = getResources().getString(R.string.dbFieldSurname);
                    booleanAsc = true;
                    spinnerChosenPosition = i;
                } else if (chosenCriterion.equals(getResources().getString(R.string.sortPersonSurnameDesc))) {
                    orderByCriterionFieldPerson = getResources().getString(R.string.dbFieldSurname);
                    booleanAsc = false;
                    spinnerChosenPosition = i;
                } else if (chosenCriterion.equals(getResources().getString(R.string.ratingAsc))) {
                    orderByCriterionFieldPerson = getResources().getString(R.string.dbFieldRating);
                    booleanAsc = true;
                    spinnerChosenPosition = i;
                } else if (chosenCriterion.equals(getResources().getString(R.string.ratingDesc))) {
                    orderByCriterionFieldPerson = getResources().getString(R.string.dbFieldRating);
                    booleanAsc = false;
                    spinnerChosenPosition = i;
                }
                setSortCriterionPreference();
                refreshList();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setSortCriterionPreference() {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ConstantsHR.SORT_CRITERION_PERSON_FIELD, orderByCriterionFieldPerson);

        /****test */
//        editor.putString(ConstantsHR.SORT_CRITERION_PERSON_FIELD, "surname");

        String stringValueBooleanAsc = String.valueOf(booleanAsc);
        editor.putString(ConstantsHR.SORT_CRITERION_PERSON_ASC_BOOLEAN, stringValueBooleanAsc );
        editor.putString(ConstantsHR.SPINNER_PERSON_SORT_CHOSEN_POSITION, spinnerChosenPosition.toString());
        editor.commit();
    }

    private void refreshList() {
        try {
            QueryBuilder<Person, Integer> queryBuilder = personDao.queryBuilder();
            orderByCriterionFieldPerson = preferences.getString(ConstantsHR.SORT_CRITERION_PERSON_FIELD, getResources().getString(R.string.dbFieldSurname));
            booleanAsc = Boolean.parseBoolean(preferences.getString(ConstantsHR.SORT_CRITERION_PERSON_ASC_BOOLEAN, "true"));
            spinnerPersonSort.setSelection(Integer.parseInt(preferences.getString(ConstantsHR.SPINNER_PERSON_SORT_CHOSEN_POSITION, "0")));
            queryBuilder.where().eq(Person.JOB_VACANCY_ID, jobVacancyId);
            queryBuilder.orderBy(orderByCriterionFieldPerson, booleanAsc);
            PreparedQuery<Person> preparedQuery = queryBuilder.prepare();
            List<Person> personListQuery = personDao.query(preparedQuery);
            PersonListAdapter adapterPersonList = new PersonListAdapter(this, personListQuery);
            lvPersons.setAdapter(adapterPersonList);

//            JobVacancy jobVacancySelected = jobVacancyDao.queryForId(jobVacancyId);
//            ForeignCollection<Person> fc = jobVacancySelected.getPersons();
//            Iterator<Person> iterator = fc.iterator();
//            while (iterator.hasNext()) {
//                personListQuery.add(iterator.next());
//            }
//            PersonListAdapter adapterPersonList = new PersonListAdapter(this, personListQuery);
//            lvPersons.setAdapter(adapterPersonList);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePerson(Integer personId) {
        try {
            personDao.deleteById(personId);
            Toast.makeText(PersonsListActivity.this, R.string.person_deleted, Toast.LENGTH_SHORT).show();
            refreshList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void editPersonDetails(Integer personSelectedId) {
        Intent intent = new Intent(this, EditPersonActivity.class);
        intent.putExtra(ConstantsHR.PERSON_SELECTED_ID, personSelectedId);
        startActivity(intent);
    }

    public void viewPerson(Integer personSelectedId) {
        Intent intent = new Intent(this, ViewPersonActivity.class);
        intent.putExtra(ConstantsHR.PERSON_SELECTED_ID, personSelectedId);
        finish();
        startActivity(intent);
    }

    private void addPerson() {
        Intent intent = new Intent(this, EditPersonActivity.class);
        intent.putExtra(ConstantsHR.JOB_VACANCY_ID, jobVacancyId);
        startActivity(intent);
    }

}

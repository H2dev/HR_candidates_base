package hr.dev.h2.hr_candidates.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import hr.dev.h2.hr_candidates.CandidatesDatabaseHelper;
import hr.dev.h2.hr_candidates.ConstantsHR;
import hr.dev.h2.hr_candidates.CustomAdapter;
import hr.dev.h2.hr_candidates.JobVacancyArrayAdapter;
import hr.dev.h2.hr_candidates.PersonListAdapter;
import hr.dev.h2.hr_candidates.R;
import hr.dev.h2.hr_candidates.dialog.ConfirmationDialog;
import hr.dev.h2.hr_candidates.dialog.JobVacancyListDialog;
import hr.dev.h2.hr_candidates.dialog.LanguagesListDialog;
import hr.dev.h2.hr_candidates.model.JobVacancy;
import hr.dev.h2.hr_candidates.model.Person;

public class MainActivity extends AppCompatActivity {

    private ListView lvJobVacancies;
    private FloatingActionButton floatingAddButton;
    private Toolbar myToolbar;
    private Spinner spinnerJobSort;

    private int counterBackPressed = 0;
    private ArrayList<Long> timeBackPressed = new ArrayList<Long>();

    private JobVacancy jobVacancySelected = new JobVacancy();
    private static Menu menuEmpty;
    private SharedPreferences preferences;
    private String orderByCriterionFieldJob = "";
    private boolean booleanAsc = false;
    private Integer spinnerChosenPosition;

    CandidatesDatabaseHelper candidatesDatabaseHelper = null;
    Dao<JobVacancy, Integer> jobVacancyDao = null;
    Dao<Person, Integer> personDao = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSharedPreferences();
        setLocale();
        setContentView(R.layout.activity_main);

        initWidgets();
        initConnection();
        refreshList();
        setupListeners();
    }

    private void initSharedPreferences() {
        this.preferences = getPreferences(Context.MODE_PRIVATE);
    }

    private void setLocale() {
        String languagePreference = preferences.getString(ConstantsHR.PREFERENCES_LANGUAGE, getResources().getString(R.string.english));
        if (languagePreference.equals(getResources().getString(R.string.hrvatski))) {
            Locale locale = new Locale("hr");
            Locale.setDefault(locale);
            Configuration config = getBaseContext().getResources().getConfiguration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        } else if (languagePreference.equals(getResources().getString(R.string.english))) {
            Locale locale = new Locale("");
            Locale.setDefault(locale);
            Configuration config = getBaseContext().getResources().getConfiguration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }

    }

    public void restartActivity() {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (menuEmpty != null) {
            onCreateOptionsMenu(menuEmpty);
        }
        refreshList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (menuEmpty != null) {
            onCreateOptionsMenu(menuEmpty);
        }
        refreshList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menuEmpty = menu;
        menuEmpty.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.job_vacancies_menu, menu);

        //adding an icon to Language
        MenuItem itemLanguage = menu.findItem(R.id.languagesOption);
        SpannableStringBuilder builder1 = new SpannableStringBuilder("*   " + getResources().getString(R.string.language));
        builder1.setSpan(new ImageSpan(this, R.drawable.ic_language_black_24dp), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        itemLanguage.setTitle(builder1);

        //adding an icon to Delete All
        MenuItem itemDelete = menu.findItem(R.id.deleteAllOption);
        SpannableStringBuilder builder2 = new SpannableStringBuilder("*   " + getResources().getString(R.string.delete_all));
        builder2.setSpan(new ImageSpan(this, R.drawable.ic_delete_black_24dp), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        itemDelete.setTitle(builder2);

        try {
            if (jobVacancyDao.countOf() == 0) {
                menu.findItem(R.id.deleteAllOption).setVisible(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.languagesOption:
                LanguagesListDialog listDialog = new LanguagesListDialog();
                listDialog.show(getFragmentManager(), ConstantsHR.LIST_DIALOG);
                return true;
            case R.id.deleteAllOption:
                showConfirmationDialogDeleteAllJobVacancies();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        timeBackPressed.add(System.currentTimeMillis());
        int arraySize = timeBackPressed.size();
        ++counterBackPressed;
        if (counterBackPressed > 1 && timeBackPressed.get(arraySize - 1) - timeBackPressed.get(arraySize - 2) < 2500) {
            finish();
        } else {
            Toast.makeText(this, R.string.press_back_twice, Toast.LENGTH_SHORT).show();
        }
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
        lvJobVacancies = (ListView) findViewById(R.id.lvJobVacancies);
        floatingAddButton = (FloatingActionButton) findViewById(R.id.floatingAddButton);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setLogo(R.mipmap.ic_launcher);

        //spinner init
        spinnerJobSort = (Spinner) findViewById(R.id.spinnerJobSort);
        ArrayAdapter spinnerArrayAdapter = ArrayAdapter.createFromResource(this, R.array.jobVacancySortMenu, R.layout.spinner_sort_criterion);
        spinnerJobSort.setAdapter(spinnerArrayAdapter);
        spinnerJobSort.setSelection(Adapter.NO_SELECTION, false);
    }

    private void refreshList() {
        try {
            QueryBuilder<JobVacancy, Integer> queryBuilder = jobVacancyDao.queryBuilder();
            orderByCriterionFieldJob = preferences.getString(ConstantsHR.SORT_CRITERION_JOB_VACANCY_FIELD, getResources().getString(R.string.dbFieldJobDate));
            booleanAsc = Boolean.parseBoolean(preferences.getString(ConstantsHR.SORT_CRITERION_JOB_VACANCY_ASC_BOOLEAN, "false"));
            spinnerJobSort.setSelection(Integer.parseInt(preferences.getString(ConstantsHR.SPINNER_JOB_SORT_CHOSEN_POSITION, "0")));
            queryBuilder.orderBy(orderByCriterionFieldJob, booleanAsc);
            PreparedQuery<JobVacancy> preparedQuery = queryBuilder.prepare();
            List<JobVacancy> jobVacanciesListQuery = jobVacancyDao.query(preparedQuery);
            JobVacancyArrayAdapter adapterJobVacanciesList = new JobVacancyArrayAdapter(this, jobVacanciesListQuery);
            lvJobVacancies.setAdapter(adapterJobVacanciesList);
//            CustomAdapter customAdapter = new CustomAdapter(this.getApplicationContext(), jobVacanciesListQuery);
//            lvJobVacancies.setAdapter(customAdapter);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupListeners() {
        lvJobVacancies.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                jobVacancySelected = (JobVacancy) adapterView.getItemAtPosition(i);
                JobVacancyListDialog listDialog = new JobVacancyListDialog();
                Bundle bundle = new Bundle(1);
                bundle.putInt(ConstantsHR.JOB_VACANCY_SELECTED_ID, jobVacancySelected.getId());
                listDialog.setArguments(bundle);
                listDialog.show(getFragmentManager(), ConstantsHR.LIST_DIALOG);
                return true;
            }
        });
        floatingAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addJobVacancy();
            }
        });
        spinnerJobSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String chosenCriterion = adapterView.getItemAtPosition(i).toString();
                if (chosenCriterion.equals(getResources().getString(R.string.sortJobTitleAsc))) {
                    orderByCriterionFieldJob = getResources().getString(R.string.dbFieldJobTitle);
                    booleanAsc = true;
                    spinnerChosenPosition = i;
                } else if (chosenCriterion.equals(getResources().getString(R.string.sortJobTitleDesc))) {
                    orderByCriterionFieldJob = getResources().getString(R.string.dbFieldJobTitle);
                    booleanAsc = false;
                    spinnerChosenPosition = i;
                } else if (chosenCriterion.equals(getResources().getString(R.string.sortDateAsc))) {
                    orderByCriterionFieldJob = getResources().getString(R.string.dbFieldJobDate);
                    booleanAsc = true;
                    spinnerChosenPosition = i;
                } else if (chosenCriterion.equals(getResources().getString(R.string.sortDateDesc))) {
                    orderByCriterionFieldJob = getResources().getString(R.string.dbFieldJobDate);
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
        editor.putString(ConstantsHR.SORT_CRITERION_JOB_VACANCY_FIELD, orderByCriterionFieldJob);
        String stringValueBooleanAsc = String.valueOf(booleanAsc);
        editor.putString(ConstantsHR.SORT_CRITERION_JOB_VACANCY_ASC_BOOLEAN, stringValueBooleanAsc);
        editor.putString(ConstantsHR.SPINNER_JOB_SORT_CHOSEN_POSITION, spinnerChosenPosition.toString());
        editor.commit();
    }

    public void addJobVacancy() {
        Intent intent = new Intent(this, EditJobVacancyActivity.class);
        startActivity(intent);
    }

    public void editJobVacancy(Integer jobVacancySelectedId) {
        Intent intent = new Intent(this, EditJobVacancyActivity.class);
        intent.putExtra(ConstantsHR.JOB_VACANCY_SELECTED_ID, jobVacancySelectedId);
        finish();
        startActivity(intent);
    }

    public void viewJobVacancy(Integer jobVacancySelectedId) {
        Intent intent = new Intent(this, ViewJobVacancyActivity.class);
        intent.putExtra(ConstantsHR.JOB_VACANCY_SELECTED_ID, jobVacancySelectedId);
        finish();
        startActivity(intent);
    }

    public void deleteJobVacancy(Integer jobVacancySelectedId) {
        try {
            personDao.delete(jobVacancyDao.queryForId(jobVacancySelectedId).getPersons());
            jobVacancyDao.delete(jobVacancyDao.queryForId(jobVacancySelectedId));
            Toast.makeText(MainActivity.this, R.string.job_vacancy_deleted, Toast.LENGTH_SHORT).show();
            refreshList();
            onCreateOptionsMenu(menuEmpty);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllJobVacancies() {
        try {
            personDao.delete(personDao.queryForAll());
            jobVacancyDao.delete(jobVacancyDao.queryForAll());
            Toast.makeText(MainActivity.this, R.string.all_job_vacancies_deleted, Toast.LENGTH_SHORT).show();
            refreshList();
            onCreateOptionsMenu(menuEmpty);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showConfirmationDialogDeleteAllJobVacancies() {
        Bundle bundle = new Bundle(2);
        bundle.putString(ConstantsHR.TYPE, ConstantsHR.DELETE_ALL_JOB_VACANCIES);
        bundle.putString(ConstantsHR.WARNING_MESSAGE, getString(R.string.are_you_sure_delete_all_job));
        ConfirmationDialog cd = new ConfirmationDialog();
        cd.setArguments(bundle);
        cd.show(getFragmentManager(), ConstantsHR.WARNING_DIALOG);
    }
}
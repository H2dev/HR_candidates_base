package hr.dev.h2.hr_candidates;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import hr.dev.h2.hr_candidates.model.JobVacancy;
import hr.dev.h2.hr_candidates.model.Person;

/**
 * Created by Hrvoje on 20.11.2016..
 */

public class CandidatesDatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "candidates.db";
    private static final int DATABASE_VERSION = 5;

    private Dao<Person, Integer> personDao = null;
    private Dao<JobVacancy, Integer> jobVacancyDao = null;

    public CandidatesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Person.class);
            TableUtils.createTable(connectionSource, JobVacancy.class);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Dao<Person, Integer> getPersonDao() throws SQLException {
        if (personDao == null) {
            personDao = getDao(Person.class);
        }

        return personDao;
    }

    public Dao<JobVacancy, Integer> getJobVacancyDao() throws SQLException {
        if (jobVacancyDao == null) {
            jobVacancyDao = getDao(JobVacancy.class);
        }

        return jobVacancyDao;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int olderVersion, int newerVersion) {
        try {
            TableUtils.dropTable(connectionSource, Person.class, true);
            TableUtils.dropTable(connectionSource, JobVacancy.class, true);
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        personDao = null;
        jobVacancyDao = null;
        super.close();
    }
}

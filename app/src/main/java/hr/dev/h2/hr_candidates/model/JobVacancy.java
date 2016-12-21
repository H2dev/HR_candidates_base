package hr.dev.h2.hr_candidates.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Hrvoje on 22.11.2016..
 */

@DatabaseTable(tableName = "job_vacancy")
public class JobVacancy {

    @DatabaseField(columnName = "id", generatedId = true)
    private Integer id;
    @DatabaseField
    private String title;
    @DatabaseField
    private String jobAdEndDate;
    @DatabaseField
    private String description;
    @DatabaseField(dataType = DataType.BYTE_ARRAY)
    byte[] imageBytes;
    @ForeignCollectionField(eager = true)
    private ForeignCollection<Person> persons;

    public JobVacancy() {
        // ORMLite needs a no-arg constructor
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJobAdEndDate() {
        return jobAdEndDate;
    }

    public void setJobAdEndDate(String jobAdEndDate) {
        this.jobAdEndDate = jobAdEndDate;
    }

    public ForeignCollection<Person> getPersons() {
        return persons;
    }

    public void setPersons(ForeignCollection<Person> persons) {
        this.persons = persons;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }
}

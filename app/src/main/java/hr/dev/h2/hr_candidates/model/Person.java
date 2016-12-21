package hr.dev.h2.hr_candidates.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Hrvoje on 20.11.2016..
 */

@DatabaseTable(tableName = "person")
public class Person {

    public static final String JOB_VACANCY_ID = "jobVacancy_id";

    @DatabaseField(columnName = "id", generatedId = true)
    private Integer id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String surname;
    @DatabaseField
    private String dateOfBirth;
    @DatabaseField
    private String qualifications;
    @DatabaseField
    private String pastWorkExperience;
    @DatabaseField
    private String address;
    @DatabaseField
    private String telNum;
    @DatabaseField
    private String email;
    @DatabaseField
    private String interviewNotes;
    @DatabaseField(dataType = DataType.BYTE_ARRAY)
    byte[] imageBytes;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private JobVacancy jobVacancy;
    @DatabaseField
    private Float rating;


    public Person() {
        // ORMLite needs a no-arg constructor
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getQualifications() {
        return qualifications;
    }

    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }

    public String getPastWorkExperience() {
        return pastWorkExperience;
    }

    public void setPastWorkExperience(String pastWorkExperience) {
        this.pastWorkExperience = pastWorkExperience;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelNum() {
        return telNum;
    }

    public void setTelNum(String telNum) {
        this.telNum = telNum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInterviewNotes() {
        return interviewNotes;
    }

    public void setInterviewNotes(String interviewNotes) {
        this.interviewNotes = interviewNotes;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }

    public JobVacancy getJobVacancy() {
        return jobVacancy;
    }

    public void setJobVacancy(JobVacancy jobVacancy) {
        this.jobVacancy = jobVacancy;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }
}

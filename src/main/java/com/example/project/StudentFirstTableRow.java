package com.example.project;

// This class represent a row in the TableView of the Student Screen
public class StudentFirstTableRow {

    private String professorDetails; // "Name Surname"
    private String subject;
    private String value;
    private String date; // DD/MM/YYYY
    private String comment;

    public StudentFirstTableRow(){}
    public StudentFirstTableRow(String professorDetails, String subject, String value, String date, String comment){
        this.professorDetails = professorDetails;
        this.subject = subject;
        this.value = value;
        this.date = date;
        this.comment = comment;
    }

    // These getters are userd by the TableView object
    public String getProfessorDetails() {
        return professorDetails;
    }
    public String getSubject() {
        return subject;
    }
    public String getValue() {
        return value;
    }
    public String getDate() {
        return date;
    }
    public String getComment() {
        return comment;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

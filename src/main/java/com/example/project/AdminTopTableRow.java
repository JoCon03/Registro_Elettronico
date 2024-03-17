package com.example.project;

// This class represent a row in the TableView of the Admin Screen
// Each row contain the information of a Student
public class AdminTopTableRow extends AdminBottomTableRow {

    private String classroom;

    public AdminTopTableRow() {}
    public AdminTopTableRow(String username, String name, String surname, String classroom){
        super(username, name, surname);
        this.classroom = classroom;
    }

    public String getClassroom(){return classroom;}
    public void setClassroom(String classroom){ this.classroom = classroom; }

}

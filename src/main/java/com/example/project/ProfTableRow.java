package com.example.project;

import javafx.scene.control.CheckBox;
import java.util.ArrayList;

// This class represent a row in the TableView of the Professor Screen
// They are gived grouped by classroom, and each contains a student's performance.
public class ProfTableRow {

    private String studentUsername;
    private String personalDetails; // "Name Surname"
    private String teaching; // "%d"

    // This is what the table print
    private CheckBox absentCheckBox;
    private final StringBuilder formattedGrades;
    private String average;

    private final ArrayList<Double> rawGrades;
    private final ArrayList<String> dates;
    private final ArrayList<String> comments;

    public ProfTableRow(){
        this.average = "0.00";
        this.rawGrades = new ArrayList<>();
        this.formattedGrades = new StringBuilder();
        this.dates = new ArrayList<>();
        this.comments = new ArrayList<>();
    }
    public ProfTableRow(String studentUsername, String teaching){
        this.studentUsername = studentUsername;
        this.personalDetails = Model.getInstance().getFullName(studentUsername);
        this.teaching = teaching;
        this.rawGrades = new ArrayList<>();
        this.formattedGrades = new StringBuilder();
        this.average = "0.00";
        this.dates = new ArrayList<>();
        this.comments = new ArrayList<>();
    }

    public void addGrade(String value, String date, String comment){
        Double convertedValue = Double.valueOf(value);
        rawGrades.add(convertedValue);
        formattedGrades.append("%.2f".formatted(convertedValue).replace(",", ".")).append(" ");
        computeAverage();
        dates.add(date);
        comments.add(comment);
    }

    public void removeGrade(int index){
        rawGrades.remove(index);
        formattedGrades.setLength(0);
        for(Double grade : rawGrades) formattedGrades.append("%.2f".formatted(grade).replace(",", ".")).append(" ");
        computeAverage();
        dates.remove(index);
        comments.remove(index);
    }

    // Some of these getters are used by the TableView object although intellij dispaly them in gray
    public String getStudentUsername() {return studentUsername;}
    public String getTeaching() {return teaching;}
    public String getPersonalDetails(){return personalDetails;}
    public CheckBox getAbsentCheckBox(){return absentCheckBox;}
    public ArrayList<Double> getRawGrades(){return rawGrades;}
    public String getFormattedGrades() {return formattedGrades.toString();}
    public String getAverage(){return average;}
    public ArrayList<String> getDates(){return dates;}
    public ArrayList<String> getComments(){return comments;}

    public void setAbsentCheckBox(CheckBox absentCheckBox, boolean isAbsent){
        this.absentCheckBox = absentCheckBox;
        this.absentCheckBox.setSelected(isAbsent);
    }

    private void computeAverage(){
        Double x = 0.0;
        if(rawGrades.isEmpty()) return;
        for(Double grade : this.rawGrades)
            x += grade;
        x /= rawGrades.size();
        average = "%.2f".formatted(x).replace(",",".");
    }

}

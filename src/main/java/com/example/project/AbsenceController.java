package com.example.project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

import static com.example.project.ProfController.*;

public class AbsenceController extends ProfController implements Initializable {

    @FXML
    public TableView<AbsencesTableRow> attendanceTable;
    @FXML
    public Label profName;
    @FXML
    public TextField studentNameTextField;
    @FXML
    public DatePicker attendancePicker;
    @FXML
    public Button addAbsenceButton;
    @FXML
    public Button removeAbsenceButton;
    @FXML
    public TableColumn<AbsencesTableRow,String> studentColumn;
    @FXML
    public TableColumn<AbsencesTableRow,String> absencesColumn;
    @FXML
    public AnchorPane pane;
    @FXML
    public Label studentNameLabel;
    @FXML
    public Label dateLabel;
    @FXML
    public Label professorLabel;
    @FXML
    public TabPane absencesPane;

    public String currentTabName;
    public Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    public Alert informationAlert = new Alert(Alert.AlertType.INFORMATION);
    public ArrayList<Label> labels = new ArrayList<Label>();
    public Model model = Model.getInstance();
    public ArrayList<AbsencesTableRow> students = model.getAbsencesTableRows();
    private boolean pickerFocused = false;
    public DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        studentColumn.setCellValueFactory(new PropertyValueFactory<>("studentUsername"));
        absencesColumn.setCellValueFactory(new PropertyValueFactory<>("studentAbsences"));
        profName.setText(View.username_logged);
        attendancePicker.setValue(LocalDate.now());

        set_name_prof();
        create_tab();

        if (!absencesPane.getTabs().isEmpty()) {
            absencesPane.getSelectionModel().selectFirst();
            set_table_withclass();
        }

        // aggiungo un listener per catturare l'evento di cambio selezione tab
        absencesPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {
                set_table_withclass();
                String tabTitle = absencesPane.getSelectionModel().getSelectedItem().getText();
            }
        });
        attendancePicker.setConverter(new javafx.util.StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;}}});
        labels.add(studentNameLabel);
        labels.add(dateLabel);
        labels.add(professorLabel);
        labels.add(profName);
        errorAlert.getDialogPane().setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        errorAlert.setHeaderText("ERRORE!");
        errorAlert.setTitle("ERROR!");
        informationAlert.getDialogPane().setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        String theme = Model.getInstance().getTheme();
        switch (theme){
            case "Light" -> setAttendenceScreenLight();
            case "Dark" -> setAttendenceScreenDark();
            case "Purple" -> setAttendenceScreenPurple();
            case "Blue" -> setAttendenceScreenBlue();
            case "Green" -> setAttendenceScreenGreen();
            case "Red" -> setAttendenceScreenRed();
            case "Yellow" -> setAttendenceScreenYellow();
        }

        attendancePicker.setOnKeyReleased(event -> {
            if((event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) && !pickerFocused && !attendancePicker.isShowing()){
                studentNameTextField.requestFocus();
            }
            pickerFocused = false;
        });

    }

    public void set_name_prof() {
        String username_prof = View.username_prof;
        Model model = Model.getInstance();
        profName.setText(model.getFullName(username_prof));
        profName.getStyleClass().add("label-style");
    }


    public boolean checkDuplicate(String classroom, ArrayList<String> teachings){
        for(String s : teachings){
            if(classroom.equals(s)){
                return true;
            }
        }
        return false;
    }

    public void create_tab () {
        Model model = Model.getInstance();
        ArrayList<String> teachings = model.getTeachingsOf(View.username_prof);
        // Creazione delle tab dinamiche e aggiunta a absencesPane
        ArrayList<String> used = new ArrayList<>();
        for (String teaching : teachings) {
            String profClass = teaching.substring(teaching.lastIndexOf(" ")+1);
            if(!checkDuplicate(profClass,used)){
                Tab tab = new Tab();
                tab.setText(profClass);
                absencesPane.getTabs().add(tab);
                used.add(profClass);
            }

        }
    }

    /*public String split_tabstring_class (String string){
        String classroom = "";
        for (int i = 0; i < string.length(); i++) {
            char character = string.charAt(i);
            classroom += character;
            if (character == ' ') classroom = "";
            //prende la classroom per mandarla ad una funzione che in base al tab stampa l'elenco degli alunni
        }
        return classroom;
    }
    public String split_tabstring_subject(String string) {
        String subject = "";
        for (int i = 0; i < string.length(); i++) {
            char character = string.charAt(i);
            if(character!=' ') subject+=character;
            else break;
        }
        return subject;
    }*/
    public void set_table_withclass () {
        //ArrayList<AbsencesTableRow> usedStudents = new ArrayList<>();
        //boolean found = false;
        attendanceTable.getItems().clear();
        String tabName = absencesPane.getSelectionModel().getSelectedItem().getText();
        currentTabName = tabName;
        /*students = Model.getInstance().getAbsencesTableRows();
        //sortStudents();
        for (AbsencesTableRow absencesTableRow : students) {
            for (AbsencesTableRow absencesTableRow1 : usedStudents) {
                if (absencesTableRow1.equals(absencesTableRow)) {
                    found = true;
                    break;
                }
            }
        if (!found) {*/
        ArrayList<AbsencesTableRow> students = model.getAbsencesTableRows(tabName);

        for (AbsencesTableRow absencesTableRow : students) {
            /*String newAbsenceString;
            newAbsenceString = absencesTableRow.getStudentAbsences().replace('-', '/');
            //if (tabName.equals(absencesTableRow.getStudentClassroom())) {*/
            attendanceTable.getItems().add(absencesTableRow);
            //}
        }
        attendanceTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        attendanceTable.getSelectionModel().setCellSelectionEnabled(true);
    }

    public void sortStudents(){
        ArrayList<String> names = new ArrayList<>();
        ArrayList<AbsencesTableRow> sortedStudents = new ArrayList<>();
        for(AbsencesTableRow absencesTableRow : students) {
            names.add(absencesTableRow.getStudentUsername());
        }
        Collections.sort(names);
        for(String name : names){
            for(AbsencesTableRow absencesTableRow : students){
                if(name.equals(absencesTableRow.getStudentUsername())){
                    sortedStudents.add(absencesTableRow);
                }
            }
        }
        students=sortedStudents;
    }

    public void addAbsences(ActionEvent actionEvent) {
        boolean find = false;
        if (studentNameTextField.getText().isEmpty()) {
            errorAlert.setContentText("DEVI SELEZIONARE UNO STUDENTE A CUI INSERIRE L'ASSENZA!");
            errorAlert.show();
        }
        else {
            for (AbsencesTableRow absencesTableRow : students) {
                String studentUsername = absencesTableRow.getStudentUsername();
                if (studentUsername.equals(studentNameTextField.getText())) {
                    String absence = attendancePicker.getEditor().getText();
                    if (absencesTableRow.getStudentAbsences().contains(absence)) {
                        System.out.println(absencesTableRow.getStudentUsername() + " " + absencesTableRow.getStudentAbsences());
                        errorAlert.setContentText("LO STUDENTE RISULTA GIA' ASSENTE IN DATA " + absence);
                        errorAlert.show();
                        find = true;
                        break;
                    } else {
                        absencesTableRow.addAbsence(absence);
                        LocalDate s = LocalDate.parse(absence, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        //dateFormatter.format(s);
                        //dateFormatter.format(s);
                        model.insertAbsence(studentUsername, s.toString());
                        attendanceTable.getItems().clear();
                        ArrayList<AbsencesTableRow> sium = model.getAbsencesTableRows(currentTabName);
                        for (AbsencesTableRow absencesTableRow1 : sium) {
                            attendanceTable.getItems().add(absencesTableRow1);
                        }
                        String newDate = LocalDate.now().format(dateFormatter);

                        //Vengono aggiunte due volte le assenze!!!

                    if(absence.equals(newDate)){
                        model.setAbsence(studentUsername, true);
                        ProfTableRow profTableRow = new ProfTableRow(studentUsername, currentTabName);
                        profTableRow.setAbsentCheckBox(new CheckBox(), true );
                    }
                        find = true;
                        studentNameTextField.setText("");
                        break;
                    }
                }
            }
        }
        if(!find && !studentNameTextField.getText().isEmpty()){
            System.out.println(studentNameTextField.getText() + " " + find);
            errorAlert.setContentText("LO STUDENTE NON E' PRESENTE NEL DATABASE!");
            errorAlert.show();
        }
    }
    public void removeAbsences(ActionEvent actionEvent) {
        boolean find = false;
        boolean absencesFound = true;
        if (studentNameTextField.getText().isEmpty()) {
            errorAlert.setContentText("DEVI SELEZIONARE UNO STUDENTE A CUI RIMUOVERE L'ASSENZA!");
            errorAlert.show();
        }
        else {
            for (AbsencesTableRow absencesTableRow : students) {
                String studentUsername = absencesTableRow.getStudentUsername();
                String studentAbsences = absencesTableRow.getStudentAbsences();
                String absence = attendancePicker.getEditor().getText();

                if (studentUsername.equals(studentNameTextField.getText()) && studentAbsences.contains(absence)) {

                    String[] dates = absencesTableRow.getStudentAbsences().split("\\s+");
                    int index = -1;
                    for (int i = 0; i < dates.length; i++) {
                        if (dates[i].equals(absence)) {
                            index = i;
                            break;
                        }
                    }
                    absencesTableRow.removeAbsence(index);
                    LocalDate s = LocalDate.parse(absence, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    //dateFormatter.format(s);
                    //dateFormatter.format(s);
                    model.removeAbsence(studentUsername, s.toString());
                    ProfTableRow profTableRow = new ProfTableRow(studentUsername, currentTabName);
                    profTableRow.setAbsentCheckBox(new CheckBox(), false );
                    attendanceTable.getItems().clear();
                    ArrayList<AbsencesTableRow> sium = model.getAbsencesTableRows(currentTabName);
                    for (AbsencesTableRow absencesTableRow1 : sium) {
                        attendanceTable.getItems().add(absencesTableRow1);
                    }
                    find = true;
                }
                else if (studentUsername.equals(studentNameTextField.getText()) && !studentAbsences.contains(absence)) {
                    errorAlert.setContentText("LO STUDENTE NON RISULTA ASSENTE IN DATA " + absence);
                    errorAlert.show();
                    absencesFound = false;
                }
            }
        }
        if(!find && !studentNameTextField.getText().isEmpty() && absencesFound){
            errorAlert.setContentText("LO STUDENTE NON E' PRESENTE NEL DATABASE!");
            errorAlert.show();
        }
    }

    public void changeToPicker(KeyEvent event) {
        KeyCode code = event.getCode();
        if((code==KeyCode.UP || code == KeyCode.DOWN)){
            attendancePicker.requestFocus();
            pickerFocused = true;
        }
        else if(code.equals(KeyCode.ENTER)){
            addAbsences(null);
        }
    }

    public void submitFromPicker(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            addAbsences(null);
        }
    }
    public void click(MouseEvent mouseEvent) {
        try {
            TablePosition g = attendanceTable.getSelectionModel().getSelectedCells().get(0);    //Prende la posizione dell'elemento selezionato
            int row = g.getRow();                                                         //Prende la riga della TablePosition
            AbsencesTableRow absencesTableRow = attendanceTable.getItems().get(row);          //Pone studentWithGrades = oggetto selezionato
            TableColumn tableColumn = g.getTableColumn();
            if(tableColumn == studentColumn){
                studentNameTextField.setText(absencesTableRow.getStudentUsername());
                studentNameTextField.requestFocus();
            }
            else if(tableColumn == absencesColumn){
                informationAlert.setTitle("Assenze di " + absencesTableRow.getStudentUsername());
                informationAlert.setHeaderText("Date delle Assenze:");
                informationAlert.setContentText(absencesTableRow.getStudentAbsences());
                informationAlert.show();

            }
            attendanceTable.getSelectionModel().clearSelection();
        } catch (RuntimeException ignored) {
        }
    }

    public void instruction(ActionEvent actionEvent) {

        informationAlert.setTitle("ISTRUZIONI PER L'USO");
        informationAlert.setHeaderText("Informazioni:");
        informationAlert.setContentText("In questa schermata puoi gestire le assenze dei tuoi alunni. Dopo aver compilato" +
                " i campi, premi il bottone corrispondente alla funzione che vuoi, e l'assenza verrà aggiunta" +
                " o rimossa in base al bottone premuto. Puoi cliccare sull'username di uno studente per farlo" +
                " comparire direttamente nell'area di testo corrispondente. Infine puoi cliccare sulle celle " +
                " delle assenze degli alunni per vedere tutte le loro assenze.");
        informationAlert.show();
    }
    public void setAttendenceScreenLight(){
        attendanceTable.setStyle("-fx-background-color: #f2f1f0; -fx-table-cell-border-color: #d6d6d6; -fx-selection-bar: cornflowerblue ");
        for(Label l : labels){
            l.setStyle("-fx-text-fill: black");
        }
        addAbsenceButton.setStyle("-fx-background-color: blue ; -fx-text-fill: white ; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 1;");
        removeAbsenceButton.setStyle("-fx-background-color: blue ; -fx-text-fill: white ; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 1;");
        pane.setStyle("-fx-background-color: whitesmoke");
    }

    public void setAttendenceScreenDark(){
        attendanceTable.setStyle("-fx-control-inner-background: #202020; -fx-table-cell-border-color: white; -fx-selection-bar: dimgrey ");
        for(Label l : labels){
            l.setStyle("-fx-text-fill: white");
        }
        addAbsenceButton.setStyle("-fx-background-color: blue ; -fx-text-fill: white ; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 1;");
        removeAbsenceButton.setStyle("-fx-background-color: blue ; -fx-text-fill: white ; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 1;");
        pane.setStyle("-fx-background-color: #1a1a1a");
    }

    public void setAttendenceScreenPurple(){
        attendanceTable.setStyle("-fx-control-inner-background: #690099; -fx-table-cell-border-color: white; -fx-selection-bar: #230033 ");
        for(Label l : labels){
            l.setStyle("-fx-text-fill: white");
        }
        addAbsenceButton.setStyle("-fx-background-color: gold ; -fx-text-fill: black ; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 1;");
        removeAbsenceButton.setStyle("-fx-background-color: gold ; -fx-text-fill: black ; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 1;");
        pane.setStyle("-fx-background-color: #690099");
    }

    public void setAttendenceScreenBlue(){
        attendanceTable.setStyle("-fx-control-inner-background: #000099; -fx-table-cell-border-color: white; -fx-selection-bar: slategrey ");
        for(Label l : labels){
            l.setStyle("-fx-text-fill: white");
        }
        addAbsenceButton.setStyle("-fx-background-color: Darkgray ; -fx-text-fill: black ; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 1;");
        removeAbsenceButton.setStyle("-fx-background-color: Darkgray ; -fx-text-fill: black ; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 1;");
        pane.setStyle("-fx-background-color: #000099");
    }

    public void setAttendenceScreenGreen(){
        attendanceTable.setStyle("-fx-control-inner-background: #0b4d12; -fx-table-cell-border-color: white; -fx-selection-bar: #4d1d0b");
        for(Label l : labels){
            l.setStyle("-fx-text-fill: white");
        }
        addAbsenceButton.setStyle("-fx-background-color: saddleBrown; -fx-text-fill: white ; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 1;");
        removeAbsenceButton.setStyle("-fx-background-color: saddleBrown; -fx-text-fill: white ; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 1;");
        pane.setStyle("-fx-background-color: #0b4d12");
    }

    public void setAttendenceScreenRed(){
        attendanceTable.setStyle("-fx-control-inner-background: FireBrick; -fx-table-cell-border-color: white; -fx-selection-bar: darkcyan ");
        for(Label l : labels){
            l.setStyle("-fx-text-fill: white");
        }
        addAbsenceButton.setStyle("-fx-background-color: Turquoise; -fx-text-fill: white ; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 1;");
        removeAbsenceButton.setStyle("-fx-background-color: Turquoise; -fx-text-fill: white ; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 1;");
        pane.setStyle("-fx-background-color: FireBrick");
    }

    public void setAttendenceScreenYellow(){
        attendanceTable.setStyle("-fx-control-inner-background: goldenrod; -fx-table-cell-border-color: black; -fx-selection-bar:yellowgreen ");
        for(Label l : labels){
            l.setStyle("-fx-text-fill: black");
        }
        addAbsenceButton.setStyle("-fx-background-color: green ; -fx-text-fill: white ; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 1;");
        removeAbsenceButton.setStyle("-fx-background-color: green ; -fx-text-fill: white ; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 1;");
        pane.setStyle("-fx-background-color: goldenrod");
    }








    /*public void changeToField(KeyEvent event){
        KeyCode code = event.getCode();
        if(code.equals(KeyCode.UP) || code.equals(KeyCode.DOWN)){
            studentNameTextField.requestFocus();
        }
        else if(code.equals(KeyCode.ENTER)){
            addAbsences(null);
        }
    }*/
}

package com.example.project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.example.project.View.*;

public class ProfController implements Initializable {
    @FXML
    public Label studentLabel;
    @FXML
    public Label dateLabel;
    @FXML
    public Label gradeLabel;
    @FXML
    public Label commentLabel;
    @FXML
    public Label profName;
    @FXML
    public TabPane TabMC;
    @FXML
    public TableView<ProfTableRow> profTable = new TableView<>();
    @FXML
    public TableColumn<ProfTableRow, String> studentColumn;
    @FXML
    public TableColumn<ProfTableRow, CheckBox> attendancesColumn;
    @FXML
    public TableColumn<ProfTableRow, String> gradesColumn;
    @FXML
    public TableColumn<ProfTableRow, String> averageColumn;
    @FXML
    public TextField studentTextField;
    @FXML
    public TextField gradeTextField;
    @FXML
    public DatePicker picker;
    @FXML
    public TextArea commentTextField;
    public String subject;
    public DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public ArrayList<ProfTableRow> students = new ArrayList<>();
    public boolean pickerFocused;
    private String teaching;

    public void initialize(URL location, ResourceBundle resource) {
        set_name_prof();
        create_tab();
        picker.setValue(LocalDate.now());

        // Imposta il formato di visualizzazione della data nel DatePicker
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        picker.setConverter(new javafx.util.StringConverter<LocalDate>() {
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
        // imposta il primo tab come selezionato
        if (!TabMC.getTabs().isEmpty()) {
            TabMC.getSelectionModel().selectFirst();
            set_table_withclass();
        }

        // aggiungo un listener per catturare l'evento di cambio selezione tab
        TabMC.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {
                set_table_withclass();
                String tabTitle = TabMC.getSelectionModel().getSelectedItem().getText();
                subject = tabTitle.substring(0,tabTitle.lastIndexOf(" "));
            }
        });
        //Setto da qui gli eventi di cambio focus al picker. Metto un keyReleased anzichè un keyPressed poichè
        //quando premi la freccia su sul picker va solo a capo testo e esso non risponde
        //all'evento che voglio settare.Stessa cosa per la freccia giù, solo che va a fine testo del picker.
        picker.setOnKeyReleased(event -> {
            if(event.getCode() == KeyCode.UP && picker.isFocused() && !pickerFocused && !picker.isShowing()){
                gradeTextField.requestFocus();
            } else if (event.getCode() == KeyCode.DOWN && picker.isFocused() && !pickerFocused && !picker.isShowing()) {
                commentTextField.requestFocus();
            }
            pickerFocused=false;
        });
        TextFormatter<String> textFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            // Verificare se la lunghezza supera il limite
            if (newText.length() > 140) {
                // Se sì, impedire la modifica
                return null;
            }

            // Altrimenti, consentire la modifica
            return change;
        });
        commentTextField.setTextFormatter(textFormatter);
    }

    public void set_name_prof() {
        String username_prof = View.username_prof;
        Model model = Model.getInstance();
        profName.setText("PROFESSORE:   " + model.getFullName(username_prof));
        profName.getStyleClass().add("label-style");
    }




    public void create_tab () {
        Model model = Model.getInstance();
        ArrayList<String> tabTitles = model.getTeachingsOf(View.username_prof);
        // Creazione delle tab dinamiche e aggiunta a TabMC
        for (String title : tabTitles) {
            Tab tab = new Tab();
            tab.setText(title);
            TabMC.getTabs().add(tab);
            }
    }
    public String split_tabstring_class (String string){
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
    }
    public void set_table_withclass () {
        profTable.getItems().clear();
        String tabName = TabMC.getSelectionModel().getSelectedItem().getText();
        String select_class = split_tabstring_class(tabName);
        String select_subject=split_tabstring_subject(tabName);
        teaching = Model.getInstance().getTeachingId(View.username_prof, select_class, select_subject);
        students = Model.getInstance().getProfTableRows(teaching);
        studentColumn.setCellValueFactory(new PropertyValueFactory<>("personalDetails"));
        attendancesColumn.setCellValueFactory(new PropertyValueFactory<>("absentCheckBox"));
        gradesColumn.setCellValueFactory(new PropertyValueFactory<>("formattedGrades"));
        averageColumn.setCellValueFactory(new PropertyValueFactory<>("average"));
        averageColumn.setCellFactory(column -> new TableCell<ProfTableRow, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle(""); // Resetta eventuali stili precedenti
                } else {
                    setText(item);

                    // Converte il valore del voto in un numero
                    try {
                        double average = Double.parseDouble(item);
                        // Imposta lo stile in base al valore del voto
                        if (average < 6.00) {
                            setStyle("-fx-background-color: tomato; -fx-text-fill: white;");
                        } else {
                            setStyle("-fx-background-color: limegreen; -fx-text-fill: white;");
                        }
                    } catch (NumberFormatException e) {
                        // Gestisci l'eccezione se il valore del voto non è un numero valido
                    }
                }
            }
        });
        attendancesColumn.setSortable(false);
        sortStudents();
        for (ProfTableRow student : students) {
            String studentUsername = student.getStudentUsername();
            CheckBox checkBox = student.getAbsentCheckBox();
            student.getAbsentCheckBox().setOnAction(actionEvent -> {
                Model.getInstance().setAbsence(studentUsername, checkBox.isSelected());
            });
            profTable.getItems().add(student);
        }
        profTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        profTable.getSelectionModel().setCellSelectionEnabled(true);
    }
    public void sortStudents(){
        ArrayList<String> names = new ArrayList<>();
        ArrayList<ProfTableRow> sortedStudents = new ArrayList<>();
        for(ProfTableRow student : students) {
            names.add(student.getPersonalDetails());
        }
        Collections.sort(names);
        for(String name : names){
            for(ProfTableRow student : students){
                if(name.equals(student.getPersonalDetails())){
                    sortedStudents.add(student);
                }
            }
        }
        students=sortedStudents;
    }
    public void save(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        boolean find = false;
        int index = 0;
        for (int i = 0; i < profTable.getItems().size(); i++) {
            if (studentTextField.getText().equals(studentColumn.getCellObservableValue(i).getValue())) {
                find = true;
                index = i;
                break;
            }
        }
        if(picker.getValue()==null){
            alert.setHeaderText("INSERISCI LA DATA IN CUI HAI CARICATO IL VOTO");
            alert.show();
        }
        else if (find) {
            if (gradeTextField.getText().matches("(\\d(\\.\\d{1,2})?)|10|10\\.00|10\\.0")){
                ProfTableRow profTableRow = profTable.getItems().get(index);
                String date = picker.getEditor().getText();
                try{
                    picker.setValue(LocalDate.parse(date, formatter));
                    String grade = gradeTextField.getText();
                    if(grade.length()==1){
                        grade+=".00";
                    } else if (grade.length()==3) {
                        grade+="0";
                    }
                    if(Model.getInstance().insertGrade(students.get(index).getStudentUsername(), teaching, picker.getValue().toString(), commentTextField.getText(),grade))
                        profTableRow.addGrade( grade, picker.getValue().toString(), commentTextField.getText() );
                    profTable.refresh();
                    studentTextField.setText("");
                    gradeTextField.setText("");
                    picker.setValue(LocalDate.now());
                    commentTextField.setText("");
                }
                catch (RuntimeException e){
                    alert.setHeaderText("INSERISCI UNA DATA VALIDA!");
                    alert.show();
                }
            }

            else{
                alert.setHeaderText("IL VOTO DEVE ESSERE O INTERO O COSTITUITO DA UNA CIFRA SEGUITA DA UN PUNTO SEGUITO AL MASSIMO DA DUE CIFRE DECIMALI!!!");
                alert.setTitle("ERROR");
                alert.show();
            }

        }
        else{
            if(studentTextField.getText().isEmpty()){
                alert.setHeaderText("ERRORE! SE VUOI CARICARE UN VOTO DEVI PRIMA INSERIRE LO STUDENTE A CUI CARICARLO!!!");
                alert.setTitle("ERROR");
                alert.show();
            }
            else{
                alert.setHeaderText("ERRORE! STUDENTE NON TROVATO!!!");
                alert.setTitle("ERROR");
                alert.show();
            }
        }
    }


    public void remove(ActionEvent actionEvent){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        boolean studentfound = false;
        int index = 0;
        for (int i = 0; i < profTable.getItems().size(); i++) {

            if (studentTextField.getText().equals(studentColumn.getCellObservableValue(i).getValue())) {
                studentfound = true;
                index = i;
                break;
            }
        }
        if (studentfound) {
            if (gradeTextField.getText().matches("(\\d(\\.\\d{1,2})?)|10|10\\.0|10\\.00")){
                ProfTableRow profTableRow = students.get(students.indexOf(profTable.getItems().get(index)));              //Studente a cui rimuovere il voto
                ArrayList<String> dates = profTableRow.getDates();
                ArrayList<Double> studentGrades = profTableRow.getRawGrades();
                try{
                picker.setValue(LocalDate.parse(picker.getEditor().getText(), formatter));
                    for(int i = 0; i < dates.size(); i++){
                        if(picker.getValue().toString().equals(dates.get(i)) && studentGrades.get(i).equals(Double.valueOf(gradeTextField.getText()))){     //For per trovare la data in cui è stato inserito il voto(deve corrispondere alla data inserita nel picker)
                            //a data i-esima corrisponde voto i-esimo, quindi se il voto i-esimo è quello inserito nel textfield, si fa roba
                            Model.getInstance().removeGrade(profTableRow.getStudentUsername(), teaching, picker.getValue().toString(),gradeTextField.getText());
                            profTableRow.removeGrade(i); // Una volta trovato il voto i-esimo da rimuovere, lo si rimuove
                            profTable.refresh();                                                            //Si ricarica la tabella con i nuovi valori che cambiano anche nella stringa voti
                            studentTextField.setText("");
                            gradeTextField.setText("");
                            picker.setValue(LocalDate.now());                        //Reimposta la data a quella corrente
                            commentTextField.setText("");
                            break;
                        }
                        if(i==dates.size()-1){
                            alert.setTitle("Error");
                            String[] dateParts = picker.getValue().toString().split("-");
                            String date = dateParts[2] + "/" + dateParts[1] + "/" + dateParts[0];
                            alert.setHeaderText("Non hai inserito questo voto allo studente " + studentTextField.getText() + " in data " + date);
                            alert.show();
                        }
                    }
                }
                catch (RuntimeException e){
                    alert.setHeaderText("INSERISCI UNA DATA VALIDA!");
                    alert.show();
                }
            }

            else{
                alert.setHeaderText("IL VOTO DEVE ESSERE O INTERO O COSTITUITO DA UNA CIFRA SEGUITA DA UN PUNTO SEGUITO AL MASSIMO DA DUE CIFRE DECIMALI!!!");
                alert.setTitle("ERROR");
                alert.show();
            }
        }
        else{
            if(studentTextField.getText().isEmpty()){
                alert.setHeaderText("ERRORE! SE VUOI RIMUOVERE UN VOTO DEVI PRIMA INSERIRE LO STUDENTE A CUI RIMUOVERLO!!!");
                alert.setTitle("ERROR");
                alert.show();
            }
            else{
                alert.setHeaderText("ERRORE! STUDENTE NON TROVATO!!!");
                alert.setTitle("ERROR");
                alert.show();
            }
        }
        }
    public void click(MouseEvent mouseEvent) {
        try{
        TablePosition g = profTable.getSelectionModel().getSelectedCells().get(0);    //Prende la posizione dell'elemento selezionato
        int row = g.getRow();                                                         //Prende la riga della TablePosition
        ProfTableRow studentWithGrades = profTable.getItems().get(row);          //Pone studentWithGrades = oggetto selezionato
        TableColumn tableColumn = g.getTableColumn();                                 //Prende la colonna della TablePosition
        if (tableColumn == gradesColumn) {                                              //Se la colonna è quella dei voti
            Alert alert = new Alert(Alert.AlertType.INFORMATION);

            alert.getDialogPane().setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            alert.setTitle("Voti dello studente " + studentWithGrades.getPersonalDetails());    //Aggiunge alla stringa le generalità dello studente selezionato
            if (studentWithGrades.getRawGrades().isEmpty()){
                alert.setHeaderText("LO STUDENTE AL MOMENTO NON HA ALCUN VOTO");
                alert.show();
            }
            else{
                ArrayList<Double> grades = studentWithGrades.getRawGrades();
                String s = "";
                for(int i = 0; i<grades.size(); i++){
                    String grade = grades.get(i).toString();
                    if(grade.length()==3){
                        s+=grade+"0   ";
                    }
                    else {
                        s += grades.get(i).toString() + "   ";                         //Voto i-esimo studente selezionato
                    }
                    String[] dateParts = studentWithGrades.getDates().get(i).split("-");
                    s+= dateParts[2] + "/" + dateParts[1] + "/" + dateParts[0] + "   ";            //Data voto i-esimo
                    s+= studentWithGrades.getComments().get(i) + "\n";}         //Commento relativo al voto i-esimo
                alert.setHeaderText(s);
                alert.show();
            }
        }
        if(tableColumn == studentColumn){
            studentTextField.setText(studentWithGrades.getPersonalDetails());
            studentTextField.requestFocus();
        }
            profTable.getSelectionModel().select(null);
    }
        catch (RuntimeException e) {
        }            //Se cliccavo sui nomi delle colonne dava errore perchè non hanno una
                                                //effettiva TablePosition valida...
    }

    public void Help_Professor(ActionEvent actionEvent) {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.getDialogPane().setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        info.setTitle("ISTRUZIONI PER L'USO");
        info.setHeaderText("BENVENUTO NELLA SCHERMATA PROFESSORE!");
        info.setContentText("Qui potrai inserire/rimuovere assenze e voti ai tuoi alunni." +
                "Per inserire/rimuovere un voto ti basterà selezionare la materia-classe sopra la tabella e compilare" +
                " le apposite aree di testo sulla destra, quindi cliccare sul pulsante che vuoi in basso" +
                " a destra, oppure invio se vuoi aggiungere." +
                " Puoi cliccare il nome di un alunno nella tabella per far comparire direttamente il suo nome" +
                " nella relativa area di testo, e muoverti tra le varie aree di testo con le freccette su e giù." +
                " Inoltre puoi modificare il tema della tua schermata, vedere le statistiche degli alunni della classe selezionata " +
                "o fare il logut dai tasti in alto a sinistra.");
        info.show();


    }

    private void Professor_Theme(String theme){
        Model.getInstance().updateTheme(username_prof, theme);
        Scene scene = currentStage.getScene();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/application/" + theme + "Professor.css").toExternalForm());
    }

    public void Professor_Light(ActionEvent actionEvent) {
        Professor_Theme("Light");
    }

    public void Professor_Dark(ActionEvent actionEvent) {
        Professor_Theme("Dark");
    }
    public void Professor_Purple(ActionEvent actionEvent) {
        Professor_Theme("Purple");
    }
    public void Professor_Blue(ActionEvent actionEvent) {
        Professor_Theme("Blue");
    }
    public void Professor_Green(ActionEvent actionEvent) {Professor_Theme("Green");}
    public void Professor_Red(ActionEvent actionEvent) {Professor_Theme("Red");}
    public void Professor_Yellow(ActionEvent actionEvent) {Professor_Theme("Yellow");}
    public void focusAndSubmitEvents(KeyEvent event) {
        switch (event.getCode()){
            case UP -> {
                if (studentTextField.isFocused()){
                    commentTextField.requestFocus();
                }
                else if(gradeTextField.isFocused()){
                    studentTextField.requestFocus();
                }
                else if(commentTextField.isFocused()){
                    picker.requestFocus();
                    pickerFocused = true;
                }
            }
            case DOWN -> {
                if (studentTextField.isFocused()){
                    gradeTextField.requestFocus();
                }
                else if(gradeTextField.isFocused()){
                    picker.requestFocus();
                    pickerFocused=true;
                }
                else if(commentTextField.isFocused()){
                    studentTextField.requestFocus();
                }
            }
            case ENTER -> {
                save(null);
            }
        }
    }

    public void Professor_logout(ActionEvent actionEvent) {
        try {
            // Chiudi la finestra corrente (interfaccia studente)
            currentStage.close();

            // Richiama il metodo start per aprire la schermata di login
            new View().start(new Stage());
        } catch (IOException e) {
            // Gestione dell'eccezione
        }
    }


    public void showAbsencesChart(ActionEvent actionEvent) {
        Model model = Model.getInstance();
        PieChart pieChart = new PieChart();
        ArrayList<String> numberOfAbsences = new ArrayList<>();
        pieChart.setTitle("Assenze degli alunni");
        for(ProfTableRow profTableRow : profTable.getItems()){
            String username = profTableRow.getStudentUsername();
            String absences = model.getAbsences(username);
            if(!numberOfAbsences.contains(absences)){
                numberOfAbsences.add(absences);
            }
        }
        for(String string : numberOfAbsences){
            Double cont = (double) 0;
            for(ProfTableRow profTableRow : profTable.getItems()){
                String username = profTableRow.getStudentUsername();
                String absences = model.getAbsences(username);
                if (absences.equals(string)){
                    cont+=1;
                }
            }
            int size = profTable.getItems().size();
            Double percent = ((cont/size)*100);
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            decimalFormat.setDecimalSeparatorAlwaysShown(false);
            // Applicare il formato e ottenere la stringa arrotondata
            String formatted = decimalFormat.format(percent);

            // Convertire la stringa risultato in un double
            String title = "alunni con " + string + " assenze:" + formatted + "%";
            PieChart.Data slice = new PieChart.Data(title, percent);
            pieChart.getData().add(slice);
        }
        // Creare il dialogo personalizzato con il grafico a torta
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("ASSENZE DEGLI ALUNNI");
        Label titleLabel = (Label) pieChart.lookup(".chart-title");
        if (titleLabel != null) {
            titleLabel.setFont(Font.font("Extra-Bold", 40));
        }
        dialog.getDialogPane().setContent(pieChart);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Assegnare un azione al pulsante OK
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return ButtonType.OK;
            }
            return null;
        });
        Optional<ButtonType> result = dialog.showAndWait();
    }

    public void showGradesChart(ActionEvent actionEvent) {
        PieChart pieChart = new PieChart();
        ArrayList<Double> roundedGrades = new ArrayList<>();
        pieChart.setTitle("MEDIE DEGLI ALUNNI");
        Label titleLabel = (Label) pieChart.lookup(".chart-title");
        if (titleLabel != null) {
            titleLabel.setFont(Font.font("Extra-Bold", 40));
        }
        for (ProfTableRow profTableRow : profTable.getItems()) {
            Double firstAverage = Double.parseDouble(profTableRow.getAverage());
            BigDecimal average = BigDecimal.valueOf(firstAverage);
            BigDecimal decimale = average.remainder(BigDecimal.ONE);
            double newAverage = (decimale.compareTo(new BigDecimal("0.5")) >= 0)        //controllo se la parte decimale è >= 0.5 o meno
                    ? Math.ceil(firstAverage)                                               //in caso positivo, arrotondo per eccesso, altrimenti per difetto
                    : Math.floor(firstAverage);
            if(!roundedGrades.contains(newAverage)){
                roundedGrades.add(newAverage);
            }
        }
        for(Double d : roundedGrades){
            Double cont= (double) 0;
            for(ProfTableRow profTableRow : profTable.getItems()){
                Double firstAverage = Double.parseDouble(profTableRow.getAverage());
                BigDecimal average = BigDecimal.valueOf(firstAverage);
                BigDecimal decimale = average.remainder(BigDecimal.ONE);
                double newAverage = (decimale.compareTo(new BigDecimal("0.5")) >= 0)        //controllo se la parte decimale è >= 0.5 o meno
                        ? Math.ceil(firstAverage)                                               //in caso positivo, arrotondo per eccesso, altrimenti per difetto
                        : Math.floor(firstAverage);
                if (newAverage==d){
                    cont+=1;
                }
            }
            int size = profTable.getItems().size();
            Double percent = ((cont/size)*100);
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            decimalFormat.setDecimalSeparatorAlwaysShown(false);
            // Applicare il formato e ottenere la stringa arrotondata
            String formatted = decimalFormat.format(percent);
            int newGrade = d.intValue();
            // Convertire la stringa risultato in un double
            String title = "";
            if(newGrade==0){
                title+="Senza voti: " + formatted + "%";
            }
            else{
                title="Media del " + newGrade + ": " + formatted + "%";
            }
            PieChart.Data slice = new PieChart.Data(title, percent);
            pieChart.getData().add(slice);
        }
        // Creare il dialogo personalizzato con il grafico a torta
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Grafico Voti");
        dialog.getDialogPane().setContent(pieChart);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

        // Assegnare un azione al pulsante OK
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return ButtonType.OK;
            }
            return null;
        });
        Optional<ButtonType> result = dialog.showAndWait();
    }

    public void viewAbsences(ActionEvent actionEvent) {
       View.absencesWindow(username_prof);
    }
    public void updateProfTable(){
        profTable.refresh();
    }
}




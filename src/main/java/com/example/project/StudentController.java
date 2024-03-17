package com.example.project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.example.project.View.currentStage;
import static com.example.project.View.username_logged;

public class StudentController implements  Initializable {
    @FXML
    public AnchorPane anchorPane;
    @FXML
    public MenuBar menuBar;
    @FXML
    private Label name_student;
    @FXML
    private Label info;
    @FXML
    private Label absencesNumber;
    @FXML
    private TableView<StudentFirstTableRow> tableview;
    @FXML
    private TableView average_Table;
    @FXML
    private TableColumn second_subject;
    @FXML
    private TableColumn second_average;

    @FXML
    private TableColumn<StudentFirstTableRow, String> profColumn;

    @FXML
    private TableColumn<StudentFirstTableRow, String> subjectColumn;

    @FXML
    private TableColumn<StudentFirstTableRow, String> gradeColumn;

    @FXML
    private TableColumn<StudentFirstTableRow, String> dateColumn;

    @FXML
    private TableColumn<StudentFirstTableRow, String> commentColumn;
    @FXML
    private Button gradesButton;
    @FXML
    private Button averageButton;
    public Label numberOfAbsences;
    public int schoolDays = 203;



    public void initialize(URL location, ResourceBundle resources) {
        averageButton.setScaleX(0.65);  //riduce le dimensioni del 65%
        averageButton.setScaleY(0.65);
        String theme = Model.getInstance().getTheme();
        if (theme.equals("Light") || theme.equals("Dark")) {
            gradesButton.setStyle("-fx-background-color: blue");
        }
        if (theme.equals("Purple")) {
            gradesButton.setStyle("-fx-background-color: gold");
        }
        if (theme.equals("Blue")) {
            gradesButton.setStyle("-fx-background-color: darkGray");
        }
        if (theme.equals("Green")) {
            gradesButton.setStyle("-fx-background-color: saddleBrown");
        }
        if (theme.equals("Red")) {
            gradesButton.setStyle("-fx-background-color: Turquoise");
        }
        if (theme.equals("Yellow")) {
            gradesButton.setStyle("-fx-background-color: darkGreen");
        }

        average_Table.setVisible(false);
        add_name();
        add_absences();
        fill_Table();
    }

    public void add_name() {
        String usernameToCheck = View.username_logged;
        Model model = Model.getInstance();
        name_student.setText("STUDENTE:    " + model.getFullName(usernameToCheck));
        name_student.getStyleClass().add("label-style");
    }

    public void add_absences() {
        String usernameToCheck = View.username_logged;
        Model model = Model.getInstance();
        String assenze = model.getAbsences(usernameToCheck);
        absencesNumber.setText(assenze);
        absencesNumber.getStyleClass().add("label-style");
        int maxAbsences = computeMaxAbsences(schoolDays);
        if (Integer.parseInt(assenze) <= maxAbsences) {
            absencesNumber.setStyle("-fx-text-fill: green;-fx-font-size: 25px;");
        } else {
            absencesNumber.setStyle("-fx-text-fill: tomato;-fx-font-size: 25px;");
        }
    }

    public void fill_Table() {
        ArrayList<StudentFirstTableRow> studentFirstTableRows = Model.getInstance().getStudentFirstTableRows(View.username_logged);
        // ordina gli elementi in base alla data in ordine decrescente (più recente in cima)
        studentFirstTableRows.sort(Comparator.comparing(this::parseDate).reversed());
        tableview.getItems().clear();
        profColumn.setCellValueFactory(new PropertyValueFactory<>("professorDetails"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
        // imposta uno stile per la cella del voto basato sul suo valore
        gradeColumn.setCellFactory(column -> new TableCell<StudentFirstTableRow, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle(""); // resetta eventuali stili precedenti
                } else {
                    setText(item);

                    // converte il valore del voto in un numero
                    try {
                        double voto = Double.parseDouble(item);

                        // imposta lo stile in base al valore del voto
                        if (voto < 6) {
                            setStyle("-fx-background-color: tomato; -fx-text-fill: white;");
                        } else {
                            setStyle("-fx-background-color: limeGreen; -fx-text-fill: white;");
                        }
                    } catch (NumberFormatException e) {
                        // gestisci l'eccezione se il valore del voto non è un numero valido
                    }
                }
            }
        });
        for(StudentFirstTableRow student : studentFirstTableRows ){
            String grade = student.getValue();
            if(grade.length()==3 || grade.equals("10.0")){
                student.setValue(grade+"0");
            }
        }
        // popola la TableView con i dati
        tableview.getItems().addAll(studentFirstTableRows);

    }


    public void set_grades(ActionEvent actionEvent) {
        String theme = Model.getInstance().getTheme();
        if (theme.equals("Light") || theme.equals("Dark")) {
            gradesButton.setStyle("-fx-background-color: blue");
            averageButton.setStyle("-fx-background-color: royalBlue");
        }
        if (theme.equals("Purple")) {
            gradesButton.setStyle("-fx-background-color: gold");
            averageButton.setStyle("-fx-background-color: khaki");
        }
        if (theme.equals("Blue")) {
            gradesButton.setStyle("-fx-background-color: darkGray");
            averageButton.setStyle("-fx-background-color: whiteSmoke");
        }
        if (theme.equals("Green")) {
            gradesButton.setStyle("-fx-background-color: saddleBrown");
            averageButton.setStyle("-fx-background-color: sienna");
        }
        if (theme.equals("Red")) {
            gradesButton.setStyle("-fx-background-color: Turquoise");
            averageButton.setStyle("-fx-background-color: whiteSmoke");
        }
        if (theme.equals("Yellow")) {
            gradesButton.setStyle("-fx-background-color: darkGreen");
            averageButton.setStyle("-fx-background-color: green");
        }


        averageButton.setScaleX(0.65);  //riduce le dimensioni del 65%
        averageButton.setScaleY(0.65);
        gradesButton.setScaleX(1.0);
        gradesButton.setScaleY(1.0);
        average_Table.setVisible(false);
        tableview.setVisible(true);
        fill_Table();
    }

    public void set_averages(ActionEvent actionEvent) {
        String theme = Model.getInstance().getTheme();
        if (theme.equals("Light") || theme.equals("Dark")) {
            averageButton.setStyle("-fx-background-color: blue");
            gradesButton.setStyle("-fx-background-color: royalBlue");
        }
        if (theme.equals("Purple")) {
            averageButton.setStyle("-fx-background-color: gold");
            gradesButton.setStyle("-fx-background-color: khaki");
        }
        if (theme.equals("Blue")) {
            averageButton.setStyle("-fx-background-color: darkGray ");
            gradesButton.setStyle("-fx-background-color: whiteSmoke");
        }
        if (theme.equals("Green")) {
            averageButton.setStyle("-fx-background-color: saddleBrown");
            gradesButton.setStyle("-fx-background-color: sienna");
        }
        if (theme.equals("Red")) {
            averageButton.setStyle("-fx-background-color: Turquoise");
            gradesButton.setStyle("-fx-background-color: whiteSmoke");
        }
        if (theme.equals("Yellow")) {
            averageButton.setStyle("-fx-background-color: darkGreen");
            gradesButton.setStyle("-fx-background-color: green");
        }

        averageButton.setScaleX(1.0);
        averageButton.setScaleY(1.0);
        gradesButton.setScaleX(0.65);
        gradesButton.setScaleY(0.65);
        average_Table.getItems().clear();
        tableview.setVisible(false);
        average_Table.setVisible(true);
        Model model = Model.getInstance();
        ArrayList<StudentSecondTableRow> list = model.getStudentSecondTableRows(View.username_logged);
        second_subject.setCellValueFactory(new PropertyValueFactory<>("subject"));
        second_average.setCellValueFactory(new PropertyValueFactory<>("average"));
        // imposta la factory delle righe della tabella
        average_Table.setRowFactory(tv -> new TableRow<StudentSecondTableRow>() {
            @Override
            protected void updateItem(StudentSecondTableRow item, boolean empty) {
                super.updateItem(item, empty);

                // verifica se l'elemento è vuoto o nullo
                if (item == null || empty) {
                    setStyle("");
                } else {
                    String averageString = item.getAverage().replace(",", ".");
                    double Average = Double.parseDouble(averageString);
                    if (Average < 6) {
                        setStyle("-fx-background-color: tomato;");
                    } else {
                        setStyle("-fx-background-color: limeGreen;");
                    }
                }
            }
        });
        // aggiunge gli elementi alla tabella
        for (int i = 0; i < list.size(); i++) {
            String average = list.get(i).getAverage();
            String newAverage = average.replace(",",".");
            if(newAverage.length()==3 || newAverage.equals("10.0")){
                newAverage+="0";
            }
            list.get(i).setAverage(newAverage);
            average_Table.getItems().add(list.get(i));
        }
    }

    public void set_student_LightTheme(ActionEvent actionEvent) {
        Scene scene = currentStage.getScene();
        scene.getStylesheets().clear();
        if (gradesButton.getScaleX() == 1.0) {
            gradesButton.setStyle("-fx-background-color:blue");
            averageButton.setStyle("-fx-background-color: royalBlue");
        } else {
            gradesButton.setStyle("-fx-background-color:royalBlue");
            averageButton.setStyle("-fx-background-color: blue");
        }
        scene.getStylesheets().add(getClass().getResource("/application/LightStudent.css").toExternalForm());
        Model.getInstance().updateTheme(username_logged, "Light");
    }

    public void set_student_DarKTheme(ActionEvent actionEvent) {
        Scene scene = currentStage.getScene();
        scene.getStylesheets().clear();
        if (gradesButton.getScaleX() == 1.0) {
            gradesButton.setStyle("-fx-background-color:blue");
            averageButton.setStyle("-fx-background-color: royalBlue");
        } else {
            gradesButton.setStyle("-fx-background-color:royalBlue");
            averageButton.setStyle("-fx-background-color: blue");
        }
        scene.getStylesheets().add(getClass().getResource("/application/DarkStudent.css").toExternalForm());
        Model.getInstance().updateTheme(username_logged, "Dark");
    }

    public void set_student_PurpleTheme(ActionEvent actionEvent) {
        Scene scene = currentStage.getScene();
        scene.getStylesheets().clear();
        if (gradesButton.getScaleX() == 1.0) {
            gradesButton.setStyle("-fx-background-color:gold");
            averageButton.setStyle("-fx-background-color: khaki");
        } else {
            gradesButton.setStyle("-fx-background-color:khaki");
            averageButton.setStyle("-fx-background-color: gold");
        }
        scene.getStylesheets().add(getClass().getResource("/application/PurpleStudent.css").toExternalForm());
        Model.getInstance().updateTheme(username_logged, "Purple");
    }

    public void set_student_BlueTheme(ActionEvent actionEvent) {
        Scene scene = currentStage.getScene();
        scene.getStylesheets().clear();
        if (gradesButton.getScaleX() == 1.0) {
            gradesButton.setStyle("-fx-background-color:DarkGray");
            averageButton.setStyle("-fx-background-color: white");
        } else {
            gradesButton.setStyle("-fx-background-color:white");
            averageButton.setStyle("-fx-background-color: DarkGray");
        }
        scene.getStylesheets().add(getClass().getResource("/application/BlueStudent.css").toExternalForm());
        Model.getInstance().updateTheme(username_logged, "Blue");
    }

    public void set_student_GreenTheme(ActionEvent actionEvent) {
        Scene scene = currentStage.getScene();
        scene.getStylesheets().clear();
        if (gradesButton.getScaleX() == 1.0) {
            gradesButton.setStyle("-fx-background-color:saddleBrown");
            averageButton.setStyle("-fx-background-color: sienna");
        } else {
            gradesButton.setStyle("-fx-background-color:sienna");
            averageButton.setStyle("-fx-background-color: saddleBrown");
        }
        scene.getStylesheets().add(getClass().getResource("/application/GreenStudent.css").toExternalForm());
        Model.getInstance().updateTheme(username_logged, "Green");
    }

    public void set_student_RedTheme(ActionEvent actionEvent) {
        Scene scene = currentStage.getScene();
        scene.getStylesheets().clear();
        if (gradesButton.getScaleX() == 1.0) {
            gradesButton.setStyle("-fx-background-color:Turquoise");
            averageButton.setStyle("-fx-background-color: whiteSmoke");
        } else {
            gradesButton.setStyle("-fx-background-color:whiteSmoke");
            averageButton.setStyle("-fx-background-color: Turquoise");
        }
        scene.getStylesheets().add(getClass().getResource("/application/RedStudent.css").toExternalForm());
        Model.getInstance().updateTheme(username_logged, "Red");

    }

    public void set_student_YellowTheme(ActionEvent actionEvent) {
        Scene scene = currentStage.getScene();
        scene.getStylesheets().clear();
        if (gradesButton.getScaleX() == 1.0) {
            gradesButton.setStyle("-fx-background-color:darkGreen");
            averageButton.setStyle("-fx-background-color: green");

        } else {
            gradesButton.setStyle("-fx-background-color: green");
            averageButton.setStyle("-fx-background-color: darkGreen");
        }
        scene.getStylesheets().add(getClass().getResource("/application/YellowStudent.css").toExternalForm());
        Model.getInstance().updateTheme(username_logged, "Yellow");
    }

    public void Help_functionality(ActionEvent actionEvent) {
        Alert istruzioni = new Alert(Alert.AlertType.INFORMATION);
        istruzioni.setTitle("ISTRUZIONI PER L'USO");
        istruzioni.setHeaderText("BENVENUTO NELLA SCHERMATA STUDENTE!");
        istruzioni.setContentText("Qui potrai visualizzare i voti inseriti dai professori e cliccando sul tasto 'MEDIE VOTI' potrai anche visualizzare le medie per ogni singola materia.\nInfine,puoi scegliere il tema che preferisci dal tasto in alto a sinistra.");
        istruzioni.show();
        istruzioni.setHeight(255);
    }

    public void click(MouseEvent mouseEvent) {
        try {
            TablePosition<StudentFirstTableRow, ?> position = tableview.getSelectionModel().getSelectedCells().get(0);
            int row = position.getRow();
            StudentFirstTableRow studentFirstTableRow = tableview.getItems().get(row);
            TableColumn<StudentFirstTableRow, ?> tableColumn = position.getTableColumn();

            if (tableColumn == commentColumn) {
                String commento = (String) tableColumn.getCellObservableValue(studentFirstTableRow).getValue();

                if (commento != null && !commento.isEmpty()) {
                    show_Popup(commento);
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setWidth(300);
                    alert.setTitle(null);
                    alert.setHeaderText("NESSUN COMMENTO AL VOTO");
                    alert.show();
                }
            }

        } catch (RuntimeException e) {
        }
        tableview.getSelectionModel().select(null);
    }

    private void show_Popup(String testo) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setWidth(300);
        alert.setTitle("COMMENTO VOTO");
        alert.setHeaderText(testo);
        alert.show();
    }

    public int computeMaxAbsences(int schoolDays) {
        int maxAbsences = Integer.parseInt(String.valueOf(schoolDays / 4));
        return maxAbsences;
    }

    // metodo per convertire la stringa della data in un oggetto Date
    private Date parseDate(StudentFirstTableRow row) {
        String dateString = row.getDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    public void Student_logout(ActionEvent actionEvent) {
        try {
            // chiudi la finestra corrente (interfaccia studente)
            currentStage.close();
            // richiama il metodo start per aprire la schermata di login
            new View().start(new Stage());
        } catch (IOException e) {
            // gestione dell'eccezione
        }

    }
    //il cursore diventa una mano
    public void hover(MouseEvent mouseEvent) {info.setCursor(Cursor.HAND);}
    //poi torna di defaul
    public void backToNormal(MouseEvent mouseEvent) {info.setCursor(Cursor.DEFAULT);}
    public void info_Absences(MouseEvent mouseEvent) {
        String usernameToCheck = View.username_logged;
        Model model = Model.getInstance();
        String assenze = model.getAbsences(usernameToCheck);
        int assenzeNumeriche = Integer.parseInt(assenze);
        int max_num_assenze = 50;
        PieChart pieChart = createPieChart(assenzeNumeriche, max_num_assenze);


        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Info Assenze");
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

    private PieChart createPieChart(int actualAbsences, int maxAbsences) {
        PieChart pieChart = new PieChart();
        pieChart.setTitle("GRAFICO ASSENZE");
        // imposta la dimensione del font per il titolo,metodo lookup cerca un nodo figlio del PieChart.chart-title
        Label titleLabel = (Label) pieChart.lookup(".chart-title");
        if (titleLabel != null) {
            titleLabel.setFont(Font.font("Extra-Bold", 40));
        }
        // se il numero di assenze supera il massimo consentito, mostra solo la fetta "Assenze"
        if (actualAbsences <= maxAbsences) {
            PieChart.Data actualAbsencesData = new PieChart.Data("Assenze: " + actualAbsences, actualAbsences);
            PieChart.Data remainingData = new PieChart.Data("Assenze disponibili: " + ( maxAbsences - actualAbsences), maxAbsences - actualAbsences);

            pieChart.getData().addAll(actualAbsencesData, remainingData);
        } else {
            PieChart.Data actualAbsencesData = new PieChart.Data("Assenze: " + actualAbsences, actualAbsences);
            PieChart.Data remainingData = new PieChart.Data("Assenze disponibili: 0" , 0);
            pieChart.getData().addAll(actualAbsencesData,remainingData);
        }

        return pieChart;
    }
}


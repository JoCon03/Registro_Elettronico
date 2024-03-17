package com.example.project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class RequestPassController implements Initializable {

    @FXML
    public TextField usernameRequested;
    @FXML
    public TextField newPasswordRequested;
    @FXML
    public Button submitButton;
    @FXML
    public TextField confirmNewPassword;
    private final Model model = Model.getInstance();
    @FXML
    public AnchorPane pane;
    @FXML
    public Label thirdLabel;
    @FXML
    public Label secondLabel;
    @FXML
    public Label firstLabel;
    @FXML
    public Label titleLabel;
    ArrayList<Label> labels = new ArrayList<>();

    public void initialize(URL location, ResourceBundle resources) {
        labels.add(firstLabel);
        labels.add(secondLabel);
        labels.add(thirdLabel);
        labels.add(titleLabel);
        String theme = model.getTheme();
        switch (theme) {
            case "Light" -> {
                pane.setStyle("-fx-background-color: whiteSmoke");
                for (Label label : labels) {
                    label.setStyle("-fx-text-fill: black");
                }
                submitButton.setStyle("-fx-background-color: royalBlue; -fx-text-fill: whiteSmoke");
                submitButton.setOnMouseEntered(mouseEvent -> {
                    submitButton.setStyle("-fx-background-color: blue; -fx-text-fill: whitesmoke");
                });
                submitButton.setOnMouseExited(mouseEvent -> {
                    submitButton.setStyle("-fx-background-color: royalBlue;-fx-text-fill: whitesmoke");
                });
            }
            case "Dark" -> {
                pane.setStyle("-fx-background-color: #1a1a1a");
                for (Label label : labels) {
                    label.setStyle("-fx-text-fill: whiteSmoke");
                }
                submitButton.setStyle("-fx-background-color: royalBlue; -fx-text-fill: whitesmoke");
                submitButton.setOnMouseEntered(mouseEvent -> {
                    submitButton.setStyle("-fx-background-color: blue; -fx-text-fill: whitesmoke");
                });
                submitButton.setOnMouseExited(mouseEvent -> {
                    submitButton.setStyle("-fx-background-color: royalblue; -fx-text-fill: whitesmoke");
                });
            }
            case "Purple" -> {
                pane.setStyle("-fx-background-color: #690099");
                for (Label label : labels) {
                    label.setStyle("-fx-text-fill: white");
                }
                submitButton.setStyle("-fx-background-color: khaki");
                submitButton.setOnMouseEntered(mouseEvent -> {
                    submitButton.setStyle("-fx-background-color: gold");
                });
                submitButton.setOnMouseExited(mouseEvent -> {
                    submitButton.setStyle("-fx-background-color: khaki");
                });
            }
            case "Blue" -> {
                pane.setStyle("-fx-background-color: #000099");
                for (Label label : labels) {
                    label.setStyle("-fx-text-fill: whiteSmoke");
                }
                submitButton.setStyle("-fx-background-color: whiteSmoke; -fx-text-fill: black");
                submitButton.setOnMouseEntered(mouseEvent -> {
                    submitButton.setStyle("-fx-background-color: darkgrey; -fx-text-fill: black");
                });
                submitButton.setOnMouseExited(mouseEvent -> {
                    submitButton.setStyle("-fx-background-color: whiteSmoke; -fx-text-fill: black");
                });
            }
            case "Green" -> {
                pane.setStyle("-fx-background-color: #0b4d12");
                for (Label label : labels) {
                    label.setStyle("-fx-text-fill: white");
                }
                submitButton.setStyle("-fx-background-color: sienna; -fx-text-fill: whitesmoke");
                submitButton.setOnMouseEntered(mouseEvent -> {
                    submitButton.setStyle("-fx-background-color: saddleBrown; -fx-text-fill: whitesmoke");
                });
                submitButton.setOnMouseExited(mouseEvent -> {
                    submitButton.setStyle("-fx-background-color: sienna; -fx-text-fill: whitesmoke");
                });
            }
            case "Red" -> {
                pane.setStyle("-fx-background-color: FireBrick");
                for (Label label : labels) {
                    label.setStyle("-fx-text-fill: whiteSmoke");
                }
                submitButton.setStyle("-fx-background-color: whiteSmoke; -fx-text-fill: black");
                submitButton.setOnMouseEntered(mouseEvent -> {
                    submitButton.setStyle("-fx-background-color: turquoise; -fx-text-fill: black");
                });
                submitButton.setOnMouseExited(mouseEvent -> {
                    submitButton.setStyle("-fx-background-color: whitesmoke; -fx-text-fill: black");
                });
            }
            case "Yellow" -> {
                pane.setStyle("-fx-background-color: goldenrod");
                for (Label label : labels) {
                    label.setStyle("-fx-text-fill: black");
                }
                submitButton.setStyle("-fx-background-color: green; -fx-text-fill: whitesmoke");
                submitButton.setOnMouseEntered(mouseEvent -> {
                    submitButton.setStyle("-fx-background-color: darkGreen; -fx-text-fill: whitesmoke");
                });
                submitButton.setOnMouseExited(mouseEvent -> {
                    submitButton.setStyle("-fx-background-color: green; -fx-text-fill: whitesmoke");
                });
            }
        }
    }

    public void buttonClicked(ActionEvent actionEvent) {
        boolean checkUsername = false;
        String username = usernameRequested.getText();
        String newPassword = newPasswordRequested.getText();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        String confirmHumanNewPassword = confirmNewPassword.getText();
        if (newPassword.isEmpty()){
            alert.setTitle("ERRORE");
            alert.setHeaderText("ERRORE");
            alert.setContentText("LA PASSWORD NON PUO' ESSERE VUOTA!");
            alert.show();}
        else if(!newPassword.matches("[a-zA-Z0-9_\\-.@]+$")){
            alert.setTitle("ERRORE");
            alert.setHeaderText("ERRORE");
            alert.setContentText("CI SONO DEI SIMBOLI NON CONSENTITI NELLA NUOVA PASSWORD. SONO CONSENTITI SOLO ALFANUMERICI, '_' , '-' , '.' E '@', E NON DEVONO ESSERCI SPAZI");
            alert.show();
        }
        else if (newPassword.length() < 5) {
            alert.setTitle("ERRORE");
            alert.setHeaderText("ERRORE");
            alert.setContentText("LA PASSWORD DEVE CONTENERE ALMENO 4 CARATTERI!");
            alert.show();
        }
        else if (!confirmHumanNewPassword.equals(newPassword)) {
            alert.setTitle("ERRORE");
            alert.setHeaderText("ERRORE");
            alert.setContentText("LE DUE PASSWORD NON COINCIDONO!");
            alert.show();
        }
        else {
            ArrayList<AdminTopTableRow> adminTopTableRows = model.getAdminTopTableRows();
            for (AdminTopTableRow adminTopTableRow : adminTopTableRows) {
                if (adminTopTableRow.getUsername().equals(username)) {
                    checkUsername = true;
                    break;
                }
            }
            ArrayList<AdminBottomTableRow> adminBottomTableRows = model.getAdminBottomTableRows();
            for (AdminBottomTableRow adminBottomTableRow : adminBottomTableRows) {
                if (adminBottomTableRow.getUsername().equals(username)) {
                    checkUsername = true;
                    break;
                }
            }
            if (checkUsername) {
                model.requestPassword(username, newPassword);
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("INFORMAZIONE:");
                alert1.setHeaderText("INFORMAZIONE:");
                alert1.setContentText("L'OPERAZIONE E' ANDATA A BUON FINE. ATTENDI RISPOSTA DALLA SEGRETERIA");
                alert1.show();
                usernameRequested.clear();
                newPasswordRequested.clear();
                confirmNewPassword.clear();
            } else {
                alert.setContentText("LO USERNAME INSERITO NON E' VALIDO!");
                alert.show();
            }
        }

    }

    public void change(KeyEvent event) {
        switch (event.getCode()) {
            case UP -> {
                if (usernameRequested.isFocused()) {
                    confirmNewPassword.requestFocus();
                } else if (newPasswordRequested.isFocused()) {
                    usernameRequested.requestFocus();
                } else if (confirmNewPassword.isFocused()) {
                    newPasswordRequested.requestFocus();
                }
            }
            case DOWN -> {
                if (usernameRequested.isFocused()) {
                    newPasswordRequested.requestFocus();
                } else if (newPasswordRequested.isFocused()) {
                    confirmNewPassword.requestFocus();
                } else if (confirmNewPassword.isFocused()) {
                    usernameRequested.requestFocus();
                }
            }
            case ENTER -> {
                buttonClicked(null);
            }
        }
    }
}


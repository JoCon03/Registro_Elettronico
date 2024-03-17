package com.example.project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class RequestsScreenController implements Initializable {
    @FXML
    public Label titleLabel;
    @FXML
    public AnchorPane pane;
    @FXML
    public TableView<Request> requestsTable;
    @FXML
    public TableColumn<Request, String> requestsTableColumn2;
    @FXML
    public TableColumn<Request, String> requestsTableColumn1;
    @FXML
    public Button acceptButton;
    @FXML
    public Button refuseButton;
    public Model model = Model.getInstance();



    public static class Request {
        private String username;
        private String newPassword;

        public Request(String username, String newPassword) {
            this.username = username;
            this.newPassword = newPassword;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String value) {
            this.username = username;
        }

        public String getNewPassword() {return newPassword;
        }

        public void setNewPassword(String value) {
            this.newPassword = newPassword;
        }
    }

    public void initialize(URL location, ResourceBundle resources) {
        requestsTableColumn1.setCellValueFactory(new PropertyValueFactory<>("username"));
        requestsTableColumn2.setCellValueFactory(new PropertyValueFactory<>("newPassword"));
        String requests = model.getPasswordRequests();
        int startIndex = 0;
        while (startIndex < requests.length()) {
            if(requests.startsWith(",")){
                requests = requests.replaceFirst(",","");           //A volte la dalle richieste non si toglieva la ",", quindi la caccio da qui
            }
            int indexOfColon = requests.indexOf(":", startIndex);
            int indexOfComma = requests.indexOf(",", startIndex);

            if (indexOfColon != -1 && indexOfComma != -1) {
                String username = requests.substring(startIndex, indexOfColon);
                String password = requests.substring(indexOfColon + 1, indexOfComma);
                Request request = new Request(username, password);
                requestsTable.getItems().add(request);
                startIndex = indexOfComma + 1;
            }
        }
        String theme = model.getTheme();
        switch (theme){
            case "Light" -> {
                titleLabel.setStyle("-fx-text-fill: black");
                pane.setStyle("-fx-background-color: whiteSmoke");
                acceptButton.setStyle("-fx-background-color: royalBlue; -fx-text-fill: whiteSmoke");
                acceptButton.setOnMouseEntered(mouseEvent -> {
                    acceptButton.setStyle("-fx-background-color: blue; -fx-text-fill: whiteSmoke");
                });
                acceptButton.setOnMouseExited(mouseEvent -> {
                    acceptButton.setStyle("-fx-background-color: royalBlue; -fx-text-fill: whiteSmoke");
                });
                refuseButton.setStyle("-fx-background-color: royalBlue; -fx-text-fill: whiteSmoke");
                refuseButton.setOnMouseEntered(mouseEvent -> {
                    refuseButton.setStyle("-fx-background-color: blue; -fx-text-fill: whiteSmoke");
                });
                refuseButton.setOnMouseExited(mouseEvent -> {
                    refuseButton.setStyle("-fx-background-color: royalBlue; -fx-text-fill: whiteSmoke");
                });
                requestsTable.setStyle("-fx-control-inner-background: #f2f1f0; -fx-table-cell-border-color: #d6d6d6; -fx-selection-bar: cornflowerblue ");
            }
            case "Dark" -> {
                titleLabel.setStyle("-fx-text-fill: white");
                pane.setStyle("-fx-background-color: #1a1a1a");
                refuseButton.setStyle("-fx-background-color: royalBlue; -fx-text-fill: whitesmoke");
                refuseButton.setOnMouseEntered(mouseEvent -> {
                    refuseButton.setStyle("-fx-background-color: blue; -fx-text-fill: whitesmoke");
                });
                refuseButton.setOnMouseExited(mouseEvent -> {
                    refuseButton.setStyle("-fx-background-color: royalblue; -fx-text-fill: whitesmoke");
                });

                acceptButton.setStyle("-fx-background-color: royalBlue; -fx-text-fill: whitesmoke");
                acceptButton.setOnMouseEntered(mouseEvent -> {
                    acceptButton.setStyle("-fx-background-color: blue; -fx-text-fill: whitesmoke");
                });
                acceptButton.setOnMouseExited(mouseEvent -> {
                    acceptButton.setStyle("-fx-background-color: royalblue; -fx-text-fill: whitesmoke; ");
                });
                requestsTable.setStyle("-fx-control-inner-background: #202020; -fx-table-cell-border-color: white; -fx-selection-bar: dimgrey ");
            }
            case "Purple" -> {
                titleLabel.setStyle("-fx-text-fill: white");
                pane.setStyle("-fx-background-color: #690099");
                acceptButton.setStyle("-fx-background-color: khaki; -fx-text-fill: black");
                acceptButton.setOnMouseEntered(mouseEvent -> {
                    acceptButton.setStyle("-fx-background-color: gold");
                });
                acceptButton.setOnMouseExited(mouseEvent -> {
                    acceptButton.setStyle("-fx-background-color: khaki");
                });

                refuseButton.setStyle("-fx-background-color: khaki; -fx-text-fill: black");
                refuseButton.setOnMouseEntered(mouseEvent -> {
                    refuseButton.setStyle("-fx-background-color: gold");
                });
                refuseButton.setOnMouseExited(mouseEvent -> {
                    refuseButton.setStyle("-fx-background-color: khaki");
                });
                requestsTable.setStyle("-fx-control-inner-background: #690099; -fx-table-cell-border-color: white; -fx-selection-bar: #230033 ");
            }
            case "Blue" -> {
                titleLabel.setStyle("-fx-text-fill: white");
                pane.setStyle("-fx-background-color: #000099");
                refuseButton.setStyle("-fx-background-color: whiteSmoke; -fx-text-fill: black");
                refuseButton.setOnMouseEntered(mouseEvent -> {
                    refuseButton.setStyle("-fx-background-color: darkgrey;");
                });
                refuseButton.setOnMouseExited(mouseEvent -> {
                    refuseButton.setStyle("-fx-background-color: whiteSmoke;");
                });

                acceptButton.setStyle("-fx-background-color: whiteSmoke; -fx-text-fill: black");
                acceptButton.setOnMouseEntered(mouseEvent -> {
                    acceptButton.setStyle("-fx-background-color: darkgrey;");
                });
                acceptButton.setOnMouseExited(mouseEvent -> {
                    acceptButton.setStyle("-fx-background-color: whiteSmoke;");
                });
                requestsTable.setStyle("-fx-control-inner-background: #000099; -fx-table-cell-border-color: white; -fx-selection-bar: slategrey ");

            }

            case "Green" -> {
                titleLabel.setStyle("-fx-text-fill: white");
                pane.setStyle("-fx-background-color: #0b4d12");
                acceptButton.setStyle("-fx-background-color: sienna; -fx-text-fill: whitesmoke");
                acceptButton.setOnMouseEntered(mouseEvent -> {
                    acceptButton.setStyle("-fx-background-color: saddleBrown; -fx-text-fill: whitesmoke");
                });
                acceptButton.setOnMouseExited(mouseEvent -> {
                    acceptButton.setStyle("-fx-background-color: sienna; -fx-text-fill: whitesmoke");
                });

                refuseButton.setStyle("-fx-background-color: sienna; -fx-text-fill: whitesmoke");
                refuseButton.setOnMouseEntered(mouseEvent -> {
                    refuseButton.setStyle("-fx-background-color: saddleBrown; -fx-text-fill: whitesmoke");
                });
                refuseButton.setOnMouseExited(mouseEvent -> {
                    refuseButton.setStyle("-fx-background-color: sienna; -fx-text-fill: whitesmoke");
                });
                requestsTable.setStyle("-fx-control-inner-background: #0b4d12; -fx-table-cell-border-color: white; -fx-selection-bar: #4d1d0b ");

            }
            case "Red" -> {
                titleLabel.setStyle("-fx-text-fill: white");
                pane.setStyle("-fx-background-color: FireBrick");
                refuseButton.setStyle("-fx-background-color: whiteSmoke; -fx-text-fill: black");
                refuseButton.setOnMouseEntered(mouseEvent -> {
                    refuseButton.setStyle("-fx-background-color: turquoise; -fx-text-fill: whitesmoke");
                });
                refuseButton.setOnMouseExited(mouseEvent -> {
                    refuseButton.setStyle("-fx-background-color: whiteSmoke; -fx-text-fill: black");
                });

                acceptButton.setStyle("-fx-background-color: whiteSmoke; -fx-text-fill: black");
                acceptButton.setOnMouseEntered(mouseEvent -> {
                    acceptButton.setStyle("-fx-background-color: turquoise; -fx-text-fill: whitesmoke");
                });
                acceptButton.setOnMouseExited(mouseEvent -> {
                    acceptButton.setStyle("-fx-background-color: whiteSmoke; -fx-text-fill: black");
                });
                requestsTable.setStyle("-fx-control-inner-background: Firebrick; -fx-table-cell-border-color: white; -fx-selection-bar: darkcyan ");
            }
            case "Yellow" -> {
                titleLabel.setStyle("-fx-text-fill: black");
                pane.setStyle("-fx-background-color: goldenrod");
                acceptButton.setStyle("-fx-background-color: green; -fx-text-fill: whitesmoke");
                acceptButton.setOnMouseEntered(mouseEvent -> {
                    acceptButton.setStyle("-fx-background-color: darkGreen; -fx-text-fill: whitesmoke");
                });
                acceptButton.setOnMouseExited(mouseEvent -> {
                    acceptButton.setStyle("-fx-background-color: green; -fx-text-fill: whitesmoke");
                });

                refuseButton.setStyle("-fx-background-color: green; -fx-text-fill: whitesmoke");
                refuseButton.setOnMouseEntered(mouseEvent -> {
                    refuseButton.setStyle("-fx-background-color: darkGreen; -fx-text-fill: whitesmoke");
                });
                refuseButton.setOnMouseExited(mouseEvent -> {
                    refuseButton.setStyle("-fx-background-color: green; -fx-text-fill: whitesmoke");
                });
                requestsTable.setStyle("-fx-control-inner-background: goldenrod; -fx-table-cell-border-color: black; -fx-selection-bar:yellowgreen ");

            }
        }
    }
    public void changed(ActionEvent actionEvent) {
        try{
            Request request = requestsTable.getSelectionModel().getSelectedItem();
            model.updatePassword(request.username, request.newPassword);
            requestsTable.getItems().remove(request);
        }catch (RuntimeException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERRORE");
            alert.setHeaderText("ERRORE");
            alert.setContentText("DEVI PRIMA SELEZIONARE LA RICHIESTA!");
            alert.show();
        }
    }
    public void refused(ActionEvent actionEvent) {
        try{
            Request request = requestsTable.getSelectionModel().getSelectedItem();
            model.discardPasswordRequest(request.getUsername());
            requestsTable.getItems().remove(request);
        }catch (RuntimeException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERRORE");
            alert.setHeaderText("ERRORE");
            alert.setContentText("DEVI PRIMA SELEZIONARE LA RICHIESTA!");
            alert.show();
        }
    }
}

package client.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import protocol.CSReturnValues;
import protocol.classes.Card;
import protocol.classes.CardMovement;
import protocol.classes.CardStatus;
import protocol.classes.Project;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CardShowController extends ClientController {
/*
    Nodes
 */
    @FXML
    private Label labelCardName;
    @FXML
    private TextArea textAreaDesscription;
    @FXML
    private ListView<String> historyListView;
    @FXML
    private JFXComboBox<String> comboMoveCard;
    @FXML
    private JFXButton btnMoveCard;

/*
    Handlers
 */
    @FXML
    void handleComboChooseStatus(ActionEvent event) {
        btnMoveCard.setDisable(false);
    }

    @FXML
    void handleMoveCard(MouseEvent event) {
        String nextStatus = comboMoveCard.getValue();

        if(nextStatus == null)
            return;

        System.out.println(nextStatus);
        try {
            CSReturnValues ret = clientLogic.moveCard(
                    this.project,
                    this.card.getCardName(),
                    this.card.getStatus(),
                    CardStatus.valueOf(comboMoveCard.getValue().replace(" ", "_"))
            );

            switch(ret) {
                case MOVE_CARD_OK:
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setHeaderText("Spostamento completato!");
                    info.showAndWait();

                    break;

                case CARD_FROM_STATUS_OUTDATED:
                    Alert infoFromStatusOutdated = new Alert(Alert.AlertType.INFORMATION);
                    infoFromStatusOutdated.setHeaderText("La carta è stata spostata da un altro utente");
                    infoFromStatusOutdated.setContentText("L'operazione richiesta non è stata completata");
                    infoFromStatusOutdated.showAndWait();
                    break;

                case PROJECT_NOT_PRESENT:
                    showDialogProjectNotPresent();
                    break;

                case USERNAME_NOT_PRESENT:
                    showDialogUsernameNotPresent();
                    break;

                case ILLEGAL_OPERATION:
                    showDialogIllegalOperation();
                    break;
            }

            // close showCard window
            closeStage(btnMoveCard);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/*
start
 */
    @Override
    public void handleCloseRequest() throws IOException {}

    private ObservableList<String> historyObsList = FXCollections.observableArrayList();
    private Card card = null;
    private String project = null;

    public void initShowCard(Card c, String p) {
        this.card = c;
        this.project = p;

        initCardShow();
    }

    public void initCardShow() {
        // card name
        labelCardName.setText(this.card.getCardName());

        // description
        textAreaDesscription.setText(this.card.getDescription());

        // history movement
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for(CardMovement cm : this.card.getCardHistory()) {

            String tmp =    "[" + format.format(cm.movementTime) + "] " +
                    cm.user + ": "
                            + cardStatusToString(cm.fromStatus) + " -> "
                            + cardStatusToString(cm.toStatus);

            historyObsList.add(tmp);

        }
        historyListView.setItems(historyObsList);

        // move box
        ObservableList<String> nextStatusAvailable = FXCollections.observableArrayList();
        switch(this.card.getStatus()) {
            case TO_DO:
                nextStatusAvailable.add(cardStatusToString(CardStatus.IN_PROGRESS));
                break;

            case IN_PROGRESS:
                nextStatusAvailable.add(cardStatusToString(CardStatus.TO_BE_REVISED));
                nextStatusAvailable.add(cardStatusToString(CardStatus.DONE));
                break;

            case TO_BE_REVISED:
                nextStatusAvailable.add(cardStatusToString(CardStatus.IN_PROGRESS));
                nextStatusAvailable.add(cardStatusToString(CardStatus.DONE));
                break;

            case DONE:
                btnMoveCard.setDisable(true);
                break;
        }
        comboMoveCard.setItems(nextStatusAvailable);
    }
}
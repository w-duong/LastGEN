package csulb.cecs323.controller;

import csulb.cecs323.model.Pharmacology;
import csulb.cecs323.model.Usage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Usage_POPUP_CTRL implements Initializable
{
    private boolean isEdit = false;
    private Usage edit;
    private List<Usage> workingCopy;
    public void setWorkingCopy (List<Usage> profiles) { this.workingCopy = profiles; }

    @FXML
    ListView<Usage> profilesListView;
    private ObservableList<Usage> profilesList = FXCollections.observableArrayList();

    @FXML
    TextField inputIndicationField;
    @FXML
    TextArea inputDoseRangeArea;
    @FXML
    CheckBox fdaCheck;

    public void onSaveButton (ActionEvent actionEvent)
    {
        String indication = inputIndicationField.getText();
        String dosaging = inputDoseRangeArea.getText();

        if (indication.trim().equals("") || dosaging.trim().equals(""))
            return;
        if (isEdit)
            workingCopy.remove(edit);

        workingCopy.add (new Usage(indication, dosaging, !fdaCheck.isSelected()));

        clearFields();
        refreshList();
    }

    public void onDeleteButton (ActionEvent actionEvent) { deleteAction(); }

    public void onDeleteKeyPress (KeyEvent keyEvent)
    {
        if (keyEvent.getCode().equals(KeyCode.DELETE))
            deleteAction();
    }

    public void deleteAction ()
    {
        if(profilesListView.getSelectionModel().getSelectedItems() != null)
        {
            workingCopy.removeAll(profilesListView.getSelectionModel().getSelectedItems());

            clearFields();
            refreshList();
        }
    }

    public void onListDoubleClick (MouseEvent mouseEvent)
    {
        if(mouseEvent.getClickCount() == 2 && profilesListView.getSelectionModel().getSelectedItem() != null)
        {
            isEdit = true;
            edit = profilesListView.getSelectionModel().getSelectedItem();

            inputIndicationField.setText(edit.getIndication());
            inputDoseRangeArea.setText(edit.getDosageRange());
            fdaCheck.setSelected(!edit.getApproval());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(()->{
            profilesList.setAll(workingCopy);
            profilesListView.setItems(profilesList);

            profilesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        });
    }

    public void refreshList ()
    {
        profilesList.removeAll(profilesList);
        profilesList.addAll(workingCopy);
        profilesListView.setItems(profilesList);
    }

    public void clearFields ()
    {
        inputIndicationField.setText(null);
        inputDoseRangeArea.setText(null);
        fdaCheck.setSelected(false);
    }

    public void onCancelButton (ActionEvent actionEvent)
    {
        Stage popUp = (Stage) profilesListView.getScene().getWindow();
        popUp.close();
    }
}

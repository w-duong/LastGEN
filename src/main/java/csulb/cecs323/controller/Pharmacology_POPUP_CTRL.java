package csulb.cecs323.controller;

import csulb.cecs323.model.Pharmacology;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Pharmacology_POPUP_CTRL implements Initializable
{
    private boolean isEdit = false;
    private Pharmacology edit;
    private List<Pharmacology> workingCopy;
    public void setWorkingCopy (List<Pharmacology> profiles) { this.workingCopy = profiles; }

    @FXML
    ListView<Pharmacology> profilesListView;
    private ObservableList<Pharmacology> profilesList = FXCollections.observableArrayList();

    @FXML
    TextField inputOrganField;
    @FXML
    TextField inputEnzymeField;
    @FXML
    TextField inputElimField;

    public void onSaveButton (ActionEvent actionEvent)
    {
        String organ = (inputOrganField.getText() == null) ? "" : inputOrganField.getText();
        String enzyme = (inputEnzymeField.getText() == null) ? "" : inputEnzymeField.getText();
        String elim_route = (inputElimField.getText() == null) ? "" : inputElimField.getText();

        if (organ.trim().equals("") && enzyme.trim().equals("") && elim_route.trim().equals(""))
            return;
        if (isEdit)
            workingCopy.remove(edit);

        workingCopy.add (new Pharmacology(organ, enzyme, elim_route));

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

            inputOrganField.setText(edit.getClearanceOrgan());
            inputEnzymeField.setText(edit.getClearanceEnzyme());
            inputElimField.setText(edit.getEliminationRoute());
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
        inputOrganField.setText(null);
        inputEnzymeField.setText(null);
        inputElimField.setText(null);
    }

    public void onCancelButton (ActionEvent actionEvent)
    {
        Stage popUp = (Stage) profilesListView.getScene().getWindow();
        popUp.close();
    }
}

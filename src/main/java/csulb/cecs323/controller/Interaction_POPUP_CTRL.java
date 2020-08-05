package csulb.cecs323.controller;

import csulb.cecs323.model.Drug;
import csulb.cecs323.model.DrugDrugIX;
import csulb.cecs323.model.Pharmacology;
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

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class Interaction_POPUP_CTRL implements Initializable
{
    /* Scene specific */
    private boolean isEdit = false;
    private DrugDrugIX edit;
    private boolean isMidTransaction = false;
    public void setIsMidTransaction (boolean isMidTransaction) { this.isMidTransaction = isMidTransaction; }

    /* Transaction specific */
    private EntityManager entityManager;
    public void setEntityManager (EntityManager entityManager) { this.entityManager = entityManager; }
    private Drug workingCopy;
    public void setWorkingCopy (Drug workingCopy) { this.workingCopy = workingCopy; }

    @FXML
    TextField inputDrugField;
    private Drug offendingDrug = new Drug ("","");
    public void setOffendingDrug (Drug drug) { this.offendingDrug = drug; }

    @FXML
    TextArea inputIXDescription;

    @FXML
    ListView<DrugDrugIX> profilesListView;
    private ObservableList<DrugDrugIX> ixObservableList = FXCollections.observableArrayList();

    public void onSaveButton (ActionEvent actionEvent)
    {
        if (!offendingDrug.getChemical_name().equals("") && !inputDrugField.getText().trim().equals("") && severityCBox.getValue() != null)
        {
            if (isEdit && !isMidTransaction)
            {
                workingCopy.getDrugInteractions().remove(edit);

                entityManager.getTransaction().begin();
                TypedQuery query = entityManager.createNamedQuery(DrugDrugIX.DELETE_BY_ONE, DrugDrugIX.class);
                query.setParameter("ixObject", edit);
                query.executeUpdate();
                entityManager.getTransaction().commit();

                isEdit = false;
            }
            else if (isEdit && isMidTransaction)
            {
                workingCopy.getDrugInteractions().remove(edit);

                TypedQuery query = entityManager.createNamedQuery(DrugDrugIX.DELETE_BY_ONE, DrugDrugIX.class);
                query.setParameter("ixObject", edit);
                query.executeUpdate();

                isEdit = false;
            }

            workingCopy.addInterxAsBase(offendingDrug, inputIXDescription.getText(), severityCBox.getValue());

            clearFields();
            refreshList();
        }
    }

    public void onDeleteButton (ActionEvent actionEvent) { deleteAction(); }

    public void onDeleteKeyPress (KeyEvent keyEvent)
    {
        if (keyEvent.getCode().equals(KeyCode.DELETE))
            deleteAction();
    }

    public void deleteAction ()
    {
        if (profilesListView.getSelectionModel().getSelectedItems() != null)
        {
            List<DrugDrugIX> deleteBuffer = profilesListView.getSelectionModel().getSelectedItems();
            workingCopy.getDrugInteractions().removeAll(deleteBuffer);

            for (DrugDrugIX interx : deleteBuffer)
            {
                if (!isMidTransaction)
                {
                    entityManager.getTransaction().begin();
                    TypedQuery query = entityManager.createNamedQuery(DrugDrugIX.DELETE_BY_ONE, DrugDrugIX.class);
                    query.setParameter("ixObject", interx);
                    query.executeUpdate();
                    entityManager.getTransaction().commit();
                }
                else
                {
                    TypedQuery query = entityManager.createNamedQuery(DrugDrugIX.DELETE_BY_ONE, DrugDrugIX.class);
                    query.setParameter("ixObject", interx);
                    query.executeUpdate();
                }
            }
            clearFields();
            refreshList();
        }
    }

    public void onListDoubleClick (MouseEvent mouseEvent)
    {
        if (mouseEvent.getClickCount() == 2 && profilesListView.getSelectionModel().getSelectedItem() != null)
        {
            isEdit = true;
            edit = profilesListView.getSelectionModel().getSelectedItem();

            inputDrugField.setText(edit.getOffender().getChemical_name());
            severityCBox.setValue(edit.getSeverityLevel());
            inputIXDescription.setText(edit.getDescription());
        }
    }

    @FXML
    ChoiceBox<Integer> severityCBox;
    private ObservableList<Integer> severityLevels = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(()->{
            severityLevels.addAll(1,2,3);
            severityCBox.setItems(severityLevels);

            profilesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            inputDrugField.setPromptText(offendingDrug.getChemical_name());

            ixObservableList.setAll(workingCopy.getDrugInteractions());
            profilesListView.setItems(ixObservableList);
        });
    }

    public void clearFields ()
    {
        offendingDrug = new Drug ("", "");
        inputIXDescription.setText("");
        inputDrugField.setText(offendingDrug.getChemical_name());
        severityCBox.setValue(null);
    }

    public void refreshList ()
    {
        ixObservableList.setAll(workingCopy.getDrugInteractions());
        profilesListView.setItems(ixObservableList);
    }

    public void onDrugSearchButton (ActionEvent actionEvent) throws IOException
    {
        Stage newSearch = General_SEARCH_CTRL.readyStage(Drug.class,this,General_SEARCH_CTRL.Mode_IXAddDG,
                "Search for Drug", this.entityManager);
        newSearch.show();
    }

    public void onCancelButton (ActionEvent actionEvent)
    {
        Stage popUp = (Stage) profilesListView.getScene().getWindow();
        popUp.close();
    }
}

package csulb.cecs323.controller;

import com.sun.javafx.image.impl.General;
import csulb.cecs323.model.Drug;
import csulb.cecs323.model.DrugClass;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.ResourceBundle;

public class DrugClass_NEW_CTRL implements Initializable
{
    private boolean isMidTransaction = false;
    protected void setIsMidTransaction (boolean isMidTransaction) { this.isMidTransaction = isMidTransaction; }
    private boolean isDuplicate = false;
    protected void setIsDuplicate (boolean isDuplicate) { this.isDuplicate = isDuplicate; }

    EntityManager entityManager;
    public void setEntityManager (EntityManager entityManager) { this.entityManager = entityManager; }

    private DrugClass workingCopy = new DrugClass("", "");
    public void setWorkingCopy (DrugClass workingCopy) { this.workingCopy = workingCopy; }
    public DrugClass getWorkingCopy () { return this.workingCopy; }

    @FXML
    TextField inputNameField;
    @FXML
    Label nameLabel;
    @FXML
    TextField inputAbbrField;
    @FXML
    Label abbrLabel;
    @FXML
    TextField inputParentField;
    @FXML
    TextField inputChildrenField;
    List<DrugClass> drugClassesTransient = new ArrayList<>();
    ArrayList<String> autocompleteDrugClassList = new ArrayList<>();

    @FXML
    ListView<Drug> drugListView;
    private ObservableList<Drug> drugObservableList = FXCollections.observableArrayList();

    @FXML
    ListView<DrugClass> parentListView;
    private ObservableList<DrugClass> parentObservableList = FXCollections.observableArrayList();

    @FXML
    ListView<DrugClass> childrenListView;
    private ObservableList<DrugClass> childObservableList = FXCollections.observableArrayList();

    public void drugClassOnEditButton (ActionEvent actionEvent) throws IOException
    {
        Stage newSearch = General_SEARCH_CTRL.readyStage(DrugClass.class, this, General_SEARCH_CTRL.Mode_DCEditDC,
                "Search for Drug Class", this.entityManager);

        newSearch.show();
    }

    public void drugClassOnAddDrugsButton (ActionEvent actionEvent) throws IOException
    {
        Stage newSearch = General_SEARCH_CTRL.readyStage(Drug.class, this, General_SEARCH_CTRL.Mode_DCAddDG,
                "Search for Drug", this.entityManager);

        newSearch.show();
    }

    public void drugClassOnDeleteFromList (KeyEvent keyEvent)
    {
        if (keyEvent.getCode().equals(KeyCode.DELETE) && drugListView.getSelectionModel().getSelectedItems() != null)
        {
            for (Drug drug : drugListView.getSelectionModel().getSelectedItems())
                workingCopy.removeDrug(drug);

            refreshDrugList();
        }
    }

    protected void resetLabels ()
    {
        nameLabel.setText("Class Name: " + workingCopy.getName());
        abbrLabel.setText("Class Abbreviation: " + workingCopy.getAbbreviation());
    }

    public void onClassNameEnterKey (KeyEvent keyEvent)
    {
        if (keyEvent.getCode().equals(KeyCode.ENTER) && !inputNameField.getText().trim().isEmpty()
                && !inputNameField.getText().trim().equals(""))
        {
            workingCopy.setName(inputNameField.getText());
            resetLabels();

            inputNameField.setText(null);
            inputNameField.setPromptText(workingCopy.getName());
        }
    }

    public void onClassAbbrEnterKey (KeyEvent keyEvent)
    {
        if (keyEvent.getCode().equals(KeyCode.ENTER) && !inputAbbrField.getText().isEmpty()
                && !inputAbbrField.getText().trim().equals(""))
        {
            workingCopy.setAbbreviation(inputAbbrField.getText());
            resetLabels();

            inputAbbrField.setText(null);
            inputAbbrField.setPromptText(workingCopy.getAbbreviation());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        Platform.runLater(()->{
            TypedQuery query = entityManager.createNamedQuery(DrugClass.FIND_ALL_BY_NAME, DrugClass.class).setParameter("searchString", "");
            drugClassesTransient = query.getResultList();

            for (DrugClass dc : drugClassesTransient)
                autocompleteDrugClassList.add(dc.getName());

            TextFields.bindAutoCompletion(inputParentField, autocompleteDrugClassList);
            TextFields.bindAutoCompletion(inputChildrenField, autocompleteDrugClassList);
            inputParentField.setPromptText("Name of Drug Class to add as 'Parent'...");
            inputChildrenField.setPromptText("Name of Drug Class to add as 'Child'...");

            drugObservableList.addAll(workingCopy.getDrugs());
            parentObservableList.addAll(workingCopy.getSuperclasses());
            childObservableList.addAll(workingCopy.getSubclasses());

            drugListView.setItems(drugObservableList);
            parentListView.setItems(parentObservableList);
            childrenListView.setItems(childObservableList);

            drugListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            parentListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            childrenListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            refreshFields();
        });
    }

    public void refreshFields ()
    {
        if (workingCopy.getName() == null || workingCopy.getName().equals(""))
            inputNameField.setPromptText("Drug Class Name...");
        else
        {
            inputNameField.setText(null);
            inputNameField.setPromptText(workingCopy.getName());
        }

        if (workingCopy.getAbbreviation() == null || workingCopy.getAbbreviation().equals(""))
            inputAbbrField.setPromptText("4-Letter Drug Abbreviation...");
        else
        {
            inputAbbrField.setText(null);
            inputAbbrField.setPromptText(workingCopy.getAbbreviation());
        }
    }

    public void refreshParentList ()
    {
        parentObservableList.removeAll(parentObservableList);
        parentObservableList.addAll(workingCopy.getSuperclasses());

        inputParentField.setText(null);
        inputParentField.setPromptText("Name of Drug Class to add as 'Parent'...");
    }

    public void refreshChildList ()
    {
        childObservableList.removeAll(childObservableList);
        childObservableList.addAll(workingCopy.getSubclasses());

        inputChildrenField.setText(null);
        inputChildrenField.setPromptText("Name of Drug Class to add as 'Child'...");
    }

    public void refreshDrugList ()
    {
        drugObservableList.removeAll(drugObservableList);
        drugObservableList.addAll(workingCopy.getDrugs());
    }

    public void refreshLists () // TO DO: this is horrible....
    {
        refreshDrugList();
        refreshParentList();
        refreshChildList();
    }

    public void onCancelButton (ActionEvent actionEvent)
    {
        if (isMidTransaction)
            entityManager.getTransaction().rollback();

        Stage popUp = (Stage) inputNameField.getScene().getWindow();
        popUp.close();
    }

    public void onDeleteClassButton (ActionEvent actionEvent)
    {
        if (isMidTransaction)
        {
            entityManager.getTransaction().commit();
            isMidTransaction = false;
            isDuplicate = false;
        }

        entityManager.getTransaction().begin();

        Query deleteQuery = entityManager.createQuery("DELETE FROM DrugClass dc WHERE dc = :toDelete");
        int deletedCount = deleteQuery.setParameter("toDelete", workingCopy).executeUpdate();

        entityManager.getTransaction().commit();

        cleanUpTransaction();
    }

    public void onSaveButton (ActionEvent actionEvent)
    {
        if (!workingCopy.getName().trim().equals("") && !workingCopy.getAbbreviation().trim().equals(""))
        {
            if (isMidTransaction && !isDuplicate)
            {
                entityManager.persist(workingCopy);
                entityManager.getTransaction().commit();
                isMidTransaction = false;
            }
            else if (isMidTransaction && isDuplicate)
            {
                DrugClass newTemp = new DrugClass (workingCopy);
                entityManager.getTransaction().rollback();

                entityManager.getTransaction().begin();
                entityManager.persist(newTemp);
                entityManager.getTransaction().commit();
                isMidTransaction = false;
                isDuplicate = false;
            }
            else
            {
                entityManager.getTransaction().begin();
                entityManager.persist(workingCopy);
                entityManager.getTransaction().commit();
            }

        cleanUpTransaction();
        }
    }

    protected void cleanUpTransaction ()
    {
        workingCopy = new DrugClass ("", "");
        refreshFields();
        refreshLists();
        resetLabels();
    }

    public void onParentEnterKey (KeyEvent keyEvent)
    {
        if (keyEvent.getCode().equals(KeyCode.ENTER))
        {
            for (DrugClass dc : drugClassesTransient)
                if (dc.getName().equals(inputParentField.getText()) && !workingCopy.getSuperclasses().contains(dc))
                {
                    workingCopy.addSuperclass(dc);
                    break;
                }
            refreshParentList();
        }
    }

    public void onDeleteFromParentList (KeyEvent keyEvent)
    {
        if (keyEvent.getCode().equals(KeyCode.DELETE) && parentListView.getSelectionModel().getSelectedItems() != null)
        {
            for (DrugClass dc : parentListView.getSelectionModel().getSelectedItems())
                workingCopy.removeSuperclass(dc);

            refreshParentList();
        }
    }

    public void onDeleteFromChildList (KeyEvent keyEvent)
    {
        if (keyEvent.getCode().equals(KeyCode.DELETE) && childrenListView.getSelectionModel().getSelectedItems() != null)
        {
            for (DrugClass dc : childrenListView.getSelectionModel().getSelectedItems())
                workingCopy.removeSubclass(dc);

            refreshChildList();
        }
    }

    public void onChildEnterKey (KeyEvent keyEvent)
    {
        if (keyEvent.getCode().equals(KeyCode.ENTER))
        {
            for (DrugClass dc : drugClassesTransient)
                if (dc.getName().equals(inputChildrenField.getText()) && !workingCopy.getSubclasses().contains(dc))
                {
                    workingCopy.addSubclass(dc);
                    break;
                }
            refreshChildList();
        }
    }
}

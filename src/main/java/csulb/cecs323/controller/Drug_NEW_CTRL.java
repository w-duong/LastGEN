package csulb.cecs323.controller;

import csulb.cecs323.model.BrandName;
import csulb.cecs323.model.DEA_Class;
import csulb.cecs323.model.Drug;
import csulb.cecs323.model.DrugClass;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.controlsfx.control.textfield.TextFields;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Drug_NEW_CTRL implements Initializable
{
    EntityManager entityManager;
    public void setEntityManager (EntityManager entityManager) { this.entityManager = entityManager; }

    private Drug workingCopy = new Drug("", "");
    public void setWorkingCopy (Drug workingCopy) { this.workingCopy = workingCopy; }
    public Drug getWorkingCopy () { return this.workingCopy; }

    @FXML
    TextField inputCNameField;
    public void onChemNameEnterKey (KeyEvent keyEvent)
    {
        if (keyEvent.getCode().equals(KeyCode.ENTER))
        {
            workingCopy.setChemicalName(inputCNameField.getText());
            inputCNameField.setText(null);
            inputCNameField.setPromptText("Enter chemical name...");
        }
    }

    @FXML
    TextField inputBNameField;
    private ArrayList<String> autocompleteBNameList = new ArrayList<>();

    public void onBrandNameEnterKey (KeyEvent keyEvent)
    {
        if (keyEvent.getCode().equals(KeyCode.ENTER))
        {
            workingCopy.addBrandName(inputBNameField.getText());
            inputBNameField.setText(null);
            inputBNameField.setPromptText("Add a Brand name...");
        }
    }

    public void onBrandNameDeleteButton (ActionEvent actionEvent)
    {
        if ((inputBNameField.getText() != null) && (inputBNameField.getText() != ""))
        {
            workingCopy.removeBrandName(inputBNameField.getText());
            inputBNameField.setText(null);
            inputBNameField.setPromptText("Add a Brand name...");
        }
    }

    @FXML
    TextField inputPNameField;
    private List<DrugClass> drugClassesTransient;
    private ArrayList<String> autocompletePNameList = new ArrayList<>();

    public void onParentNameEnterKey (KeyEvent keyEvent)
    {
        if (keyEvent.getCode().equals(KeyCode.ENTER))
        {
            for (DrugClass dc : drugClassesTransient)
                if (dc.getName().equals(inputPNameField.getText()))
                    workingCopy.addDrugClass(dc);

            inputPNameField.setText(null);
            inputPNameField.setPromptText("Add a parent Drug class...");
        }
    }

    public void onParentNameDeleteButton (ActionEvent actionEvent)
    {
        if ((inputPNameField.getText() != null) && (inputPNameField.getText() != ""))
        {
            for (DrugClass dc : drugClassesTransient)
                if (dc.getName().equals(inputPNameField.getText()))
                    workingCopy.removeDrugClass(dc);

            inputPNameField.setText(null);
            inputPNameField.setPromptText("Add a parent Drug class...");
        }
    }

    @FXML
    ChoiceBox<String> scheduleCBox;
    private List<DEA_Class> deaClassesTransient;
    private ObservableList<String> deaSchedules = FXCollections.observableArrayList();

    protected void populateCBox ()
    {
        TypedQuery query = entityManager.createNamedQuery(DEA_Class.FIND_ALL, DEA_Class.class);
        deaClassesTransient = query.getResultList();

        for (int i = 1; i <= 8; ++i)
            for (DEA_Class dea : deaClassesTransient)
                if (dea.getSchedule() == i)
                    deaSchedules.add(dea.getSymbol().toString());

        scheduleCBox.setItems(deaSchedules);
    }

    public void onScheduleAddButton (ActionEvent actionEvent)
    {
        if (scheduleCBox.getValue() != null)
            for (DEA_Class dea : deaClassesTransient)
                if (dea.getSymbol().toString().equals(scheduleCBox.getValue()))
                    workingCopy.setDrugSchedule(dea);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        Platform.runLater(()->{
            populateCBox();

            TypedQuery query = entityManager.createNamedQuery(DrugClass.FIND_ALL_BY_NAME, DrugClass.class).setParameter("searchString", "");
            drugClassesTransient = query.getResultList();

            for (DrugClass dc : drugClassesTransient)
                autocompletePNameList.add(dc.getName());

            //NOTE: may need to pull from DB here (!!)
            for (BrandName bns: workingCopy.getBrandNames())
                autocompleteBNameList.add(bns.toString());

            TextFields.bindAutoCompletion(inputBNameField, autocompleteBNameList);
            TextFields.bindAutoCompletion(inputPNameField, autocompletePNameList);
        });
    }
}

package csulb.cecs323.controller;

import csulb.cecs323.model.BrandName;
import csulb.cecs323.model.Drug;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.controlsfx.control.textfield.TextFields;

import javax.persistence.EntityManager;
import java.net.URL;
import java.util.ArrayList;
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
    ArrayList<String> autocompleteBNameList = new ArrayList<>();

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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        Platform.runLater(()->{
            //NOTE: may need to pull from DB here (!!)
            for (BrandName bns: workingCopy.getBrandNames())
                autocompleteBNameList.add(bns.toString());

            TextFields.bindAutoCompletion(inputBNameField, autocompleteBNameList);
        });
    }
}

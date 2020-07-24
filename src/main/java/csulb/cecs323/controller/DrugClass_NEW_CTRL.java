package csulb.cecs323.controller;

import csulb.cecs323.model.DrugClass;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class DrugClass_NEW_CTRL implements Initializable
{
    EntityManager entityManager;
    public void setEntityManager (EntityManager entityManager) { this.entityManager = entityManager; }

    private DrugClass workingCopy = new DrugClass();
    public void setWorkingCopy (DrugClass workingCopy) { this.workingCopy = workingCopy; }

    @FXML
    TextField inputNameField;
    @FXML
    TextField inputAbbrField;

    public void drugClass_new_onButtonSelect (ActionEvent actionEvent) throws IOException
    {
        Stage drugClass_generalSearch = new Stage();
        URL generalSearchScene = Paths.get("./src/main/resources/layout/General_SEARCH.fxml").toUri().toURL();
        FXMLLoader loader = new FXMLLoader(generalSearchScene);
        Parent root = loader.load();

        /* Pass EntityManager to next Stage and pass 'WorkingCopy' to set Controller<Type> */
        General_SEARCH_CTRL<DrugClass, DrugClass_NEW_CTRL> controller = loader.getController();
        controller.setEntityManager(this.entityManager);
        controller.setLastScene(this);

        Scene scene = new Scene (root); // can also specify size of scene/contents here, but already configured in FXML?

        // "staging"
        drugClass_generalSearch.setTitle("Search for Drug Class");
        drugClass_generalSearch.initModality(Modality.APPLICATION_MODAL); // prevents user from moving to another Stage until done
        drugClass_generalSearch.setScene(scene);
        drugClass_generalSearch.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
    }

    public void refreshFields (ActionEvent actionEvent)
    {
        if (workingCopy.getName() == null || workingCopy.getName().equals(""))
            inputNameField.setPromptText("Drug Class Name...");
        else
            inputNameField.setText(workingCopy.getName());

        if (workingCopy.getAbbreviation() == null || workingCopy.getAbbreviation().equals(""))
            inputAbbrField.setPromptText("4-Letter Drug Abbreviation...");
        else
            inputAbbrField.setText(workingCopy.getAbbreviation());

        System.out.println ("data inside of > " + workingCopy.toString());
    }
}

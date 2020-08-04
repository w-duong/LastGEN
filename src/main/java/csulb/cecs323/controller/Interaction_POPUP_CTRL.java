package csulb.cecs323.controller;

import csulb.cecs323.model.Drug;
import csulb.cecs323.model.DrugDrugIX;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Interaction_POPUP_CTRL implements Initializable
{
    private EntityManager entityManager;
    public void setEntityManager (EntityManager entityManager) { this.entityManager = entityManager; }

    private List<DrugDrugIX> workingCopy;
    public void setWorkingCopy (List<DrugDrugIX> workingCopy) { this.workingCopy = workingCopy; }

    @FXML
    TextField inputDrugField;
    private Drug offendingDrug = new Drug ("","");
    public void setOffendingDrug (Drug drug) { this.offendingDrug = drug; }

    @FXML
    ListView<DrugDrugIX> profilesListView;
    private ObservableList<DrugDrugIX> ixObservableList = FXCollections.observableArrayList();

    public void onDeleteKeyPress () {}

    public void deleteAction () {}

    public void onListDoubleClick () {}

    private enum severity {LOW, MODERATE, SEVERE};
    @FXML
    ChoiceBox<Integer> severityCBox;
    private ObservableList<Integer> severityLevels = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(()->{
            for (int i = 0; i < 3; ++i)
                severityLevels.add(severity.values()[i].ordinal() + 1);
            severityCBox.setItems(severityLevels);

            profilesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            inputDrugField.setPromptText(offendingDrug.getChemical_name());

            ixObservableList.setAll(workingCopy);
            profilesListView.setItems(ixObservableList);
        });
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

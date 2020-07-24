package csulb.cecs323.controller;

import csulb.cecs323.model.Drug;
import csulb.cecs323.model.DrugClass;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.swing.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class General_SEARCH_CTRL<DataType, SceneType> implements Initializable
{
    EntityManager entityManager;
    public void setEntityManager (EntityManager entityManager) { this.entityManager = entityManager; }

    private SceneType lastScene;
    public void setLastScene (SceneType parent) { this.lastScene = parent; }

    @FXML
    private ListView<DataType> resultsListView;
    private ObservableList<DataType> resultsList = FXCollections.observableArrayList();

    @FXML
    private TextField inputSearchBar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        resultsListView.setItems(resultsList);
    }

    public void onSearchButton (ActionEvent actionEvent) { searchOperation(); }

    public void onEnterKey (KeyEvent keyEvent)
    {
        if (keyEvent.getCode().equals(KeyCode.ENTER))
            searchOperation();
    }

    public void searchOperation ()
    {
        resultsList.removeAll(resultsList);
        String searchString = inputSearchBar.getText();
        TypedQuery query;

        if (lastScene instanceof DrugClass_NEW_CTRL)
        {
            query = entityManager.createNamedQuery(DrugClass.FIND_ALL_BY_NAME, DrugClass.class).setParameter("searchString", searchString);
            resultsList.addAll(query.getResultList());
        }
//        else if (lastScene instanceof Drug_NEW_CTRL)
//        {
//            query = entityManager.createNamedQuery(Drug.FIND_ALL_BY_NAME, Drug.class).setParameter("searchString", searchString);
//            resultsList.addAll(query.getResultList());
//        }
    }

    public void selectOperation ()
    {
        Object temp = resultsListView.getSelectionModel().getSelectedItem();

        /*
        *  Need to CAST 'lastScene' to appropriate type in order to access '.setWorkingCopy()',
        *  then need to CAST 'temp' to appropriate data object while calling '.setWorkingCopy()'
        */
        if (temp instanceof DrugClass)
        {
            ((DrugClass_NEW_CTRL) lastScene).setWorkingCopy((DrugClass) temp);
            ((DrugClass_NEW_CTRL) lastScene).inputNameField.setPromptText(((DrugClass) temp).getName());
            ((DrugClass_NEW_CTRL) lastScene).inputAbbrField.setPromptText(((DrugClass) temp).getAbbreviation());
        }
//        else if (temp instanceof Drug)
//            ((Drug_NEW_CTRL)lastScene).setWorkingCopy((Drug) temp);

        Stage popUp = (Stage) inputSearchBar.getScene().getWindow();
        popUp.close();
    }

    public void onSelectButton (ActionEvent actionEvent) { selectOperation(); }

    KeyCombination select_shortcut = new KeyCodeCombination(KeyCode.T, KeyCombination.ALT_DOWN);
    public void onSelectShortcut (KeyEvent keyEvent)
    {
        if (select_shortcut.match(keyEvent))
            selectOperation();
    }

    public void onCancelButton (ActionEvent actionEvent)
    {
        Stage popUp = (Stage) inputSearchBar.getScene().getWindow();
        popUp.close();
    }
}

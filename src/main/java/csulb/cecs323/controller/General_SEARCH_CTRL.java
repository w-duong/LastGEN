package csulb.cecs323.controller;

import csulb.cecs323.model.Drug;
import csulb.cecs323.model.DrugClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class General_SEARCH_CTRL<Type> implements Initializable
{
    EntityManager entityManager;
    public void setEntityManager (EntityManager entityManager) { this.entityManager = entityManager; }

    private Type workingCopy;
    public void setWorkingCopy (Type workingCopy) { this.workingCopy = workingCopy; }

    @FXML
    private ListView<Type> resultsListView;
    private ObservableList<Type> resultsList = FXCollections.observableArrayList();

    @FXML
    private TextField inputSearchBar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        resultsListView.setItems(resultsList);
    }

    public void searchOnSelectButton (ActionEvent actionEvent)
    {
        resultsList.removeAll(resultsList);
        searchOperation();
    }

    public void searchOperation ()
    {
        String searchString = inputSearchBar.getText();
        TypedQuery query;

        if (workingCopy instanceof DrugClass)
        {
            query = entityManager.createNamedQuery(DrugClass.FIND_ALL_BY_NAME, DrugClass.class).setParameter("searchString", searchString);
            resultsList.addAll(query.getResultList());
        }
        else if (workingCopy instanceof Drug)
        {
            query = entityManager.createNamedQuery(Drug.FIND_ALL_BY_NAME, Drug.class).setParameter("searchString", searchString);
            resultsList.addAll(query.getResultList());
        }
    }
}

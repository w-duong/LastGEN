package csulb.cecs323.controller;

import csulb.cecs323.model.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class General_SEARCH_CTRL<DataType, SceneType> implements Initializable
{
    /* Select operational mode of search window */
    static final String Mode_DCEditDC = "drugClass.EditDrugClass";
    static final String Mode_DCAddDG = "drugClass.AddDrug";
    static final String Mode_DGEditDG = "drug.EditDrug";
    static final String Mode_IXAddDG = "interaction.AddDrug";
    static final String Mode_USEditPT = "userBase.EditPatient";
    static final String Mode_USEditMD = "userBase.EditPrescriber";
    static final String Mode_USEditOP = "userBase.EditOperator";
    static final String Mode_USAddDG = "userBase.AddDrugAllergy";

    private String operationSelect;
    public void setOperationSelect (String operation) { this.operationSelect = operation; }
    public String getOperationSelect () { return this.operationSelect; }

    private boolean isMultipleMode;
    public void setMultipleMode (boolean isMultipleMode) { this.isMultipleMode = isMultipleMode; }

    EntityManager entityManager;
    public void setEntityManager (EntityManager entityManager) { this.entityManager = entityManager; }

    private SceneType lastScene;
    public void setLastScene (SceneType parent) { this.lastScene = parent; }

    private ArrayList<DataType> resultsBuffer = new ArrayList<>();

    @FXML
    private ListView<DataType> resultsListView;
    private ObservableList<DataType> resultsList = FXCollections.observableArrayList();

    @FXML
    private TextField inputSearchBar;
    @FXML
    private CheckBox duplicateCheck;

    /*
        Userbase specific input fields, not applicable for Drug/DrugClass
     */
    @FXML
    private DatePicker inputPatientDOB;
    @FXML
    private TextField inputNPIField;
    @FXML
    private TextField inputLicenseField;
    @FXML
    private Pane addUserPane;

    // MODIFY (1)
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        /* NECESSARY, or values will remain NULL - such as, 'isMultipleMode' */
        Platform.runLater(()->{
            /* Select if Userbase input fields visible + if 'Duplicate' option available + if multiple selection possible */
            switch (operationSelect)
            {
                case Mode_USEditMD:
                case Mode_USEditOP:
                    inputPatientDOB.setDisable(true);
                case Mode_USEditPT:
                    addUserPane.setVisible(true);
                case Mode_IXAddDG:
                case Mode_USAddDG:
                    duplicateCheck.setDisable(true);
                case Mode_DCEditDC:
                case Mode_DGEditDG:
                    isMultipleMode = false;
                    resultsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
                    break;
                case Mode_DCAddDG:
                    duplicateCheck.setDisable(true);
                    isMultipleMode = true;
                    resultsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                    break;
            }

            resultsListView.setItems(resultsList);
        });
    }

    public void onSearchButton (ActionEvent actionEvent) { searchOperation(); }

    public void onEnterKey (KeyEvent keyEvent)
    {
        if (keyEvent.getCode().equals(KeyCode.ENTER))
            searchOperation();
    }

    // MODIFY (2)
    public void searchOperation ()
    {
        resultsList.removeAll(resultsList);
        String searchString = preparser(inputSearchBar.getText());
        TypedQuery query = null;

        if (operationSelect.equals(Mode_DCEditDC))
        {
            query = entityManager.createNamedQuery(DrugClass.FIND_ALL_BY_NAME, DrugClass.class).setParameter("searchString", searchString);
            resultsList.addAll(query.getResultList());
        }
        else if (operationSelect.equals(Mode_DCAddDG) ||
                operationSelect.equals(Mode_DGEditDG) ||
                operationSelect.equals(Mode_IXAddDG) ||
                operationSelect.equals(Mode_USAddDG) )
        {
            query = entityManager.createNamedQuery(Drug.FIND_ALL_BY_NAME, Drug.class).setParameter("searchString", searchString);
            resultsList.addAll(query.getResultList());
        }
        else if (operationSelect.equals(Mode_USEditPT))
        {
            query = doPatientSearch(searchString);
            resultsList.addAll(query.getResultList());
        }
        else if (operationSelect.equals(Mode_USEditMD) || operationSelect.equals(Mode_USEditOP))
        {
            query = doPrescriberSearch(searchString);
            resultsList.addAll(query.getResultList());
        }
    }


    public static String preparser (String search)
    {
        if (search.contains(","))
        {
            String temp = search.trim().replaceAll("[^-'.,a-zA-Z0-9]","");
            String [] stack = temp.split(",");

            return String.join (" ", stack);
        }
        else if (search.matches(".*[\\-()./].*"))
        {
            String temp = search.trim().replaceAll("[^0-9]", "");
            if (temp.length() == 10 && temp.chars().allMatch(Character::isDigit))
                return temp;
            else
                return search.trim();
        }
        else
            return search.trim();
    }

    // MODIFY (3)
    public void selectOperation() // TO DO: this is awful...
    {
        if (isMultipleMode)
            resultsBuffer.addAll(resultsListView.getSelectionModel().getSelectedItems());
        else
            resultsBuffer.add(resultsListView.getSelectionModel().getSelectedItem());

        /*
        *  Need to CAST 'lastScene' to appropriate type in order to access '.setWorkingCopy()',
        *  then need to CAST 'temp' to appropriate data object while calling '.setWorkingCopy()'
        */
        switch (operationSelect)
        {
            case Mode_DCEditDC:
                ((DrugClass_NEW_CTRL) lastScene).setWorkingCopy((DrugClass) resultsBuffer.get(0));
                ((DrugClass_NEW_CTRL) lastScene).resetLabels();
                ((DrugClass_NEW_CTRL) lastScene).refreshFields();
                ((DrugClass_NEW_CTRL) lastScene).refreshLists();

                ((DrugClass_NEW_CTRL) lastScene).setIsDuplicate(duplicateCheck.isSelected());

                if (((DrugClass_NEW_CTRL) lastScene).isMidTransaction())
                    entityManager.getTransaction().rollback();
                ((DrugClass_NEW_CTRL) lastScene).setIsMidTransaction(true);
                entityManager.getTransaction().begin();
                break;
            case Mode_DCAddDG:
                for (DataType drug : resultsBuffer)
                    ((DrugClass_NEW_CTRL) lastScene).getWorkingCopy().addDrug((Drug) drug);

                ((DrugClass_NEW_CTRL) lastScene).refreshLists();
                break;
            case Mode_DGEditDG:
                ((Drug_NEW_CTRL) lastScene).setWorkingCopy((Drug)resultsBuffer.get(0));
                ((Drug_NEW_CTRL) lastScene).refreshBNameAutoComplete();
                ((Drug_NEW_CTRL) lastScene).refreshFields();
                ((Drug_NEW_CTRL) lastScene).printProfile();

                ((Drug_NEW_CTRL) lastScene).setIsDuplicate(duplicateCheck.isSelected());

                if (((Drug_NEW_CTRL) lastScene).isMidTransaction())
                    entityManager.getTransaction().rollback();
                ((Drug_NEW_CTRL) lastScene).setIsMidTransaction(true);
                entityManager.getTransaction().begin();
                break;
            case Mode_IXAddDG:
                ((Interaction_POPUP_CTRL) lastScene).setOffendingDrug((Drug) resultsBuffer.get(0));
                ((Interaction_POPUP_CTRL) lastScene).inputDrugField.setPromptText(((Drug) resultsBuffer.get(0)).getChemical_name());
                break;
            case Mode_USAddDG:
                if (((UserBase_NEW_CTRL) lastScene).getWorkingCopy() instanceof Patient)
                {
                    ((Patient) ((UserBase_NEW_CTRL) lastScene).getWorkingCopy()).addDrugAllergy((Drug) resultsBuffer.get(0));
                    ((UserBase_NEW_CTRL) lastScene).refreshDrugAllergyList();
                }
            case Mode_USEditPT:
                ((UserBase_NEW_CTRL) lastScene).setWorkingCopy((Patient) resultsBuffer.get(0));
                ((UserBase_NEW_CTRL) lastScene).refreshNameInfo(1);
                ((UserBase_NEW_CTRL) lastScene).refreshPhoneList(1);
                ((UserBase_NEW_CTRL) lastScene).refreshAddressList(1);

                if (((UserBase_NEW_CTRL) lastScene).isMidTransaction())
                    entityManager.getTransaction().rollback();;
                ((UserBase_NEW_CTRL) lastScene).setIsMidTransaction(true);
                entityManager.getTransaction().begin();
        }

        Stage popUp = (Stage) inputSearchBar.getScene().getWindow();
        popUp.close();
    }

    public void onSelectButton (ActionEvent actionEvent)
    {
        if (!resultsListView.getSelectionModel().isEmpty())
            selectOperation();
    }

    KeyCombination select_shortcut = new KeyCodeCombination(KeyCode.T, KeyCombination.ALT_DOWN);
    public void onSelectShortcut (KeyEvent keyEvent)
    {
        if (select_shortcut.match(keyEvent) && !resultsListView.getSelectionModel().isEmpty())
            selectOperation();
    }

    public void onCancelButton (ActionEvent actionEvent)
    {
        Stage popUp = (Stage) inputSearchBar.getScene().getWindow();
        popUp.close();
    }

    /*
        Static method to generate Search stage from other modules.
     */
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static <DataType, SceneType> Stage readyStage
    (DataType conOne, SceneType conTwo, String operation, String title, EntityManager manager) throws IOException
    {
        Stage preset = new Stage();
        URL generalSearchScene = Paths.get("./src/main/resources/layout/General_SEARCH.fxml").toUri().toURL();
        FXMLLoader loader = new FXMLLoader(generalSearchScene);
        Parent root = loader.load();

        /* Pass EntityManager to next Stage and pass 'WorkingCopy' to set Controller<Type> */
        General_SEARCH_CTRL<DataType, SceneType> controller = loader.getController(); //REQUIRED: since static method
        controller.setLastScene(conTwo);
        controller.setOperationSelect(operation);
        controller.setEntityManager(manager); // necessary ???

        Scene scene = new Scene (root);

        // "staging"
        preset.setTitle(title);
        preset.initModality(Modality.APPLICATION_MODAL); // prevents user from moving to another Stage until done
        preset.setScene(scene);

        return preset;
    }

    /*
        Person subclass-specific search methods here.
     */
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public TypedQuery doPatientSearch (String searchString)
    {
        TypedQuery query;

        String [] fullName = searchString.split(" ");
        String last = fullName[0];
        String first = (fullName.length < 2) ? "" : fullName[1];

        if (last.equals("") && first.equals("") && inputPatientDOB.getValue() != null)
        {
            query = entityManager.createNamedQuery(Patient.FIND_ALL_BY_DOB, Patient.class);
            query.setParameter("ptDOB", inputPatientDOB.getValue());
        }
        else if (last.chars().allMatch(Character::isDigit))
        {
            query = entityManager.createNamedQuery(Phone.FIND_BY_NUMBER, Patient.class);
            query.setParameter("numberString",last);
        }
        else if (inputPatientDOB.getValue() != null || !inputPatientDOB.getEditor().getText().trim().equals(""))
        {
            query = entityManager.createNamedQuery(Patient.FIND_ALL_BY_SPEC, Patient.class);
            query.setParameter("ptLastName", last)
                    .setParameter("ptFirstName", first)
                    .setParameter("ptDOB", inputPatientDOB.getValue());
        }
        else
        {
            query = entityManager.createNamedQuery(Patient.FIND_ALL_BY_NAME, Patient.class);
            query.setParameter("ptLastName", last).setParameter("ptFirstName", first);
        }

        return query;
    }

    public TypedQuery doPrescriberSearch (String searchString)
    {
        TypedQuery query;

        String [] fullName = searchString.split(" ");
        String last = fullName[0];
        String first = (fullName.length < 2) ? "" : fullName[1];

        String providerCert = (!inputNPIField.getText().trim().equals("")) ? inputNPIField.getText().trim() :
                (!inputLicenseField.getText().trim().equals("")) ? inputLicenseField.getText().trim() : "";

        if (last.equals("") && first.equals("") && !providerCert.equals(""))
        {
            query = entityManager.createNamedQuery(ProviderCertification.FIND_ALL_BY_ACTUAL, Prescriber.class);
            query.setParameter("providerLicenseNum", providerCert);
        }
        else if (last.chars().allMatch(Character::isDigit))
        {
            query = entityManager.createNamedQuery(Phone.FIND_BY_NUMBER, Patient.class);
            query.setParameter("numberString",last);
        }
        else
        {
            query = entityManager.createNamedQuery(Prescriber.FIND_BY_NAME, Prescriber.class);
            query.setParameter("mdLastName", last).setParameter("mdFirstName", first);
        }

        return query;
    }
}

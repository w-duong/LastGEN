package csulb.cecs323.controller;

import csulb.cecs323.model.*;
import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class Drug_NEW_CTRL implements Initializable
{
    private boolean isMidTransaction = false;
    protected void setIsMidTransaction (boolean isMidTransaction) { this.isMidTransaction = isMidTransaction; }
    protected boolean isMidTransaction () { return this.isMidTransaction; }
    private boolean isDuplicate = false;
    protected void setIsDuplicate (boolean isDuplicate) { this.isDuplicate = isDuplicate; }

    EntityManager entityManager;
    public void setEntityManager (EntityManager entityManager) { this.entityManager = entityManager; }

    private Drug workingCopy = new Drug("", "");
    public void setWorkingCopy (Drug workingCopy) { this.workingCopy = workingCopy; }
    public Drug getWorkingCopy () { return this.workingCopy; }

    @FXML
    TextField inputCNameField;
    public void onChemNameEnterKey (KeyEvent keyEvent)
    {
        if (keyEvent.getCode().equals(KeyCode.ENTER) && !inputCNameField.getText().trim().equals(""))
        {
            workingCopy.setChemicalName(inputCNameField.getText());
            inputCNameField.setText(null);
            inputCNameField.setPromptText(workingCopy.getChemical_name());
        }
    }

    @FXML
    TextField inputBNameField;
    private SuggestionProvider<String> autocompleteBNameList;

    public void onBrandNameEnterKey (KeyEvent keyEvent)
    {
        if (keyEvent.getCode().equals(KeyCode.ENTER) && !inputBNameField.getText().isEmpty() && !inputBNameField.getText().trim().equals(""))
        {
            workingCopy.addBrandName(inputBNameField.getText());
            refreshBNameAutoComplete();

            inputBNameField.setPromptText("Add a Brand name...");
            inputBNameField.setText(null);
        }
    }

    public void onBrandNameDeleteButton (ActionEvent actionEvent)
    {
        if ((inputBNameField.getText() != null) && (!inputBNameField.getText().trim().equals("")))
        {
            workingCopy.removeBrandName(inputBNameField.getText());
            refreshBNameAutoComplete();

            inputBNameField.setPromptText("Add a Brand name...");
            inputBNameField.setText(null);
        }
    }

    protected void refreshBNameAutoComplete ()
    {
        autocompleteBNameList.clearSuggestions();

        for (BrandName bns : workingCopy.getBrandNames())
            autocompleteBNameList.addPossibleSuggestions(bns.toString());
    }

    @FXML
    TextArea inputDescription;

    KeyCombination addDescription_shortcut = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.ALT_DOWN);
    public void onDescriptionAltEnterKey (KeyEvent keyEvent)
    {
        if (addDescription_shortcut.match(keyEvent) && !inputDescription.getText().trim().equals(""))
        {
            workingCopy.setDescription(inputDescription.getText());
            printProfile();
        }
    }


    @FXML
    TextField inputPNameField;
    private List<DrugClass> drugClassesTransient;
    private SuggestionProvider<String> autocompletePNameList;

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
                {
                    workingCopy.removeDrugClass(dc);
                    break;
                }

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

    public void onPharmacologyAddButton (ActionEvent actionEvent) throws IOException
    {
        Stage stage = new Stage();
        URL newPharmacology = Paths.get("./src/main/resources/layout/Pharmacology_POPUP.fxml").toUri().toURL();
        FXMLLoader loader = new FXMLLoader(newPharmacology);
        Parent root = loader.load();

        Pharmacology_POPUP_CTRL controller = loader.getController();
        controller.setWorkingCopy(this.workingCopy);
        controller.setEntityManager(this.entityManager);
        controller.setIsMidTransaction(isMidTransaction);

        Scene scene = new Scene(root);

        stage.setTitle("Add/Edit Pharmacology Profile");
        stage.setOnCloseRequest(event->printProfile());
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.show();
    }

    public void onUsageAddButton (ActionEvent actionEvent) throws IOException
    {
        Stage stage = new Stage();
        URL newUsage = Paths.get("./src/main/resources/layout/Usage_POPUP.fxml").toUri().toURL();
        FXMLLoader loader = new FXMLLoader(newUsage);
        Parent root = loader.load();

        Usage_POPUP_CTRL controller = loader.getController();
        controller.setWorkingCopy(this.workingCopy);
        controller.setEntityManager(this.entityManager);
        controller.setIsMidTransaction(isMidTransaction);

        Scene scene = new Scene(root);

        stage.setTitle("Add/Edit Indications");
        stage.setOnCloseRequest(event->printProfile());
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.show();
    }

    public void onInteractionAddButton (ActionEvent actionEvent) throws IOException
    {
        Stage stage = new Stage();
        URL newInteraction = Paths.get("./src/main/resources/layout/Interaction_POPUP.fxml").toUri().toURL();
        FXMLLoader loader = new FXMLLoader(newInteraction);
        Parent root = loader.load();

        Interaction_POPUP_CTRL controller = loader.getController();
        controller.setWorkingCopy(this.workingCopy);
        controller.setEntityManager(this.entityManager);
        controller.setIsMidTransaction(isMidTransaction);

        Scene scene = new Scene(root);

        stage.setTitle("Add/Edit Interactions");
        stage.setOnCloseRequest(event->printProfile());
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.show();
    }

    public void onDrugEditButton (ActionEvent actionEvent) throws IOException
    {
        Stage newSearch = General_SEARCH_CTRL.readyStage(Drug.class,this,General_SEARCH_CTRL.Mode_DGEditDG,
                "Search for Drug", this.entityManager);
        newSearch.show();
    }

    @FXML
    TextFlow profileTextFlow;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        Platform.runLater(()->{
            populateCBox();

            TypedQuery query = entityManager.createNamedQuery(DrugClass.FIND_ALL_BY_NAME, DrugClass.class).setParameter("searchString", "");
            drugClassesTransient = query.getResultList();

            autocompletePNameList = SuggestionProvider.create(new ArrayList<>());
            for (DrugClass dc : drugClassesTransient)
                autocompletePNameList.addPossibleSuggestions(dc.getName());

            //NOTE: may need to pull from DB here (!!)
            autocompleteBNameList = SuggestionProvider.create(new ArrayList<>());
            for (BrandName bns : workingCopy.getBrandNames())
                autocompleteBNameList.addPossibleSuggestions(bns.toString());

            // two different options to bind autocomplete fields (???)
            TextFields.bindAutoCompletion(inputPNameField, autocompletePNameList);
            new AutoCompletionTextFieldBinding<>(inputBNameField, autocompleteBNameList);
        });
    }

    public void onCancelButton (ActionEvent actionEvent)
    {
        if(isMidTransaction)
            entityManager.getTransaction().rollback();

        Stage popUp = (Stage) inputCNameField.getScene().getWindow();
        popUp.close();
    }

    public void onSaveButton (ActionEvent actionEvent)
    {
        if (!workingCopy.getChemical_name().trim().equals("") && !workingCopy.getDescription().trim().equals(""))
        {
            if (isMidTransaction && !isDuplicate)
            {
                entityManager.persist(workingCopy);
                entityManager.getTransaction().commit();
                isMidTransaction = false;
            }
            else if (isMidTransaction && isDuplicate)
            {
                Drug newTemp = new Drug (workingCopy);
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

    public void onDeleteDrugButton (ActionEvent actionEvent)
    {
        int deleteStatus;
        Query deleteQuery = entityManager.createQuery("DELETE FROM Drug dg WHERE dg = :toDelete");
        int deletedCount = deleteQuery.setParameter("toDelete", workingCopy).executeUpdate();

        for (int i = 0; i < workingCopy.getBrandNames().size(); ++i)
            workingCopy.getBrandNames().remove(workingCopy.getBrandNames().get(i));
        for (int j = 0; j < workingCopy.getPharmacology().size(); ++j)
            workingCopy.getPharmacology().remove(workingCopy.getPharmacology().get(j));
        for (int k = 0; k < workingCopy.getUsages().size(); ++k)
            workingCopy.getUsages().remove(workingCopy.getUsages().get(k));

        if (isMidTransaction)
        {
            deleteStatus = deleteQuery.executeUpdate();
            entityManager.getTransaction().commit();
            isMidTransaction = false;
            isDuplicate = false;
        }
        else
        {
            entityManager.getTransaction().begin();
            deleteStatus = deleteQuery.executeUpdate();
            entityManager.getTransaction().commit();
        }

        cleanUpTransaction();
    }

    protected void cleanUpTransaction ()
    {
        workingCopy = new Drug ("","");
        refreshFields();
        refreshBNameAutoComplete();
        printProfile();
    }

    public void refreshFields ()
    {
        if(workingCopy.getChemical_name() == null || workingCopy.getChemical_name().equals(""))
            inputCNameField.setPromptText("Enter chemical name...");
        else
        {
            inputCNameField.setText(null);
            inputCNameField.setPromptText(workingCopy.getChemical_name());
        }

        inputDescription.setText(workingCopy.getDescription());

        if(workingCopy.getDrugSchedule() == null)
            scheduleCBox.setValue(DEA_Class.DEA.F.toString());
        else
            scheduleCBox.setValue(workingCopy.getDrugSchedule().getSymbol().toString());
    }

    public void printProfile ()
    {
        profileTextFlow.getChildren().removeAll(profileTextFlow.getChildren());

        List<Text> formattedSections = preformat();

        for (int i = 0; i < formattedSections.size(); ++i)
        {
            Text empty;
            switch (formattedSections.get(i).getText())
            {
                case "Name:":
                    empty = new Text("\n" + workingCopy.getChemical_name());
                    formattedSections.add(i+1, empty);
                    break;
                case "Brands:":
                    empty = new Text("\n" + workingCopy.getBrandNames().toString());
                    formattedSections.add(i+1, empty);
                    break;
                case "Family:":
                    String list = "";
                    for (DrugClass dc : workingCopy.getDrugClass())
                        if (workingCopy.getDrugClass().size() == 1)
                            list += dc.getName();
                        else
                            list += dc.getName() + ",";

                    empty = new Text ("\n" + list);
                    formattedSections.add(i+1, empty);
                    break;
                case "DEA Schedule:":
                    empty = (workingCopy.getDrugSchedule() == null) ? new Text ("\n") :
                            new Text("\n" + workingCopy.getDrugSchedule().getSymbol().toString());
                    formattedSections.add(i+1, empty);
                    break;
                case "Description:":
                    empty = new Text("\n" + workingCopy.getDescription());
                    formattedSections.add(i+1, empty);
                    break;
                case "Pharmacology Profiles:":
                    String allPharm = pharmacologyHelper();
                    empty = new Text ("\n" + allPharm);
                    formattedSections.add(i+1, empty);
                    break;
                case "Indication/Dosage:":
                    String allUse = usagesHelper();
                    empty = new Text ("\n" + allUse);
                    formattedSections.add(i+1, empty);
            }
        }

        profileTextFlow.getChildren().addAll(formattedSections);
    }

    protected List<Text> preformat ()
    {
        String preset = "-fx-font-weight: bold; -fx-font-style: italic; -fx-font-size: 12; -fx-font-family: Verdana;" +
                "-fx-fill: dimgray";

        String [] sections = {"Name:", "Brands:", "Family:", "DEA Schedule:", "Description:",
                "Pharmacology Profiles:", "Indication/Dosage:"};

        List<Text> formattedSections = new ArrayList<>();
        for (String word : sections)
        {
            Text blank = new Text("\n\n");
            Text header = new Text(word);
            header.setStyle(preset);
            formattedSections.add(header);
            formattedSections.add(blank);
        }

        return formattedSections;
    }

    protected String pharmacologyHelper ()
    {
        String total = "";

        for (int j = 0; j < workingCopy.getPharmacology().size(); ++j)
        {
            total += String.format ("\n%d:",j + 1);
            total += String.format ("\t[%s] > %s\n","Organ",workingCopy.getPharmacology().get(j).getClearanceOrgan());
            total += String.format ("\t[%s] > %s\n","Enzyme",workingCopy.getPharmacology().get(j).getClearanceEnzyme());
            total += String.format ("\t[%s] > %s\n","Elimination Route",workingCopy.getPharmacology().get(j).getEliminationRoute());
        }

        return total;
    }

    protected String usagesHelper ()
    {
        String total = "";

        for (int j = 0; j < workingCopy.getUsages().size(); ++j)
        {
            total += String.format ("\n%d:",j + 1);
            total += String.format ("\t[%s] > %s\n", "Indication", workingCopy.getUsages().get(j).getIndication());
            total += String.format ("\t[%s] > %s\t[%s] > %s\n", "Dose Range", workingCopy.getUsages().get(j).getDosageRange(),
                    "FDA Approved", workingCopy.getUsages().get(j).getApproval());
        }

        return total;
    }
}

package csulb.cecs323.controller;

import csulb.cecs323.model.*;
import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UserBase_NEW_CTRL implements Initializable
{
    private static final DateTimeFormatter datePickFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private boolean isMidTransaction = false;
    protected void setIsMidTransaction (boolean isMidTransaction) { this.isMidTransaction = isMidTransaction; }
    protected boolean isMidTransaction () { return this.isMidTransaction; }

    private EntityManager entityManager;
    public void setEntityManager(EntityManager entityManager) { this.entityManager = entityManager; }

    private Person workingCopy;
    public Person getWorkingCopy() { return this.workingCopy; }
    public void setWorkingCopy(Person person) { this.workingCopy = person; }

    /*
        Patient demographics section here.
     */
    @FXML
    TextField inputPatientFN;
    @FXML
    TextField inputPatientLN;
    @FXML
    TextField inputPatientMN;
    @FXML
    DatePicker inputPatientDOB;

    public void patient_onEditButton (ActionEvent actionEvent) throws IOException
    {
        Stage search = General_SEARCH_CTRL.readyStage(Patient.class,this,
                General_SEARCH_CTRL.Mode_USEditPT, "Search for Patient", this.entityManager);
        search.show();
    }

    public void patient_onFinalSaveButton (ActionEvent actionEvent)
    {

    }

    public void patient_onFinalDeleteButton (ActionEvent actionEvent)
    {

    }

    public void patient_onUpdateButton (ActionEvent actionEvent)
    {
        String firstName = inputPatientFN.getText().trim();
        String middleName = inputPatientMN.getText().trim();
        String lastName = inputPatientLN.getText().trim();
        LocalDate localDOB = inputPatientDOB.getValue();

        if (!firstName.equals("") && !lastName.equals("") && localDOB != null)
        {
            workingCopy = new Patient(firstName, lastName, localDOB.atStartOfDay(ZoneId.systemDefault()));
            workingCopy.setMiddleName(middleName);

            refreshNameInfo();
        }
    }

    public void refreshNameInfo ()
    {
        inputPatientFN.setPromptText(workingCopy.getFirstName());
        inputPatientLN.setPromptText(workingCopy.getLastName());
        inputPatientMN.setPromptText(workingCopy.getMiddleName());

        String date = ((Patient)workingCopy).getDateOfBirth().format(datePickFormat);
        inputPatientDOB.getEditor().setPromptText(date);
    }

    /*
        Patient phone number records.
     */
    @FXML
    TextField inputPatientNumber;
    @FXML
    ListView<Phone> patientPNListView;
    private final ObservableList<Phone> patientPhoneOBSList = FXCollections.observableArrayList();
    private final ToggleGroup patientPhoneTG = new ToggleGroup();

    @FXML
    ChoiceBox<String> patientPTypeCBox;
    private ObservableList<String> phoneTypes = FXCollections.observableArrayList("Mobile", "Fax", "Home", "Page", "Business");

    public void patientPhone_onSaveButton (ActionEvent actionEvent)
    {
        String type = patientPTypeCBox.getValue();
        String phone = inputPatientNumber.getText().trim();

        if (workingCopy != null && type != null && !phone.equals(""))
        {
            Phone newNumber = new Phone(workingCopy, type, phone);
            patientPhoneOBSList.add(newNumber);
        }
    }

    /*
        Patient address records.
     */
    @FXML
    TextField inputPatientStreet;
    @FXML
    TextField inputPatientCity;
    @FXML
    TextField inputPatientZip;
    @FXML
    TextField inputPatientState;

    @FXML
    ComboBox<String> patientATypeCBox;
    private final ObservableList<String> addressTypes = FXCollections.observableArrayList("Home", "Business", "Work", "Transient");

    @FXML
    ListView<Address> patientADListView;
    private ObservableList<Address> patientAddressOBSList = FXCollections.observableArrayList();
    private final ToggleGroup patientAddressTG = new ToggleGroup();

    public void patientAddress_onSaveButton (ActionEvent actionEvent)
    {
        String street = inputPatientStreet.getText().trim();
        String city = inputPatientCity.getText().trim();
        String zip = inputPatientZip.getText().trim();
        String state = inputPatientState.getText().trim();

        if (workingCopy != null && !street.equals("") && !zip.equals(""))
        {
            Address newAddress = new Address (workingCopy,street, zip);
            newAddress.setCity(city);
            newAddress.setState(state);
            if (patientATypeCBox.getValue() != null)
                newAddress.setType(patientATypeCBox.getValue());

            patientAddressOBSList.add(newAddress);
        }
    }

    /*
        Patient drug allergies.
     */
    @FXML
    ListView<Drug> patientAllergyListView;
    private ObservableList<Drug> patientDrugOBSList = FXCollections.observableArrayList();
    public ObservableList<Drug> getPatientDrugOBSList () { return this.patientDrugOBSList; }

    public void patient_onDrugSearchButton (ActionEvent actionEvent) throws IOException
    {
        Stage search = General_SEARCH_CTRL.readyStage(Drug.class, this,
                General_SEARCH_CTRL.Mode_USAddDG, "Add Drug Allergy", this.entityManager);
        search.show();
    }

    public void patient_onDeleteDrugKey (KeyEvent keyEvent)
    {
        if (keyEvent.getCode().equals(KeyCode.DELETE) && patientAllergyListView.getSelectionModel().getSelectedItems() != null)
        {
            ((Patient) workingCopy).getDrugAllergyList().removeAll(patientAllergyListView.getSelectionModel().getSelectedItems());
            patientDrugOBSList.removeAll(patientAllergyListView.getSelectionModel().getSelectedItems());
        }
    }

    /*
        Patient comorbidity records.
     */
    private List<Comorbidity> comorbiditiesTransientList;

    @FXML
    ListView<Comorbidity> patientCOMListView;
    private ObservableList<Comorbidity> patientCOMOBSList = FXCollections.observableArrayList();

    @FXML
    TextField inputPatientCOMName;
    private SuggestionProvider<String> autocompleteCOMNameList;

    @FXML
    TextField inputPatientCOMStage;
    private SuggestionProvider<String> autocompleteCOMStageList;

    @FXML
    DatePicker inputPatientCOMDate;

    public void patientComorbidity_onSaveButton (ActionEvent actionEvent)
    {
        String stateName = inputPatientCOMName.getText().trim();
        String stage = inputPatientCOMStage.getText().trim();
        LocalDate date = inputPatientCOMDate.getValue();

        if (workingCopy instanceof Patient && !stateName.equals("") && date != null)
        {
            Comorbidity newDisease = new Comorbidity((Patient) workingCopy, stateName, date.atStartOfDay(ZoneId.systemDefault()));
            if(!stage.equals(""))
                newDisease.setStage(stage);

            patientCOMOBSList.add(newDisease);
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        Platform.runLater(()->
        {
            /* Preset autocompletion for Comorbidity inputs */
            Query query = entityManager.createQuery("SELECT cby FROM Comorbidity cby");
            comorbiditiesTransientList = query.getResultList();

            autocompleteCOMNameList = SuggestionProvider.create(new ArrayList<>());
            autocompleteCOMStageList = SuggestionProvider.create(new ArrayList<>());
            for (Comorbidity cby : comorbiditiesTransientList)
            {
                autocompleteCOMNameList.addPossibleSuggestions(cby.getStateName());
                autocompleteCOMStageList.addPossibleSuggestions(cby.getStage());
            }
            new AutoCompletionTextFieldBinding<>(inputPatientCOMName, autocompleteCOMNameList);
            new AutoCompletionTextFieldBinding<>(inputPatientCOMStage, autocompleteCOMStageList);

            patientCOMListView.setItems(patientCOMOBSList);

            /* Preset combo boxes */
            patientPTypeCBox.setItems(phoneTypes);
            patientATypeCBox.setItems(addressTypes);

            /* Custom ListView with Radio buttons */
            patientPNListView.setItems(patientPhoneOBSList);
            patientADListView.setItems(patientAddressOBSList);
            patientPNListView.setCellFactory(listView->new RadioListCellPhone(patientPhoneTG));
            patientADListView.setCellFactory(listView->new RadioListCellAddress(patientAddressTG));

            patientAllergyListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            patientAllergyListView.setItems(patientDrugOBSList);

            /* Bi-directional binding of Date in Text Field of DatePicker to Calendar object */
            bidirectionalDateBind(inputPatientDOB);
            bidirectionalDateBind(inputPatientCOMDate);
        });
    }

    private void bidirectionalDateBind (DatePicker datePicker)
    {
        datePicker.getEditor().focusedProperty().addListener((obj, wasFocused, isFocused)->
        {
            if (!isFocused)
            {
                try {
                    datePicker.setValue(datePicker.getConverter().fromString(datePicker.getEditor().getText()));
                } catch (DateTimeParseException e) {
                    datePicker.getEditor().setText(datePicker.getConverter().toString(datePicker.getValue()));
                }
            }
        });
    }

    private class RadioListCellPhone extends ListCell<Phone>
    {
        RadioButton radioButton;
        ChangeListener<Boolean> radioListener = (src, ov, nv) -> radioChanged(nv);
        WeakChangeListener<Boolean> weakRadioListener = new WeakChangeListener(radioListener);

        public RadioListCellPhone(ToggleGroup personClass)
        {
            radioButton = new RadioButton();
            radioButton.setToggleGroup(personClass);
            radioButton.selectedProperty().addListener(weakRadioListener);
            radioButton.setFocusTraversable(false);
            // let it span the complete width of the list
            // needed in fx8 to update selection state
            radioButton.setMaxWidth(Double.MAX_VALUE);
        }

        protected void radioChanged(boolean selected)
        {
            if (selected && getListView() != null && !isEmpty() && getIndex() >= 0)
                getListView().getSelectionModel().select(getIndex());
        }

        public void updateItem(Phone obj, boolean empty)
        {
            super.updateItem(obj, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
                radioButton.setToggleGroup(null);
            } else {
                radioButton.setText(obj.getNumber());
                radioButton.setSelected(isSelected());
                obj.setIsDefault(isSelected());
                System.out.println (obj.getNumber() + " > " + obj.isDefaultNumber());
                setGraphic(radioButton);
            }
        }
    }

    private class RadioListCellAddress extends ListCell<Address>
    {
        RadioButton radioButton;
        ChangeListener<Boolean> radioListener = (src, ov, nv) -> radioChanged(nv);
        WeakChangeListener<Boolean> weakRadioListener = new WeakChangeListener(radioListener);

        public RadioListCellAddress(ToggleGroup personClass)
        {
            radioButton = new RadioButton();
            radioButton.setToggleGroup(personClass);
            radioButton.selectedProperty().addListener(weakRadioListener);
            radioButton.setFocusTraversable(false);
            // let it span the complete width of the list
            // needed in fx8 to update selection state
            radioButton.setMaxWidth(Double.MAX_VALUE);
        }

        protected void radioChanged(boolean selected)
        {
            if (selected && getListView() != null && !isEmpty() && getIndex() >= 0)
                getListView().getSelectionModel().select(getIndex());
        }

        public void updateItem(Address obj, boolean empty)
        {
            super.updateItem(obj, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
                radioButton.setToggleGroup(null);
            } else {
                radioButton.setText(obj.toString());
                radioButton.setSelected(isSelected());
                obj.setIsDefaultAddress(isSelected());
                System.out.println (obj.toString() + " > " + obj.isDefaultAddress());
                setGraphic(radioButton);
            }
        }
    }
}
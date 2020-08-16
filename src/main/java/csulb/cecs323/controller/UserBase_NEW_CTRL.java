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

    private Person workingCopy = null;
    public Person getWorkingCopy() { return this.workingCopy; }
    public void setWorkingCopy(Person person) { this.workingCopy = person; }

    /*
        Template, generic actions for all UserBase classes.
     */

    public void patient_onFinalSaveButton (ActionEvent actionEvent) { onSavePersonAction(1); }

    public void patient_onFinalDeleteButton (ActionEvent actionEvent)
    {

    }

    public void onSavePersonAction (int discriminatorValue)
    {
        String fullIdentity = workingCopy.getFirstName().trim() + workingCopy.getLastName().trim();
        boolean isSecondaryGood = false;

        switch (discriminatorValue)
        {
            case 1:
                isSecondaryGood = !((Patient)workingCopy).getPrettyDOB().equals("");
                break;
            case 2:
            case 3:
                isSecondaryGood = !workingCopy.getLicenses().isEmpty();
        }

        if (!fullIdentity.equals("") && isSecondaryGood)
        {
            if (isMidTransaction)
            {
                entityManager.persist(workingCopy);
                entityManager.getTransaction().commit();
                isMidTransaction = false;
            }
            else
            {
                entityManager.getTransaction().begin();
                entityManager.persist(workingCopy);
                entityManager.getTransaction().commit();
            }
        }
    }

    /*
        Person class demographics section here.
     */
    @FXML
    TextField inputPatientFN, inputPrescriberFN, inputOperatorFN;
    @FXML
    TextField inputPatientLN, inputPrescriberLN, inputOperatorLN;
    @FXML
    TextField inputPatientMN, inputPrescriberMN, inputOperatorMN;
    @FXML
    TextField inputPrescriberSpecialty, inputOperatorPosition;
    @FXML
    DatePicker inputPatientDOB;

    public void patient_onEditButton (ActionEvent actionEvent) throws IOException
    {
        Stage search = General_SEARCH_CTRL.readyStage(Patient.class,this,
                General_SEARCH_CTRL.Mode_USEditPT, "Search for Patient", this.entityManager);
        search.show();
    }

    public void prescriber_onEditButton (ActionEvent actionEvent) throws IOException
    {
        Stage search = General_SEARCH_CTRL.readyStage(Prescriber.class, this,
                General_SEARCH_CTRL.Mode_USEditMD, "Search for Prescriber", this.entityManager);
        search.show();
    }

    public void patient_onUpdateButton (ActionEvent actionEvent)
    {
        onUpdateDemographicsAction(1);
        refreshNameInfo(1);
    }
    public void prescriber_onUpdateButton (ActionEvent actionEvent)
    {
        onUpdateDemographicsAction(2);
        refreshNameInfo(2);
    }
    public void pharmacist_onUpdateButton (ActionEvent actionEvent)
    {
        onUpdateDemographicsAction(3);
        refreshNameInfo(3);
    }

    public void onUpdateDemographicsAction (int discriminatorValue)
    {
        String firstName = (discriminatorValue == 1) ? inputPatientFN.getText().trim() :
                        (discriminatorValue == 2) ? inputPrescriberFN.getText().trim() :
                        (discriminatorValue == 3) ? inputOperatorFN.getText().trim() : "";

        String lastName = (discriminatorValue == 1) ? inputPatientLN.getText().trim() :
                        (discriminatorValue == 2) ? inputPrescriberLN.getText().trim() :
                        (discriminatorValue == 3) ? inputOperatorLN.getText().trim() : "";

        String middleName = (discriminatorValue == 1) ? inputPatientMN.getText().trim() :
                        (discriminatorValue == 2) ? inputPrescriberMN.getText().trim() :
                        (discriminatorValue == 3) ? inputOperatorMN.getText().trim() : "";

        String miscField = ""; LocalDate localDOB = null;
        if (discriminatorValue == 1)
            localDOB = inputPatientDOB.getValue();
        else
            miscField = (discriminatorValue == 2) ? inputPrescriberSpecialty.getText().trim() :
                (discriminatorValue == 3) ? inputOperatorPosition.getText().trim() : "";

        if (!firstName.equals("") && !lastName.equals("") && (localDOB != null || !miscField.equals("")))
        {
            if (!isMidTransaction)
            {
                workingCopy = (discriminatorValue == 1) ? new Patient(firstName, lastName, localDOB) :
                        (discriminatorValue == 2) ? new Prescriber (firstName, lastName, miscField) :
                        (discriminatorValue == 3) ? new Pharmacist (firstName, lastName, miscField) : null;
            }
            else
            {
                workingCopy.setFirstName(firstName);
                workingCopy.setLastName(lastName);

                if (discriminatorValue == 1)
                    ((Patient)workingCopy).setDateOfBirth(localDOB);
                else if (discriminatorValue == 2)
                    ((Prescriber)workingCopy).setSpecialty(miscField);
                else
                    ((Pharmacist)workingCopy).setPosition(miscField);
            }
            workingCopy.setMiddleName(middleName);
        }
    }

    public void refreshNameInfo (int discriminatorValue)
    {
        switch(discriminatorValue)
        {
            case 1:
                inputPatientFN.setText(workingCopy.getFirstName());
                inputPatientLN.setText(workingCopy.getLastName());
                inputPatientMN.setText(workingCopy.getMiddleName());

                String date = ((Patient)workingCopy).getDateOfBirth().format(datePickFormat);
                inputPatientDOB.getEditor().setText(date);
                break;
            case 2:
                inputPrescriberFN.setText(workingCopy.getFirstName());
                inputPrescriberLN.setText(workingCopy.getLastName());
                inputPrescriberMN.setText(workingCopy.getMiddleName());
                inputPrescriberSpecialty.setPromptText(((Prescriber)workingCopy).getSpecialty());
                break;
            case 3:
                inputOperatorFN.setText(workingCopy.getFirstName());
                inputOperatorLN.setText(workingCopy.getLastName());
                inputOperatorMN.setText(workingCopy.getMiddleName());
                inputOperatorPosition.setText(((Pharmacist)workingCopy).getPosition());
        }
    }

    /*
        Person phone number records.
     */
    @FXML
    TextField inputPatientNumber, inputPrescriberPNumber, inputOperatorPNumber;
    @FXML
    ListView<Phone> patientPNListView, prescriberPNListView, operatorPNListView;

    private final ObservableList<Phone> patientPhoneOBSList = FXCollections.observableArrayList();
    private final ObservableList<Phone> prescriberPhoneOBSList = FXCollections.observableArrayList();
    private final ObservableList<Phone> operatorPhoneOBSList = FXCollections.observableArrayList();

    private final ToggleGroup patientPhoneTG = new ToggleGroup();
    private final ToggleGroup prescriberPhoneTG = new ToggleGroup();
    private final ToggleGroup operatorPhoneTG = new ToggleGroup();

    @FXML
    ChoiceBox<String> patientPTypeCBox, prescriberPTypeCBox, operatorPTypeCBox;
    private final ObservableList<String> phoneTypes = FXCollections.observableArrayList("Mobile", "Fax", "Home", "Page", "Business");

    public void patientPhone_onSaveButton (ActionEvent actionEvent) { onSavePhoneAction(1); }
    public void prescriberPhone_onSaveButton (ActionEvent actionEvent) { onSavePersonAction(2); }
    public void operatorPhone_onSaveButton (ActionEvent actionEvent) { onSavePhoneAction(3); }

    public void onSavePhoneAction (int discriminatorValue)
    {
        String type = (discriminatorValue == 1) ? patientPTypeCBox.getValue() :
                        (discriminatorValue == 2) ? prescriberPTypeCBox.getValue() :
                        (discriminatorValue == 3) ? operatorPTypeCBox.getValue() : null;
        String phone = (discriminatorValue == 1) ? inputPatientNumber.getText().trim() :
                        (discriminatorValue == 2) ? inputPrescriberPNumber.getText().trim() :
                        (discriminatorValue == 3) ? inputOperatorPNumber.getText().trim() : "";

        if (workingCopy != null && type != null && !phone.equals(""))
        {
            Phone newNumber = new Phone(workingCopy, type, phone);

            if (discriminatorValue == 1)
                patientPhoneOBSList.add(newNumber);
            else if (discriminatorValue == 2)
                prescriberPhoneOBSList.add(newNumber);
            else
                operatorPhoneOBSList.add(newNumber);
        }
    }

    public void refreshPhoneList (int discriminatorValue)
    {
        if (discriminatorValue == 1)
            patientPhoneOBSList.setAll(workingCopy.getPhoneList());
        else if (discriminatorValue == 2)
            prescriberPhoneOBSList.setAll(workingCopy.getPhoneList());
        else
            operatorPhoneOBSList.setAll(workingCopy.getPhoneList());
    }

    /*
        Person address records.
     */
    @FXML
    TextField inputPatientStreet, inputPrescriberStreet, inputOperatorStreet;
    @FXML
    TextField inputPatientCity, inputPrescriberCity, inputOperatorCity;
    @FXML
    TextField inputPatientZip, inputPrescriberZip, inputOperatorZip;
    @FXML
    TextField inputPatientState, inputPrescriberState, inputOperatorState;

    @FXML
    ComboBox<String> patientATypeCBox, prescriberATypeCBox, operatorATypeCBox;
    private final ObservableList<String> addressTypes = FXCollections.observableArrayList("Home", "Business", "Work", "Transient");

    @FXML
    ListView<Address> patientADListView, prescriberADListView, operatorADListView;
    private final ObservableList<Address> patientAddressOBSList = FXCollections.observableArrayList();
    private final ObservableList<Address> prescriberAddressOBSList = FXCollections.observableArrayList();
    private final ObservableList<Address> operatorAddressOBSList = FXCollections.observableArrayList();

    private final ToggleGroup patientAddressTG = new ToggleGroup();
    private final ToggleGroup prescriberAddressTG = new ToggleGroup();
    private final ToggleGroup operatorAddressTG = new ToggleGroup();

    public void patientAddress_onSaveButton (ActionEvent actionEvent) { onSaveAddressAction(1); }
    public void prescriberAddress_onSaveButton (ActionEvent actionEvent) { onSaveAddressAction(2); }
    public void operatorAddress_onSaveButton (ActionEvent actionEvent) { onSaveAddressAction(3); }

    public void onSaveAddressAction (int discriminatorValue)
    {
        String street = null, city = null, zip = null, state = null, type = null;

        switch (discriminatorValue)
        {
            case 1:
                street = inputPatientStreet.getText().trim();
                city = inputPatientCity.getText().trim();
                zip = inputPatientZip.getText().trim();
                state = inputPatientState.getText().trim();
                type = patientATypeCBox.getValue();
                break;
            case 2:
                street = inputPrescriberStreet.getText().trim();
                city = inputPrescriberCity.getText().trim();
                zip = inputPrescriberZip.getText().trim();
                state = inputPrescriberState.getText().trim();
                type = prescriberATypeCBox.getValue();
                break;
            case 3:
                street = inputOperatorStreet.getText().trim();
                city = inputOperatorCity.getText().trim();
                zip = inputOperatorZip.getText().trim();
                state = inputOperatorState.getText().trim();
                type = operatorATypeCBox.getValue();
        }

        if (workingCopy != null && !street.equals("") && !zip.equals(""))
        {
            Address newAddress = new Address (workingCopy, type, street, zip, city, state);
            if (discriminatorValue == 1)
                patientAddressOBSList.add(newAddress);
            else if (discriminatorValue == 2)
                prescriberAddressOBSList.add(newAddress);
            else
                operatorAddressOBSList.add(newAddress);
        }
    }

    public void refreshAddressList (int discriminatorValue)
    {
        if (discriminatorValue == 1)
            patientAddressOBSList.setAll(workingCopy.getAddresses());
        else if (discriminatorValue == 2)
            prescriberAddressOBSList.setAll(workingCopy.getAddresses());
        else
            operatorAddressOBSList.setAll(workingCopy.getAddresses());
    }


    /*
        Patient drug allergies.
     */
    @FXML
    ListView<Drug> patientAllergyListView;
    private final ObservableList<Drug> patientDrugOBSList = FXCollections.observableArrayList();
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

    public void refreshDrugAllergyList () { patientDrugOBSList.setAll(((Patient) workingCopy).getDrugAllergyList()); }

    /*
        Patient comorbidity records.
     */
    private List<Comorbidity> comorbiditiesTransientList;

    @FXML
    ListView<Comorbidity> patientCOMListView;
    private final ObservableList<Comorbidity> patientCOMOBSList = FXCollections.observableArrayList();

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

    /*
        Prescriber/Operator certification records.
     */
    @FXML
    ListView<ProviderCertification> prescriberLCListView, operatorLCListView;
    private final ObservableList<ProviderCertification> prescriberLCOBSList = FXCollections.observableArrayList();
    private final ObservableList<ProviderCertification> operatorLCOBSList = FXCollections.observableArrayList();

    @FXML
    TextField inputPrescriberLNumber, inputPrescriberLType, inputPrescriberLIssuer;
    @FXML
    TextField inputOperatorLNumber, inputOperatorLType, inputOperatorLIssuer;

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
            prescriberPTypeCBox.setItems(phoneTypes);
            operatorPTypeCBox.setItems(phoneTypes);

            patientATypeCBox.setItems(addressTypes);
            prescriberATypeCBox.setItems(addressTypes);
            operatorATypeCBox.setItems(addressTypes);

            /* Custom ListView with Radio buttons */
            patientPNListView.setItems(patientPhoneOBSList);
            prescriberPNListView.setItems(prescriberPhoneOBSList);
            operatorPNListView.setItems(operatorPhoneOBSList);

            patientADListView.setItems(patientAddressOBSList);
            prescriberADListView.setItems(prescriberAddressOBSList);
            operatorADListView.setItems(operatorAddressOBSList);

            patientPNListView.setCellFactory(listView->new RadioListCellPhone(patientPhoneTG));
            prescriberPNListView.setCellFactory(listView->new RadioListCellPhone(prescriberPhoneTG));
            operatorPNListView.setCellFactory(listView->new RadioListCellPhone(operatorPhoneTG));

            patientADListView.setCellFactory(listView->new RadioListCellAddress(patientAddressTG));
            prescriberADListView.setCellFactory(listView->new RadioListCellAddress(prescriberAddressTG));
            operatorADListView.setCellFactory(listView->new RadioListCellAddress(operatorAddressTG));

            patientAllergyListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            patientAllergyListView.setItems(patientDrugOBSList);

            /* Miscellaneous ListView bindings */
            patientAllergyListView.setItems(patientDrugOBSList);
            patientCOMListView.setItems(patientCOMOBSList);

            prescriberLCListView.setItems(prescriberLCOBSList);
            operatorLCListView.setItems(operatorLCOBSList);

            /* Bi-directional binding of Date in Text Field of DatePicker to Calendar object */
            bidirectionalDateBind(inputPatientDOB);
            bidirectionalDateBind(inputPatientCOMDate);
        });
    }

    public void onCancelButton (ActionEvent actionEvent)
    {
        if(isMidTransaction)
            entityManager.getTransaction().rollback();

        Stage popUp = (Stage) inputPatientFN.getScene().getWindow();
        popUp.close();
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
                radioButton.setText(obj.toString());
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
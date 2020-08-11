package csulb.cecs323.controller;

import csulb.cecs323.model.Address;
import csulb.cecs323.model.Patient;
import csulb.cecs323.model.Person;
import csulb.cecs323.model.Phone;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javax.persistence.EntityManager;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

public class UserBase_NEW_CTRL implements Initializable
{
    private boolean isMidTransaction = false;
    protected void setIsMidTransaction (boolean isMidTransaction) { this.isMidTransaction = isMidTransaction; }
    protected boolean isMidTransaction () { return this.isMidTransaction; }

    private EntityManager entityManager;
    public void setEntityManager(EntityManager entityManager) { this.entityManager = entityManager; }

    private Person workingCopy;

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

    public void patient_onSaveButton (ActionEvent actionEvent)
    {
        String firstName = inputPatientFN.getText().trim();
        String middleName = inputPatientMN.getText().trim();
        String lastName = inputPatientLN.getText().trim();
        LocalDate localDOB = inputPatientDOB.getValue();

        if (!firstName.equals("") && !lastName.equals("") && localDOB != null)
        {
            workingCopy = new Patient(firstName, lastName, localDOB.atStartOfDay(ZoneId.systemDefault()));
            workingCopy.setMiddleName(middleName);
        }
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
        String zip = inputPatientZip.getText().trim();

        if (workingCopy != null && !street.equals("") && !zip.equals(""))
        {
            Address newAddress = new Address (workingCopy,street, zip);
            patientAddressOBSList.add(newAddress);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        Platform.runLater(()->
        {
           patientPTypeCBox.setItems(phoneTypes);
           patientATypeCBox.setItems(addressTypes);

           patientPNListView.setItems(patientPhoneOBSList);
           patientADListView.setItems(patientAddressOBSList);
           patientPNListView.setCellFactory(listView->new RadioListCellPhone(patientPhoneTG));
           patientADListView.setCellFactory(listView->new RadioListCellAddress(patientAddressTG));

           /* Bi-directional binding of Date in Text Field of DatePicker to Calendar object */
           inputPatientDOB.getEditor().focusedProperty().addListener((obj, wasFocused, isFocused)->
           {
               if (!isFocused)
               {
                   try {
                       inputPatientDOB.setValue(inputPatientDOB.getConverter().fromString(inputPatientDOB.getEditor().getText()));
                   } catch (DateTimeParseException e) {
                       inputPatientDOB.getEditor().setText(inputPatientDOB.getConverter().toString(inputPatientDOB.getValue()));
                   }
               }
           });

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

/* CUSTOM LISTCELL CLASS, DEPRECATED


    private class PhoneObject extends HBox
    {
        private CheckBox defaultChoice = new CheckBox();
        private Label listedNumber = new Label();

        public PhoneObject () {}
        public PhoneObject (String number, boolean defaulted)
        {
            this.setSpacing(10);
            defaultChoice.setSelected(defaulted);
            listedNumber.setText(number);

            this.getChildren().addAll(defaultChoice, listedNumber);
        }

        public void setDefaultNumber () { defaultChoice.setSelected(true); }
        public void unsetDefaultNumber () { defaultChoice.setSelected(false); }

        public void setListedNumber (String number) { listedNumber.setText(number); }
    }
 */
/*
 * Licensed under the Academic Free License (AFL 3.0).
 *     http://opensource.org/licenses/AFL-3.0
 *
 *  This code is distributed to CSULB students in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, other than educational.
 *
 *  2020 William Duong <william.n.duong@outlook.com>
 *
 */

package csulb.cecs323.app;

import csulb.cecs323.controller.MainWindowCTRL;
import csulb.cecs323.model.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.persistence.*;
import java.net.URL;
import java.nio.file.Paths;
import java.time.*;
import java.util.*;
import java.util.logging.Logger;

import static csulb.cecs323.model.DEA_Class.DEA.*;

public class LastGENApp extends Application
{
   private EntityManager entityManager;

   private static final Logger LOGGER = Logger.getLogger(LastGENApp.class.getName());

   /* CONSTRUCTORS */
   public LastGENApp () {}
   public LastGENApp (EntityManager manager) { this.entityManager = manager; }

   public static void main (String[] args) { launch(args); }

   public void loadInitialData ()
   {
      /////////////////////////////////////////////////////////////////////////////////////////////////////////////
      // DRUG DATA
      /////////////////////////////////////////////////////////////////////////////////////////////////////////////
      for (DEA_Class.DEA object : INITIAL_DEA_SCHEDULE.keySet())
      {
         DEA_Class temp = new DEA_Class(object, INITIAL_DEA_SCHEDULE.get (object));

         entityManager.persist (temp);
      }

      // retrieve 'F' class from DB
      Query query = entityManager.createNamedQuery(DEA_Class.RETRIEVE_A_DEA_CLASS, DEA_Class.class);
      query.setParameter("deaSymbol", DEA_Class.DEA.F);
      DEA_Class temp1 = (DEA_Class) query.getResultList().get(0);

      DrugClass statin = new DrugClass ("STNS", "Statins");

      Drug zocor = new Drug ("Simvastatin", "Zocor", "Cholesterol medication, CYP3A4 metabolite.", statin);
      zocor.setDrugSchedule(temp1);
      zocor.addBrandName("FloLipid");

      Drug lotensin = new Drug ("Benazepril", "Blood pressure medication");
      lotensin.addBrandName("Lotensin");
      lotensin.setDrugSchedule(temp1);

      DrugClass antiINF = new DrugClass ("ATIF", "Anti-Infective");
      DrugClass macrolide = new DrugClass ("MACR", "Macrolides");
      DrugClass metabolics = new DrugClass ("META", "Metabolic Agents");
      DrugClass testClass = new DrugClass ("TSTC", "Test Class");
      entityManager.persist(testClass);

      metabolics.addSubclass(statin);
      antiINF.addSubclass(macrolide);
      testClass.addDrug(lotensin);

      ClassClassIX statinMacrolide = new ClassClassIX (statin, macrolide, "Macrolide 3A4 inhibition, check Statin.", 2);

      entityManager.persist (zocor);
      entityManager.persist (statinMacrolide);
      entityManager.persist (antiINF);
      entityManager.persist (macrolide);
      entityManager.persist (statin);
      entityManager.persist (metabolics);
      entityManager.persist (testClass);

      /////////////////////////////////////////////////////////////////////////////////////////////////////////////
      // PERSON DATA
      /////////////////////////////////////////////////////////////////////////////////////////////////////////////
      ProviderCertification temp = new ProviderCertification();
      temp.setType("DEA");
      temp.setNumber("RPG12345");
      temp.setIssuer("DEA");
      Prescriber firstPerson = new Prescriber ("William", "Duong", temp);
      Address newAddress = new Address (firstPerson, "office", "25292 Dayton Drive", "92630");

      ProviderCertification temp2 = new ProviderCertification();
      temp2.setType("State License");
      temp2.setNumber("RPH64794");
      temp2.setIssuer("California");
      Pharmacist thirdPerson = new Pharmacist ("Piseth", "Sam", temp2);

      LocalDate birth = LocalDate.of(1988, Month.JANUARY, 28);
      Patient seconPerson = new Patient ("Jackie", "Duong", birth);
      Address anotherAdd = new Address (seconPerson, "home", "25292 Dayton Drive", "92630");
      Phone someNumber = new Phone (seconPerson, "Mobile", "9498132974");
      seconPerson.addDrugAllergy(lotensin);

      Prescription newRx = new Prescription(seconPerson, firstPerson, LocalDateTime.now());
      RxLine rxLine = new RxLine(newRx, zocor, thirdPerson, 20, 30, 1);
      newRx.addRxLine(rxLine.createFill(15));

      entityManager.persist(firstPerson);
      entityManager.persist(seconPerson);
      entityManager.persist(thirdPerson);
      entityManager.persist(newRx);
   }

   @Override
   public void start(Stage primaryStage) throws Exception
   {
      /* Initialize the database. */
      /////////////////////////////////////////////////////////////////////////////////////////////////////////////
      /////////////////////////////////////////////////////////////////////////////////////////////////////////////
      LOGGER.fine("Creating EntityManagerFactory and EntityManager");
      EntityManagerFactory factory = Persistence.createEntityManagerFactory("LastGEN_PU");
      EntityManager manager = factory.createEntityManager();
      LastGENApp lastGEN = new LastGENApp (manager);

      LOGGER.fine("Begin of Transaction");
      EntityTransaction tx = manager.getTransaction();
      tx.begin();

//      lastGEN.loadInitialData();

      tx.commit();
      LOGGER.fine("End of Transaction");

      /////////////////////////////////////////////////////////////////////////////////////////////////////////////
//      Query query = lastGEN.entityManager.createQuery("SELECT ppl.drugAllergyList FROM Patient ppl WHERE ppl.lastName = 'Duong'");
//      List<Drug> results = query.getResultList();
//      for (int i = 0; i < results.size(); ++i)
//         System.out.println ("Person " + (i + 1) + String.format("[%s]", results.get(i)));

//      Query first = lastGEN.entityManager.createNamedQuery(Drug.FIND_ALL_BY_NAME, Drug.class).setParameter("searchString", "sim");
//      Drug drugOne = (Drug) first.getResultList().get(0);
//      Drug drugTwo = (Drug) first.setParameter("searchString", "bena").getResultList().get(0);
//
//      Query second = lastGEN.entityManager.createQuery("SELECT DISTINCT ppl FROM Patient ppl WHERE ppl.drugAllergyList = :drug1 or ppl.drugAllergyList = :drug2");
//      second.setParameter("drug1", drugOne);
//      second.setParameter("drug2", drugTwo);
//      List<Object> results = second.getResultList();
//
//      for (Object index : results)
//         System.out.println ("Result> " + index.toString());

//      Query query = lastGEN.entityManager.createQuery("SELECT rx FROM Prescription rx INNER JOIN Patient pt ON pt = rx.patient WHERE pt.lastName = 'Duong'");
//      Prescription result = (Prescription) query.getResultList().get(0);
//
//      String dateWritten = result.getPrettyDateWritten();
//      String patient = result.getPatient().toString();
//      List<RxLine> history = result.getRxLines();
//      System.out.println (dateWritten + " : " + patient);
//      for (RxLine line : history)
//         System.out.println (line + "\tRemaining: " + line.getQuantityRemaining());

//      TypedQuery<Patient> query = lastGEN.entityManager.createNamedQuery(Patient.FIND_ALL_BY_NAME, Patient.class);
//      query.setParameter("ptLastName", "uon").setParameter("ptFirstName", "ac");
//      List<Patient> result = query.getResultList();

//      TypedQuery<Patient> query = lastGEN.entityManager.createNamedQuery(Patient.FIND_ALL_BY_DOB, Patient.class);
//      query.setParameter("ptDOB", LocalDate.of(1988,1,28));
//      List<Patient> result = query.getResultList();
//
//      for (Patient pt: result)
//         System.out.println (pt);


      /////////////////////////////////////////////////////////////////////////////////////////////////////////////

      /* Extract Layout */
      URL mainWindowScene = Paths.get("./src/main/resources/layout/MainWindow.fxml").toUri().toURL();
      FXMLLoader loader = new FXMLLoader(mainWindowScene);
      Parent root = loader.load();

      /* Pass the next Stage the EntityManager for transactions */
      MainWindowCTRL controller = loader.getController();
      controller.setEntityManager(lastGEN.entityManager);

      // to apply style to scene, need to instantiate a 'Scene' -- otherwise, we could do anonymous object while 'staging'
      Scene scene = new Scene (root);

      // "staging"
      primaryStage.setTitle("LastGEN");
      primaryStage.setScene(scene);
      primaryStage.show();

      primaryStage.setOnCloseRequest( event -> {
         System.out.println("Closing Primary Stage");

         Platform.exit();
         System.exit(0);
      });
   }

   /* REQUIRED as initial data */
   private static final HashMap<DEA_Class.DEA, String> INITIAL_DEA_SCHEDULE;
   static
   {
      INITIAL_DEA_SCHEDULE = new HashMap<>();

      INITIAL_DEA_SCHEDULE.put (DEA_Class.DEA.INV, "Substances still in R&D or clinical trial phase -- awaiting approvals.");
      INITIAL_DEA_SCHEDULE.put (I, "Substances with no accepted medical use. High abuse potential.");
      INITIAL_DEA_SCHEDULE.put (II, "High abuse potential with severe psychological/physiological dependence.");
      INITIAL_DEA_SCHEDULE.put (III, "Moderate to low abuse potential and development of dependence.");
      INITIAL_DEA_SCHEDULE.put (IV, "Low potential for abuse and low risk of dependence.");
      INITIAL_DEA_SCHEDULE.put (V, "Low potential for abuse/dependence -- limited quantities of specific narcotics.");
      INITIAL_DEA_SCHEDULE.put (F, "Substances used for therapy requiring prescription. No potential for abuse.");
      INITIAL_DEA_SCHEDULE.put (OTC, "Substances available for purchase without prescription. No FDA approvals necessary.");
   }
}

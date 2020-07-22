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

import csulb.cecs323.model.ClassClassIX;
import csulb.cecs323.model.DrugClass;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.net.URL;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class LastGENApp extends Application
{
   private EntityManager entityManager;

   private static final Logger LOGGER = Logger.getLogger(LastGENApp.class.getName());

   public LastGENApp () {}
   public LastGENApp (EntityManager manager) {
      this.entityManager = manager;
   }

   public static void main (String[] args)
   {
      LOGGER.fine("Creating EntityManagerFactory and EntityManager");
      EntityManagerFactory factory = Persistence.createEntityManagerFactory("LastGEN_PU");
      EntityManager manager = factory.createEntityManager();
      LastGENApp lastGEN = new LastGENApp (manager);


      // Any changes to the database need to be done within a transaction.
      // See: https://en.wikibooks.org/wiki/Java_Persistence/Transactions

      LOGGER.fine("Begin of Transaction");
      EntityTransaction tx = manager.getTransaction();

      tx.begin();

      lastGEN.loadInitialData();

      tx.commit();
      LOGGER.fine("End of Transaction");

      launch(args);
   }

   public void loadInitialData ()
   {
      DrugClass antiINF = new DrugClass ("ATIF", "Anti-Infective");
      DrugClass macrolide = new DrugClass ("MACR", "Macrolides");
      DrugClass metabolics = new DrugClass ("META", "Metabolic Agents");
      DrugClass statin = new DrugClass ("STNS", "Statins");

      metabolics.addSubclass(statin);
      antiINF.addSubclass(macrolide);

      ClassClassIX statinMacrolide = new ClassClassIX (statin, macrolide, "Macrolide 3A4 inhibition, check Statin.", 2);

      entityManager.persist (statinMacrolide);
      entityManager.persist (antiINF);
      entityManager.persist (macrolide);
      entityManager.persist (statin);
      entityManager.persist (metabolics);
   }

   @Override
   public void start(Stage primaryStage) throws Exception
   {
      URL sample = Paths.get("./src/main/resources/layout/sample.fxml").toUri().toURL();
      URL style = Paths.get("./src/main/java/csulb/cecs323/app/sampleStyle.css").toUri().toURL();
      // load "scene" from whatever is configured in FXML file instead of creating here in 'start'
      Parent root = FXMLLoader.load(sample);

      // stage = frame/window, scene = contents of frame
      primaryStage.setTitle("FirstJFX");

      // to apply style to scene, need to instantiate a 'Scene' -- otherwise, we could do anonymous object while 'staging'
      Scene scene = new Scene (root); // can also specify size of scene/contents here, but already configured in FXML?
//      scene.getStylesheets().add(style.toExternalForm());

      // "staging"
      primaryStage.setScene(scene);
      primaryStage.show();
   }
}

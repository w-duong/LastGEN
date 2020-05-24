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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.logging.Logger;

/**
 * A simple application to demonstrate how to persist an object in JPA.
 *
 * This is for demonstration and educational purposes only.
 */
public class LastGENApp
{
   private EntityManager entityManager;

   private static final Logger LOGGER = Logger.getLogger(LastGENApp.class.getName());

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
}

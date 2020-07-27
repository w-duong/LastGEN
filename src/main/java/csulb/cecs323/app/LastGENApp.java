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

import csulb.cecs323.model.*;
import csulb.cecs323.model.Menu.ClassDBsubmodule;
import csulb.cecs323.model.Menu.DrugDBMenu;

import javax.persistence.*;
import java.util.List;
import java.util.logging.Logger;

import java.util.HashMap;

import static csulb.cecs323.model.DEA_Class.DEA.*;

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

//      LOGGER.fine("Begin of Transaction");
//      EntityTransaction tx = manager.getTransaction();
//
//      tx.begin();
//
//      lastGEN.loadInitialData();
//
//      tx.commit();
//      LOGGER.fine("End of Transaction");

      // (1)
//      DrugDBMenu testModule = new DrugDBMenu(lastGEN.entityManager);
//      testModule.display();

      // (2)
//      lastGEN.testDelete();

      // (3)
      lastGEN.randomQuery();
   }

   public void randomQuery ()
   {
      System.out.println ("FIRST QUERY >>>>>>> Retrieve all DEA classes and every Drug within that category");

      List<DEA_Class> deaClassList = entityManager.createNamedQuery(DEA_Class.FIND_ALL, DEA_Class.class).getResultList();
      for (DEA_Class result : deaClassList)
      {
         System.out.println (result);

         TypedQuery query = entityManager.createNamedQuery(DEA_Class.SCHEDULE_OF_DRUGS, Drug.class);
         query.setParameter("drugSchedule", result.getSymbol());
         List<Drug> drugList = query.getResultList();

         for (Drug inList : drugList)
            System.out.println (inList);
      }

      System.out.println ("SECOND QUERY >>>>>> Retrieve every drug in DB and every interaction they have");

      List<Drug> drugList = entityManager.createNamedQuery(Drug.FIND_ALL_BY_NAME, Drug.class).setParameter("searchString", "").getResultList();
      for (Drug drug : drugList)
      {
         System.out.println (drug);
         System.out.println ("=================");
         System.out.println ("NUMBER OF DRUG INTERACTIONS: " + drug.getInterxAsBase().size());

         for (DrugDrugIX interx : drug.getInterxAsBase())
            System.out.println("\t" + interx);
      }

      System.out.println ("FINISHED QUERY");
   }

   public void loadInitialData ()
   {
      for (DEA_Class.DEA object : INITIAL_DEA_SCHEDULE.keySet())
      {
         DEA_Class temp = new DEA_Class(object, INITIAL_DEA_SCHEDULE.get (object));

         entityManager.persist (temp);
      }

      entityManager.flush();

      // retrieve 'F' class from DB
      Query query = entityManager.createNamedQuery(DEA_Class.RETRIEVE_A_DEA_CLASS, DEA_Class.class);
      query.setParameter("deaSymbol", DEA_Class.DEA.F);
      DEA_Class temp1 = (DEA_Class) query.getResultList().get(0);

      // add classes
      //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      DrugClass antiINF = new DrugClass ("ATIF", "Anti-Infective");
      DrugClass macrolide = new DrugClass ("MACR", "Macrolides");
      DrugClass metabolics = new DrugClass ("META", "Metabolic Agents");
      DrugClass statin = new DrugClass ("STNS", "Statins");
      DrugClass antacids = new DrugClass ("ATAC", "Antacids");
      //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

      // add drugs
      //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      Drug zocor = new Drug ("Simvastatin", "Zocor", "Cholesterol medication, CYP3A4 metabolite.", statin);
//      DEA_Class temp1 = new DEA_Class(DEA_Class.DEA.F, INITIAL_DEA_SCHEDULE.get(F));
      zocor.setDrugSchedule(temp1);
      zocor.addBrandName("FloLipid");

      Drug coumadin = new Drug ("Warfarin", "Coumadin", "Blood thinner, anti-platelets", metabolics);
      coumadin.setDrugSchedule(temp1);
      Drug prilosec = new Drug ("Omeprazole", "Prilosec", "Proton pump inhibitor", antacids);
      prilosec.setDrugSchedule(temp1);
      //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      entityManager.persist (prilosec);
      entityManager.persist (coumadin);

      // operations
      //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      metabolics.addSubclass(statin);
      antiINF.addSubclass(macrolide);

      coumadin.addInterxAsBase (prilosec, "PPI inhibition of CYP3a4", 2);
      ClassClassIX statinMacrolide = new ClassClassIX (statin, macrolide, "Macrolide 3A4 inhibition, check Statin.", 2);
      //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

      // persist
      entityManager.persist (antacids);
      entityManager.persist (statinMacrolide);
      entityManager.persist (antiINF);
      entityManager.persist (macrolide);
      entityManager.persist (statin);
      entityManager.persist (metabolics);
      entityManager.persist (zocor);
   }

   public void testDelete ()
   {
      entityManager.getTransaction().begin();

      DrugClass aceInh = new DrugClass ("ACEI", "ACE-Inhibitors");
      entityManager.persist (aceInh);

      entityManager.getTransaction().commit();
      ////////// test delete /////////////
      entityManager.getTransaction().begin();

      Query deleteQuery = entityManager.createQuery("DELETE FROM DrugClass dc WHERE dc = :toDelete");
      int deletedCount = deleteQuery.setParameter("toDelete", aceInh).executeUpdate();

      entityManager.getTransaction().commit();
   }

   /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   private static final HashMap <DEA_Class.DEA, String> INITIAL_DEA_SCHEDULE;
   static
   {
      INITIAL_DEA_SCHEDULE = new HashMap<>();

      INITIAL_DEA_SCHEDULE.put (INV, "Substances still in R&D or clinical trial phase -- awaiting approvals.");
      INITIAL_DEA_SCHEDULE.put (I, "Substances with no accepted medical use. High abuse potential.");
      INITIAL_DEA_SCHEDULE.put (II, "High abuse potential with severe psychological/physiological dependence.");
      INITIAL_DEA_SCHEDULE.put (III, "Moderate to low abuse potential and development of dependence.");
      INITIAL_DEA_SCHEDULE.put (IV, "Low potential for abuse and low risk of dependence.");
      INITIAL_DEA_SCHEDULE.put (V, "Low potential for abuse/dependence -- limited quantities of specific narcotics.");
      INITIAL_DEA_SCHEDULE.put (F, "Substances used for therapy requiring prescription. No potential for abuse.");
      INITIAL_DEA_SCHEDULE.put (OTC, "Substances available for purchase without prescription. No FDA approvals necessary.");
   }
}

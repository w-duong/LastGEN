package csulb.cecs323.controller;

import csulb.cecs323.model.DrugDrugIX;
import csulb.cecs323.model.Pharmacology;
import csulb.cecs323.model.Usage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

public class MainWindowCTRL
{
    EntityManager entityManager;
    public void setEntityManager (EntityManager entityManager) { this.entityManager = entityManager; }

    @FXML
    BorderPane mainWindow;

    public void database_drugClass_onMenuSelect (ActionEvent actionEvent) throws IOException
    {
        Stage db_drugClass_newWindow = new Stage();
        URL drugClassScene = Paths.get("./src/main/resources/layout/DrugClass_NEW.fxml").toUri().toURL();
        FXMLLoader loader = new FXMLLoader(drugClassScene);
        Parent root = loader.load();

        DrugClass_NEW_CTRL controller = loader.getController();
        controller.setEntityManager(this.entityManager);
        Scene scene = new Scene (root);

        // "staging"
        db_drugClass_newWindow.setTitle("Add/Edit Drug Class");
        db_drugClass_newWindow.setResizable(false);
        db_drugClass_newWindow.setOnCloseRequest(event->{
            if (controller.isMidTransaction())
                entityManager.getTransaction().rollback();
        });
        db_drugClass_newWindow.initModality(Modality.APPLICATION_MODAL); // prevents user from moving to another Stage until done
        db_drugClass_newWindow.setScene(scene);
        db_drugClass_newWindow.show();
    }

    public void database_drug_onMenuSelect (ActionEvent actionEvent) throws IOException
    {
        Stage db_drug_newWindow = new Stage();
        URL drugScene = Paths.get("./src/main/resources/layout/Drug_NEW.fxml").toUri().toURL();
        FXMLLoader loader = new FXMLLoader(drugScene);
        Parent root = loader.load();

        Drug_NEW_CTRL controller = loader.getController();
        controller.setEntityManager(this.entityManager);
        Scene scene = new Scene (root);

        // "staging"
        db_drug_newWindow.setTitle("Add/Edit Drug");
        db_drug_newWindow.setResizable(false);
        db_drug_newWindow.setOnCloseRequest(event->{
            if (controller.isMidTransaction())
                entityManager.getTransaction().rollback();
        });
        db_drug_newWindow.initModality(Modality.APPLICATION_MODAL); // prevents user from moving to another Stage until done
        db_drug_newWindow.setScene(scene);
        db_drug_newWindow.show();
    }

    public void onMenuSelectClose (ActionEvent actionEvent)
    {
        Query pharmQuery = entityManager.createQuery("SELECT pgy FROM Pharmacology pgy");
        List<Pharmacology> pgyProfiles = pharmQuery.getResultList();
        for (Pharmacology pk : pgyProfiles)
            System.out.println (pk.fullString());

        Query ixQuery = entityManager.createQuery("SELECT ddix FROM DrugDrugIX ddix");
        List<DrugDrugIX> ddixProfiles = ixQuery.getResultList();
        for (DrugDrugIX ix : ddixProfiles)
            System.out.println (ix.toString());

        Query usQuery = entityManager.createQuery ("SELECT us FROM Usage us");
        List<Usage> usProfiles = usQuery.getResultList();
        for (Usage us : usProfiles)
            System.out.println (us.fullString());

        Stage main = (Stage) mainWindow.getScene().getWindow();
        main.close();
    }
}

package csulb.cecs323.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

public class MainWindowCTRL
{
    EntityManager entityManager;
    public void setEntityManager (EntityManager entityManager) { this.entityManager = entityManager; }

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
        db_drugClass_newWindow.initModality(Modality.APPLICATION_MODAL); // prevents user from moving to another Stage until done
        db_drugClass_newWindow.setScene(scene);
        db_drugClass_newWindow.show();
    }
}

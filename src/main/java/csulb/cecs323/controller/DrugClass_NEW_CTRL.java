package csulb.cecs323.controller;

import csulb.cecs323.model.DrugClass;
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

public class DrugClass_NEW_CTRL
{
    EntityManager entityManager;
    public void setEntityManager (EntityManager entityManager) { this.entityManager = entityManager; }

    DrugClass workingCopy = new DrugClass();

    public void drugClass_new_onButtonSelect (ActionEvent actionEvent) throws IOException
    {
        Stage drugClass_generalSearch = new Stage();
        URL generalSearchScene = Paths.get("./src/main/resources/layout/General_SEARCH.fxml").toUri().toURL();
        FXMLLoader loader = new FXMLLoader(generalSearchScene);
        Parent root = loader.load();

        /* Pass EntityManager to next Stage and pass 'WorkingCopy' to set Controller<Type> */
        General_SEARCH_CTRL controller = loader.getController();
        controller.setEntityManager(this.entityManager);
        controller.setWorkingCopy(workingCopy);

        Scene scene = new Scene (root); // can also specify size of scene/contents here, but already configured in FXML?

        // "staging"
        drugClass_generalSearch.setTitle("Search for Drug Class");
        drugClass_generalSearch.initModality(Modality.APPLICATION_MODAL); // prevents user from moving to another Stage until done
        drugClass_generalSearch.setScene(scene);
        drugClass_generalSearch.show();
    }
}

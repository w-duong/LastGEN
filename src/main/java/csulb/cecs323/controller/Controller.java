package csulb.cecs323.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.Random;

// controller class defines functions used to respond to events (such as button clicks from scene)
public class Controller
{
    private static final Random generate = new Random();

    @FXML
    private Label labelOnGenerate; //generates label ID that we can select from within scene builder

    public void generateRandom (ActionEvent event)
    {
        int randNum = generate.nextInt(50) + 1;

        //for purposes of label, we need to convert number to string form
        labelOnGenerate.setText(Integer.toString(randNum));
    }

    public void quitProgram (ActionEvent event)
    {
        System.out.println ("Secondary action...");
        System.exit(0);
    }
}

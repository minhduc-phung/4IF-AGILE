/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/**
 * This is the class that run the tests
 * @see CalculateTourTest
 * @see DijsktraTest
 * @see LoadMapTest
 */
public class TestRunner extends Application {
    /**
     * this is the main method that will be called when the application is launched (and then the tests will be called)
     */
    public static void main(String[] args) {
        JUnitCore junit = new JUnitCore();
        junit.addListener(new TextListener(System.out));
        Result result = junit.run(
                LoadMapTest.class, CalculateTourTest.class);
        resultReport(result);
        Platform.exit();
    }

    /**
     * this method shows in the console the result report of the tests
     * @param result
     */
    public static void resultReport(Result result) {
        System.out.println("Finished. Result: Failures: " +
            result.getFailureCount() + ". Ignored: " +
            result.getIgnoreCount() + ". Tests run: " +
            result.getRunCount() + ". Time: " +
            result.getRunTime() + "ms.");
    }

    @Override
    public void start(Stage stage) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

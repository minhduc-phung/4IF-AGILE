# 4IF-AGILE
Java PLD (Long-duration Project) AGILE


## Installation
This part is here to guide any installation of external libraries that are needed to run the project.

### JavaFX
JavaFX is a library that is used to create graphical interfaces. It is used in this project to create the graphical interface of the application.
To install and setup:
1. Download the JavaFX SDK from [here](https://gluonhq.com/products/javafx/).
2. Extract the archive and copy the folder to anywhere that you like (I recommend to put it in the same folder that you installed Java.).

#### __IntelliJ IDEA__ IDE
3. In the project, go to `File > Project Structure > Libraries` and click on the `+` button.
4. Select `Java` and select the `lib` folder of the JavaFX SDK.
5. Click `Apply` and `OK`.
6. In the project, go to `File > Project Structure > Modules` and select the `Dependencies` tab.
7. Select `javafx.graphics` and click `OK`.
8. Click `Apply` and `OK`.
9. In the project, go to `Run > Edit Configurations...`.
10. Select the `Application` configuration and click on the `+` button.
11. Select `VM Options` and enter the following line:
```
--module-path "path/to/javafx-sdk-11.0.2/lib" --add-modules javafx.controls,javafx.fxml
```
12. __Replace__ `path/to/javafx-sdk-11.0.2/lib` __with the path to the `lib` folder of the JavaFX SDK.__
13. Click `Apply` and `OK`.
14. Run the project.

#### __NetBeans__ IDE
(Not sure if this is correct, it's GitHub CoPilot suggesting all of these.)

3. In the project, go to `Tools > Libraries` and click on the `+` button.
4. Select `Java` and select __the content__ of the `lib` folder of the JavaFX SDK.
5. Click `OK`.
6. In the project properties (right-click the name of the project and go to properties), go to `Run > Set Project Configuration` and select the `Run` tab.
7. Enter the following line in the VM Option:
```
--module-path "path/to/javafx-sdk-11.0.2/lib" --add-modules javafx.controls,javafx.fxml
```
8. __Replace__ `path/to/javafx-sdk-11.0.2/lib` __with the path to the__ `lib` __folder of the JavaFX SDK.__
9. Click `OK`.
10. Run the project.
## Changelog
Feel free to put anything in here so everyone can be up-to-date with the project!

### 23/10/2022
- Finished the service of saving delivery points (Minh and Viet)
- Added the GUI for showing the map read by the .XML file (Duc)

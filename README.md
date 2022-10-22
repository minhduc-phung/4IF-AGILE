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
7. Click on the `+` button and select `Module Dependency`.
8. Select `javafx.graphics` and click `OK`.
9. Click `Apply` and `OK`.
10. In the project, go to `Run > Edit Configurations...`.
11. Select the `Application` configuration and click on the `+` button.
12. Select `VM Options` and enter the following line:
```
--module-path "path/to/javafx-sdk-11.0.2/lib" --add-modules javafx.controls,javafx.fxml
```
13. Replace `path/to/javafx-sdk-11.0.2/lib` with the path to the `lib` folder of the JavaFX SDK.
14. Click `Apply` and `OK`.
15. Run the project.

#### __NetBeans__ IDE
(Not sure if this is correct, it's GitHub CoPilot suggesting all of these.)

3. In the project, go to `Tools > Libraries` and click on the `+` button.
4. Select `Java` and select the `lib` folder of the JavaFX SDK.
5. Click `OK`.
6. In the project, go to `Run > Set Project Configuration` and select the `Run` tab.
7. Click on the `+` button and select `VM Options`.
8. Enter the following line:
```
--module-path "path/to/javafx-sdk-11.0.2/lib" --add-modules javafx.controls,javafx.fxml
```
9. Replace `path/to/javafx-sdk-11.0.2/lib` with the path to the `lib` folder of the JavaFX SDK.
10. Click `OK`.
11. Run the project.
## Changelog
Feel free to put anything in here so everyone can be up-to-date with the project!

### 20/10/2022
- Edited the code of the .XML file reading (Minh and Viet)
- Added the GUI for showing the map read by the .XML file (Duc)

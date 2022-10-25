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
9. Run the file which uses JavaFX by right-clicking the file and click something that looks like `Run Main.main()` (__You will get an error__)
10. On the top right of the IDE, click on the file name and choose `Edit configurations`:
![image](https://user-images.githubusercontent.com/94907884/197814722-4db2c0f4-ca5a-47e4-b809-2ce3deea0f76.png)

11. Select `VM Options`:
![image](https://user-images.githubusercontent.com/94907884/197815076-53ed7bab-cd11-40c1-9bf5-dddf425a686c.png)
12. Copy and paste this into the `VM Option`:
```
--module-path "path/to/javafx-sdk-11.0.2/lib" --add-modules javafx.controls,javafx.fxml
```
13. __Replace__ `path/to/javafx-sdk-11.0.2/lib` __with the path to the `lib` folder of the JavaFX SDK.__
14. Click `Apply` and `OK`.
15. Run the project.

#### __NetBeans__ IDE
(Not sure if this is correct, it's GitHub CoPilot suggesting all of these.)

3. In the project, go to `Tools > Libraries` and click on the `+` button.
4. Select `Java` and select __the content__ of the `lib` folder of the JavaFX SDK.
5. Click `OK`.
6. In the project properties (right-click the name of the project -> `Properties`), select the `Run` tab.
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

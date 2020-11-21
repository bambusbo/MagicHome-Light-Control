import me.g33ry.magic_home_java.Controller;
import me.g33ry.magic_home_java.Discover;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Application {

    static boolean turnOnAtStartup;
    static Controller[] controllers;

    public static void main(String[] args) throws IOException {
        //Discover all MagicHome compatible devices and assign them to an array
        discoverDevices();

        //Read properties file and set variables accordingly
        readPropertiesFile();

        //If enabled, turn the lights on when the application starts
        if (turnOnAtStartup){
            turnAllOn();
        }

        //Initialize the taskbar icon
        initTaskBarIcon();


        turnAllOn();
        turnAllOff();
        
        
        
        
        
        
    }



    private static void discoverDevices() throws IOException {
        controllers = Discover.Scan();
    }

    private static void readPropertiesFile() throws IOException {
        //Initialize properties
        Properties properties = new Properties();
        InputStream propertiesStream = new FileInputStream("config.properties");

        properties.load(propertiesStream);

        //Assign config properties to variables
        turnOnAtStartup = Boolean.parseBoolean(properties.getProperty("turnOnAtStartup"));

        }

    private static void initTaskBarIcon() {
        //Init
        TrayIcon trayIcon = null;
        // Get the SystemTray instance
        SystemTray tray = SystemTray.getSystemTray();
        // Load an image
        Image image = Toolkit.getDefaultToolkit().getImage("resources/icon.png");
        // Create a action listener to listen for default action executed on the tray icon

        ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }

        };

        // Create a popup menu
        PopupMenu popup = new PopupMenu();
        // Create menu item for the default action
        MenuItem exit = new MenuItem("Exit");
        //Add a listener for the Exit button
        exit.addActionListener(listener);
        // Add a popup for the exit button
        popup.add(exit);

        trayIcon = new TrayIcon(image, "MagicHome Light Control", popup);
        // set the TrayIcon properties
        trayIcon.addActionListener(listener);

        // Add the tray image
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.err.println(e);
        }

    }




    private static void turnAllOn() {
        for (Controller controller : controllers){
            controller.setPower(true);
        }
    }

    private static void turnAllOff() {
        for (Controller controller : controllers){
            controller.setPower(false);
        }
    }




}

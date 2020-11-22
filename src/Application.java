import me.g33ry.magic_home_java.Controller;
import me.g33ry.magic_home_java.Discover;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

public class Application {
    static Properties properties;
    static boolean turnOnAtStartup;
    static boolean startAppWithComputer;
    static Controller[] controllers;
    static CheckboxMenuItem turnOnAtStartupCB;
    static CheckboxMenuItem startAppWithComputerCB;
    static InputStream propertiesStream;
    static FileOutputStream propertiesOut;
    static Menu subMenuDevicesPopup;
    static MenuItem refresh;
    static ArrayList<CheckboxMenuItem> devices = new ArrayList<>();



    public static void main(String[] args) throws IOException {
        //Discover all MagicHome compatible devices and assign them to an array
        discoverDevices();
        //Initialize the tray icon
        initTrayIcon();
        //Read properties file and set variables accordingly
        readPropertiesFile();
        //If enabled, turn the lights on when the application starts
        if (turnOnAtStartup){
            turnAllOn();
        }


    }



    private static void discoverDevices() throws IOException {
        controllers = Discover.Scan();
    }

    private static void readPropertiesFile() throws IOException {
        //Initialize properties
        properties = new Properties();
        propertiesStream = new FileInputStream("config.properties");
        properties.load(propertiesStream);

        //Assign config properties to variables
        turnOnAtStartup = Boolean.parseBoolean(properties.getProperty("turnOnAtStartup"));
        startAppWithComputer = Boolean.parseBoolean(properties.getProperty("startAppWithComputer"));
        propertiesStream.close();



        //Set checkboxes to property values
        turnOnAtStartupCB.setState(turnOnAtStartup);
        startAppWithComputerCB.setState(startAppWithComputer);

        }

    private static void initTrayIcon() throws IOException {
        //Init
        TrayIcon trayIcon = null;
        // Get the SystemTray instance
        SystemTray tray = SystemTray.getSystemTray();
        // Load an image
        Image image = ImageIO.read((Application.class.getClassLoader().getResource("icon.png")));

        //Listeners
        ActionListener refreshListener = e -> refreshDevices();
        ActionListener lightsOnListener = e -> turnAllOn();
        ActionListener lightsOffListener = e -> turnAllOff();
        ActionListener exitListener = e -> exitProgram();

        ItemListener turnOnAtStartupCBListener = e -> {
            turnOnAtStartup = turnOnAtStartupCB.getState();
            properties.setProperty("turnOnAtStartup", "" + turnOnAtStartup);
            try {
                propertiesOut = new FileOutputStream("config.properties");
                properties.store(propertiesOut, "Generated properties file");
                propertiesOut.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        };

        ItemListener startAppWithComputerCBListener = e -> {
            startAppWithComputer = startAppWithComputerCB.getState();
            properties.setProperty("startAppWithComputer", ""+startAppWithComputer);
            try {
                propertiesOut = new FileOutputStream("config.properties");
                properties.store(propertiesOut, "Generated properties file");
                propertiesOut.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        };

        // Create a popup menu
        PopupMenu popup = new PopupMenu();

        // Create menu items
        MenuItem exit = new MenuItem("Exit");
        MenuItem lightsOn = new MenuItem("Lights On");
        MenuItem lightsOff = new MenuItem("Lights Off");
        turnOnAtStartupCB = new CheckboxMenuItem("Turn on lights on app start");
        startAppWithComputerCB = new CheckboxMenuItem("Start app with windows");


        // Create a sub popupmenu for known devices
        subMenuDevicesPopup = new Menu("Devices");

        // Create sub menu items


        addDevicesToSubMenu();




        // Add the tray icon items to the popupmenu
        popup.add(subMenuDevicesPopup);
        popup.add(lightsOn);
        popup.add(lightsOff);
        popup.add(startAppWithComputerCB);
        popup.add(turnOnAtStartupCB);
        popup.add(exit);

        // Add sub menu items

        // Add listeners for the buttons / checkboxes
        lightsOn.addActionListener(lightsOnListener);
        lightsOff.addActionListener(lightsOffListener);
        exit.addActionListener(exitListener);
        turnOnAtStartupCB.addItemListener(turnOnAtStartupCBListener);
        startAppWithComputerCB.addItemListener(startAppWithComputerCBListener);
        refresh.addActionListener(refreshListener);


        //Set the tray icon image
        trayIcon = new TrayIcon(image, "MagicHome Light Control", popup);

        // Add the tray image
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.err.println(e);
        }
    }




    private static void addDevicesToSubMenu() {
        //Start by adding the refresh button
        refresh = new MenuItem("Refresh");
        subMenuDevicesPopup.add(refresh);
        // For each discovered controller, add to a menu item array
        for (Controller controller : controllers) {
            devices.add(new CheckboxMenuItem(controller.getIP() + " ->> " + controller.getNAME()));
        }

        // Create and add devices to submenu, and create an item listener for each
        for (CheckboxMenuItem currentItem : devices) {
            currentItem = new CheckboxMenuItem((currentItem.getLabel()));
            subMenuDevicesPopup.add(currentItem);

            // Create item listener
            currentItem.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {

                }
            });
        }
    }
    private static void refreshDevices() {
        //Discover devices again
        try {
            discoverDevices();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Clear device array
        devices.clear();
        //Remove all current devices from the submenu
        subMenuDevicesPopup.removeAll();
        //Add again
        addDevicesToSubMenu();
    }


    private static void exitProgram() {
        System.exit(0);
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

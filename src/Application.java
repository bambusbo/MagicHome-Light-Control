import me.g33ry.magic_home_java.Controller;
import me.g33ry.magic_home_java.Discover;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.net.Socket;
import java.util.Properties;

public class Application {
    private static String IP;
    static Properties properties;
    static boolean turnOnAtStartup;
    static boolean startAppWithComputer;
    static Controller[] controllers;
    static CheckboxMenuItem turnOnAtStartupCB;
    static CheckboxMenuItem startAppWithComputerCB;
    static InputStream propertiesStream;
    static FileOutputStream propertiesOut;

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
        propertiesOut = new FileOutputStream("config.properties");
        propertiesStream = new FileInputStream("config.properties");

        properties.load(propertiesStream);

        //Assign config properties to variables
        turnOnAtStartup = Boolean.parseBoolean(properties.getProperty("turnOnAtStartup"));
        startAppWithComputer = Boolean.parseBoolean(properties.getProperty("startAppWithComputer"));

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
        // Create a action listener to listen for default action executed on the tray icon




        //Listeners
        ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        };

            ActionListener lightsOnListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                turnAllOn();
            }
        };
        ActionListener lightsOffListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                turnAllOff();
            }
        };
        ActionListener exitListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exitProgram();
            }
        };
        ItemListener turnOnAtStartupCBListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                turnOnAtStartup = turnOnAtStartupCB.getState();
                properties.setProperty("turnOnAtStartup", ""+turnOnAtStartup);
                try {
                    properties.store(propertiesOut, "Generated properties file");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        };
        ItemListener startAppWithComputerCBListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                startAppWithComputer = startAppWithComputerCB.getState();
                properties.setProperty("startAppWithComputer", ""+startAppWithComputer);
                try {
                    properties.store(propertiesOut, "Generated properties file");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
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

        //Add listeners for the buttons / checkboxes
        lightsOn.addActionListener(lightsOnListener);
        lightsOff.addActionListener(lightsOffListener);
        exit.addActionListener(exitListener);
        turnOnAtStartupCB.addItemListener(turnOnAtStartupCBListener);
        startAppWithComputerCB.addItemListener(startAppWithComputerCBListener);


        // Add popups for menu items


        popup.add(lightsOn);
        popup.add(lightsOff);
        popup.add(startAppWithComputerCB);
        popup.add(turnOnAtStartupCB);
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

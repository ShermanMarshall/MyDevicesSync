/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mydevicessync;

/**
 *
 * @author admin
 */
public class Constants {
    static final String fileError = "File selected is not a valid file. Add another?";
    static final String fileErrorTitle = "Error with selected file";
    static final String favoritesList = "Favorites";
    static final String messageTitle = "Data from ip: ";
    static final String error = "Error";
    static final String deviceAddFailed = "The device failed to add";
    static final String inputIP = "Input the device IP address";
    static final String inputName = "Input the device name";
    static final String addButtonPrompt = "Add Device";
    static final String noDevices = "No Devices";
    static final String appTitle = "GUI";
    static final String interfaceHeader = "Interface Information";
    static final String inputMessage = "Input Message Here";
    static final String deviceHeader = "Devices";
    static final String removeDevice = "Remove Device";
    static final String sendData = "Send Data";
    static final String IOE = "Message encountered an IOException";
    static final String localhost = "127.0.0.1";
    
    static int sleepInterval = 500;
    
    static String formatInvalidIP (String ip) {
        return "The ip: " + ip + " is invalid";
    }
    static String formatHyphenBreak (String input) {
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < input.length(); x++)
            sb.append("-");
        return sb.toString();
    }
}

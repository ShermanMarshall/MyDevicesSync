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
    static final String fileErrorTitle = "Error with selected file";
    static final String favoritesList = "Favorites";
    static final String messageTitle = "Data from ip: ";
    
    static final String deviceAddFailed = "The device failed to add";
    static final String inputIP = "Input the device IP address";
    static final String inputName = "Input the device name";
    static final String addButtonPrompt = "Add Device";
    static final String noDevices = "No Devices";
    static final String appTitle = "GUI";
    static final String interfaceHeader = "Interface Information";
    static final String inputMessage = "Input Message Below";
    static final String deviceHeader = "Devices";
    static final String removeDevice = "Remove Device";
    static final String sendData = "Send Data";
    static final String IOE = "Message encountered an IOException";
    static final String localhost = "127.0.0.1";
    static final String noDirectoryInFavorites = "No directory in favorites to select from";
    static final String addFileButtonPrompt = "Add File";
    static final String fileSelected = "File selected";
    static final String fileHeader = "File to send: ";
    static final String directoryBackButton = "Back";
    static final String readMessage = "read";
    
    //Errors
    static final String error = "Error";
    static final String connectionIOError = "Connection could not be handled";
    static final String numberFormatError = "Message length parse failed";
    static final String serverRestart = "Server shut down. Restarting";
    static final String configFailError = "Unable to identify network interface";
    static final String writeError = "Error sending data to device";
    
    //File extensions
    static final String txtFile = "txt";    
    static int sleepInterval = 500;    
    static byte colon = (byte) 58;
    static String formatInvalidIP (String ip) { return "The ip: " + ip + " is invalid"; }
    static String formatHyphenBreak (String input) {
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < input.length(); x++)
            sb.append("-");
        return sb.toString();
    }
    static String prepareInterfaceInfo(String ip) {
        StringBuilder info = new StringBuilder();
        
        return info.append("Device IP: " + ip).toString();
    }
    static String prepareFileError (String fileName) {
        StringBuilder prompt = new StringBuilder(" is not a valid file. Add another?");
        if (fileName == null) prompt = prompt.insert(0, "File selected");
        else prompt = prompt.insert(0, fileName);
        return prompt.toString();
    }
    static String prepareFileLabel (String fileName) {
        StringBuilder label = new StringBuilder(fileHeader);
        if (fileName != null)
            label.append(fileName);
        return label.toString();
    }
}

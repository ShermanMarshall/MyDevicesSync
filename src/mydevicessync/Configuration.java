package mydevicessync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import javax.swing.JOptionPane;

/**
 *
 * @author admin
 */
public class Configuration {
    String ip; 
    final String configFile = "config.txt";
    boolean isConfigured;
    
    public Configuration() { 
        File f = new File(configFile);
        try {
            if (f.exists()) {               
                BufferedReader br = new BufferedReader(new FileReader(f));
                String data; ArrayList<String> input = new ArrayList<>();
                
                while ((data = br.readLine()) != null)
                    input.add(data);
                
                br.close();
                ip = input.get(0);
                
                if (ConnectionManager.validateIP(input.get(0)))
                    isConfigured = true;
            } else
                isConfigured = false;
        } catch (IOException e) {
            MyDevicesSync.showErrorMessage("Error initializing application configuration");
            isConfigured = false;
        }        
        getConfiguration();
    }
    
    public void getConfiguration () {
        ArrayList<Interface> data = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            for (int x = 1; nis.hasMoreElements(); x++) {
                    NetworkInterface ni = nis.nextElement();
                    String ifName = ni.getDisplayName();
                    for (InetAddress ia : Collections.list(ni.getInetAddresses()))
                            if (ConnectionManager.validateIP(ia.getHostAddress()))
                                data.add(new Interface(ifName, ia.getHostAddress()));
            }
            Interface[] selections = new Interface[data.size()];
            data.toArray(selections);            
            if (!isConfigured) {
                String message =  "Select the interface and IP address of this computer:";
                selectInterface(selections, message, selections[0]);                
            } else {
                String message = "The saved network interface is not present. Change to an available network interface:";
                Interface current = null;
                
                for (Interface tmp : selections)
                    if (tmp.ip.equals(ip))
                        current = tmp;
                
                if (current == null) { 
                    ip = null;
                    do {
                        selectInterface(selections, message, selections[0]);
                    } while (ip == null);
                }
            }
            saveConfiguration();
        } catch (IOException e) {
            MyDevicesSync.showErrorMessage("Error listing possible IP addresses");
            isConfigured = ConnectionManager.validateIP(ip) ? true : false;                    
        }        
    }
    
    public void selectInterface(Interface[] selections, String message, Interface initial) {        
        Interface chosen = (Interface) JOptionPane.showInputDialog(
                            null,           //ParentComponent
                            message,        //Message
                            ip,             //Title,
                            0,              //Message type
                            null,           //Icon
                            selections,     //selections
                            selections[0]); //Initial selection
        if (chosen != null)
            ip = chosen.ip;                
    }
    
    public void saveConfiguration() {
        File f = new File(configFile);
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(f));
            pw.write(ip);
            pw.close();
        } catch (IOException e) {
            MyDevicesSync.showErrorMessage("Error saving configuration");
        }
    }
    
    private class Interface {
        String name, ip;        
        Interface(String name, String ip) { this.name = name; this.ip = ip; }
        public String toString() {
            return name + ": " + ip;
        }
    }
}

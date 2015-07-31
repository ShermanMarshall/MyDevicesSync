package mydevicessync;

import java.util.ArrayList;
import java.io.*;
import javax.swing.JButton;

public class DeviceManager {
    private ArrayList<Message> messages;
    private ArrayList<Device> devices = null;
    private int deviceIdx = 0;
    private String errorPrompt = "";
    private File content = new File("content.txt");
    DeviceManager manager;
    
    DeviceManager() {
        devices = initDevices();
        if (devices == null)
            errorPrompt = "Error: devices not loaded";
        manager = this;
    }
    
    boolean devicesLoaded() {
        return (devices != null);
    }
    
    String getError() { return errorPrompt; }
    String removeError() { return errorPrompt = ""; }
    int getDeviceSize() { return devices.size(); }
    int getIdx() { return deviceIdx; }
    
    ArrayList<Device> getDevices() {
        return devices;
    }
    
    ArrayList<Device> initDevices() {
        try {
            devices = new ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader(content));
            String input;
            while ((input = br.readLine()) != null) 
                devices.add(new Device(input.split(":"), this));
            br.close();
        } catch (IOException e) {   
            devices = null;     
        }
        if (devices.size() < 0)
            devices = null;
        return devices;
    }
    
    String getDeviceIP() {
        return devices.get(deviceIdx).ip;
    }
    
    void setIdx(JButton button) {
        for (int x = 0; x < devices.size(); x++)
            if (devices.get(x).button == button) {
                deviceIdx = x;
                return;
            }
        deviceIdx = -1;
    }
    
    void addDevice(String name, String ip) {
        devices.add(new Device(name, ip, manager));
        update();
        deviceIdx = -1;
    }
    
    void removeDevice(int index) {
        devices.remove(index);
        update();
        deviceIdx = -1;
    }
    
    /*
    void addMessage(String contents) {
        messages.add(new Message(contents, devices.get(deviceIdx).ip));
    }
    
    void removeMessage(int position) {
        messages.remove(position);
    }
    */
    void update() {
        PrintWriter pw = null;
        if (devices.size() > 0)
            try { 
                pw = new PrintWriter(new FileWriter(content));
                for (Device d : devices) 
                    pw.write(d.toString());
                pw.close();
                errorPrompt = "";
            } catch (IOException e) {
                errorPrompt = "Error: " + e.getMessage();
                if (pw != null)
                    pw.close();
            }
        else {
            content.delete();
            try {
                content.createNewFile();
            } catch (IOException e) {
                errorPrompt = "Error: contents.txt removed";
            }
        }
    }
}

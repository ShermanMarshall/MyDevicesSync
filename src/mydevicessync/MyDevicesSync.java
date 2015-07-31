package mydevicessync;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class MyDevicesSync extends JFrame {
    JTextArea contents;
    JLabel error = new JLabel(""), 
           interfaceHeading = new JLabel("Interface Information"),  
           interfaceSpacer = new JLabel("---------------------"),
           contentHeading = new JLabel("Input Message Here"),
           deviceHeading = new JLabel("Devices");
    static JLabel interfaceInfo = new JLabel();
    JPanel deviceHierarchy, buttonPanel, contentPanel, devicePanel;
    JButton addDevice, removeDevice, send, receive;
    DeviceManager manager;
    ConnectionManager connectionManager;
    Configuration configuration;
    ArrayList<String> fileUploads;
    
    MyDevicesSync() {
        super("Device Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension d = new Dimension(600, 600);
        setBounds(0, 0, d.height, d.width);
        
        connectionManager = new ConnectionManager(this);                
        manager = new DeviceManager();
        contents = new JTextArea(100, 33);
        
        deviceHierarchy = new JPanel();
        deviceHierarchy.setLayout(new BoxLayout(deviceHierarchy, BoxLayout.Y_AXIS));
        deviceHierarchy.add(deviceHeading);
        
        if (manager.devicesLoaded())
            for (Device device : manager.getDevices()) 
                deviceHierarchy.add(device.button);
        else
            deviceHeading.setText("No Devices");
        
        devicePanel = new JPanel();
        devicePanel.setLayout(new BoxLayout(devicePanel, BoxLayout.Y_AXIS));
        
        addDevice = new JButton("Add Device");
            addDevice.addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent e) {
                    String name = JOptionPane.showInputDialog(null,     //Frame size
                                            null,                       //Input Values
                                            "Input the device name",    //Title
                                            JOptionPane.PLAIN_MESSAGE); //Option type
                    if (name == null)
                        return;
                    else {
                        String ip = JOptionPane.showInputDialog(null, 
                                                null,
                                                "Input the device IP address",
                                                JOptionPane.PLAIN_MESSAGE);                        
                        if (ip == null) 
                            return;
                        else {
                            if (ConnectionManager.validateIP(ip)) {
                                manager.addDevice(name, ip);
                                devicePanel.add(manager.getDevices().get(manager.getDeviceSize()-1).button);
                                deviceHierarchy.validate();
                            } else {
                                ip = JOptionPane.showInputDialog(null,                                                           
                                        "The ip: " + ip + " is invalid",
                                        "Input the device IP address",
                                        JOptionPane.PLAIN_MESSAGE);
                                if (ConnectionManager.validateIP(ip)) {
                                    System.out.println(manager.getDeviceSize());
                                    manager.addDevice(name, ip);                                    
                                    
                                    devicePanel.add(manager.getDevices().get(manager.getDeviceSize() - 1).button);
                                    System.out.println(manager.getDeviceSize());
                                    deviceHierarchy.validate();
                                } else
                                    JOptionPane.showConfirmDialog(null, 
                                        "The device failed to add",
                                        "Input the device IP address",
                                        JOptionPane.PLAIN_MESSAGE);
                            }
                        }
                    }                   
                }
            });
            
        removeDevice = new JButton("Remove Device");
            removeDevice.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                   manager.removeDevice(manager.getIdx());
               } 
            });
            
        send = new JButton("Send Data");
            send.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                   //showNewMessage("testing", "testing");
                   //connectionManager.write(manager.getDeviceIP(), contents.getText());
               } 
            });
            
        /*
        receive = new JButton("Receive Data");
            receive.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    
                }
            });
        */
            
        JPanel deviceButtons = new JPanel(new FlowLayout());
        deviceButtons.add(addDevice);
        deviceButtons.add(removeDevice);
        
        deviceHierarchy.add(devicePanel);
        deviceHierarchy.add(deviceButtons);
        
        buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.add(send);
        //buttonPanel.add(receive);
        
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(interfaceHeading);
        contentPanel.add(interfaceInfo);
        contentPanel.add(contentHeading);
        contentPanel.add(contents);
        
        add(error, BorderLayout.NORTH);    
        add(buttonPanel, BorderLayout.SOUTH);
        add(contentPanel, BorderLayout.CENTER);
        add(deviceHierarchy, BorderLayout.WEST);        
        setVisible(true);
        configuration = new Configuration();
    }
    public void addUpload(String filename) {
        if (fileUploads == null)
            fileUploads = new ArrayList<>();
        else
            fileUploads.add(filename);
    }
    public static void update (String ip) {
        interfaceInfo.setText(ip);
    }
    public static void showNewMessage(String ip, String data) {
        JOptionPane.showConfirmDialog(null, data, "Data from ip: " + ip, JOptionPane.PLAIN_MESSAGE);
    }
        
    public static void showErrorMessage(String error) {
        JOptionPane.showConfirmDialog(null, "Error", error, JOptionPane.PLAIN_MESSAGE);
    }
    public static void main(String[] args) {
        //new MyDevicesSync();
        //new Configuration(null).getConfiguration(null);
        new FileChooser(null);
    }   
}
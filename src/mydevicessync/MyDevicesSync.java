package mydevicessync;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class MyDevicesSync extends JFrame {
    private JTextArea contents;
    private JScrollPane contentScroller;
    private JLabel error = new JLabel(), 
           interfaceHeading = new JLabel(Constants.interfaceHeader),  
           //interfaceSpacer = new JLabel(Constants.formatHyphenBreak(Constants.interfaceHeader)),
           contentHeading = new JLabel(Constants.inputMessage),
           deviceHeading = new JLabel(Constants.deviceHeader);
           //fileHeading = new JLabel(Constants.fileHeader);
    private static JLabel interfaceInfo = new JLabel(),
                         fileToBeLoaded = new JLabel(Constants.prepareFileLabel(null));
    
    private JPanel deviceHierarchy, buttonPanel, contentPanel, devicePanel;
    private JButton addDeviceButton, removeDeviceButton, sendButton, receiveButton, addFileButton;
    
    private DeviceManager manager;
    private ConnectionManager connectionManager;
    private Configuration configuration;
    private File fileSelected;
    
    MyDevicesSync() {
        super(Constants.appTitle);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension d = new Dimension(600, 600);
        setBounds(0, 0, d.height, d.width);
        
        connectionManager = new ConnectionManager(this);                
        manager = new DeviceManager();
        contents = new JTextArea(100, 50);
        
        deviceHierarchy = new JPanel();
        deviceHierarchy.setLayout(new BoxLayout(deviceHierarchy, BoxLayout.Y_AXIS));
        deviceHierarchy.add(deviceHeading);
        
        devicePanel = new JPanel();
        devicePanel.setLayout(new BoxLayout(devicePanel, BoxLayout.Y_AXIS));
        
        if (manager.devicesLoaded()) 
            for (Device device : manager.getDevices()) {                
                devicePanel.add(device.button);
            }
        else
            deviceHeading.setText(Constants.noDevices);                
        
        addDeviceButton = new JButton(Constants.addButtonPrompt);
            addDeviceButton.addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent e) {
                    String name = JOptionPane.showInputDialog(null,     //Frame size
                                            null,                       //Input Values
                                            Constants.inputName,        //Title
                                            JOptionPane.PLAIN_MESSAGE); //Option type
                    if (name == null)
                        return;
                    else {
                        String ip = JOptionPane.showInputDialog(null, 
                                                null,
                                                Constants.inputIP,
                                                JOptionPane.PLAIN_MESSAGE);                        
                        if (ip == null) 
                            return;
                        else {
                            if (ConnectionManager.validateIP(ip)) {
                                manager.addDevice(name, ip);
                                JButton button = manager.getDevices().get(manager.getDeviceSize()-1).button;
                                button.setHorizontalAlignment(JButton.CENTER);
                                devicePanel.add(button);
                                deviceHierarchy.validate();
                            } else {
                                ip = JOptionPane.showInputDialog(null,                                                           
                                        Constants.formatInvalidIP(ip),
                                        Constants.inputIP,
                                        JOptionPane.PLAIN_MESSAGE);
                                if (ConnectionManager.validateIP(ip)) {
                                    manager.addDevice(name, ip);                                    
                                    JButton button = manager.getDevices().get(manager.getDeviceSize()-1).button;
                                    button.setHorizontalAlignment(JButton.CENTER);
                                    devicePanel.add(button);
                                    deviceHierarchy.validate();
                                } else
                                    JOptionPane.showConfirmDialog(null, 
                                        Constants.deviceAddFailed,
                                        Constants.inputIP,
                                        JOptionPane.PLAIN_MESSAGE);
                            }
                        }
                    }                   
                }
            });
            
        removeDeviceButton = new JButton(Constants.removeDevice);
            removeDeviceButton.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                    manager.removeDevice(manager.getIdx());
                    devicePanel.removeAll();                  

                    if (manager.devicesLoaded()) {
                        deviceHeading.setText(Constants.deviceHeader);
                        for (Device device : manager.getDevices()) {
                            devicePanel.add(device.button);
                        }
                    } else
                        deviceHeading.setText(Constants.noDevices);
                    deviceHierarchy.validate();
                } 
            });
            
        sendButton = new JButton(Constants.sendData);
            sendButton.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                   System.out.println(e.getActionCommand());
                   //showNewMessage("testing", "testing");
                   //connectionManager.write(manager.getDeviceIP(), contents.getText());
               } 
            });
        final MyDevicesSync reference = this;
        addFileButton = new JButton(Constants.addFileButtonPrompt);
            addFileButton.addActionListener(new ActionListener(){
               public void actionPerformed(ActionEvent e) {                   
                   new FileChooser(reference);
               } 
            });
            
        receiveButton = new JButton("Receive Data");
            receiveButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    
                }
            });
            
        JPanel deviceButtons = new JPanel(new FlowLayout());
        deviceButtons.add(addDeviceButton);
        deviceButtons.add(removeDeviceButton);
        
        deviceHierarchy.add(devicePanel);
        deviceHierarchy.add(deviceButtons);
        
        buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.add(sendButton);
        //buttonPanel.add(receive);
        
        contentScroller = new JScrollPane();
        contentScroller.setViewportView(contents);
        //contentScroller.setAlignmentX(0.0f);
        
        contentPanel = new JPanel();        
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            //interfaceHeading.setAlignmentX(0.0f);
        contentPanel.add(interfaceHeading);
            //interfaceInfo.setAlignmentX(0.0f);
        contentPanel.add(interfaceInfo);
        contentPanel.add(contentHeading);
        contentPanel.add(contentScroller);
            //contentPanel.add(fileHeading);
        contentPanel.add(fileToBeLoaded);
        contentPanel.add(addFileButton);
        
            //contentPanel.setSize(new Dimension(300, 300));
        deviceHierarchy.setSize(50, 300);
        add(error, BorderLayout.NORTH);    
        add(buttonPanel, BorderLayout.SOUTH);        
        add(contentPanel, BorderLayout.CENTER);
        add(deviceHierarchy, BorderLayout.WEST);      
        pack();
        setVisible(true);
        configuration = new Configuration();
    }
    public void addUpload(File file) {
        fileSelected = file;
        if (!file.isDirectory())           
            fileToBeLoaded.setText(Constants.prepareFileLabel(file.getName()));
        else
            System.out.println(file.getName());
    }
    public static void update (String ip) {
        interfaceInfo.setText(Constants.prepareInterfaceInfo(ip));
    }
    public static void showNewMessage(String ip, String data) {
        JOptionPane.showConfirmDialog(null, data, Constants.messageTitle + ip, JOptionPane.PLAIN_MESSAGE);
    }        
    public static void showErrorMessage(String error) {
        JOptionPane.showConfirmDialog(null, Constants.error, error, JOptionPane.PLAIN_MESSAGE);
    }
    public static void main(String[] args) {
        new MyDevicesSync();
    }   
}

package mydevicessync;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class Device {
    String name, ip;
    final JButton button;
    final DeviceManager manager;
    
    Device(String name, String ip, DeviceManager manager) {
        this.name = name;
        this.ip = ip;
        this.manager = manager;
        button = new JButton(toString());
        prepareButton();
    }
    
    Device(String[] data, DeviceManager manager) {
        this.name = data[0];
        this.ip = data[1];
        this.manager = manager;
        button = new JButton(toString());
        prepareButton();
    }
    
    public String toString() {
        return name + ":" + ip + "\n";
    }
    
    public void prepareButton() {
        button.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               manager.setIdx(button);
           } 
        });
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mydevicessync;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author admin
 */
public class FileChooser extends JFrame {
    String[] utils;
    String[] contents;
    JPanel utilPanel, selectPanel, buttonPanel;
    JTextField fileSelection; 
    JButton upload;
    MyDevicesSync reference;
    
    public FileChooser(final MyDevicesSync reference) {
        super("Select File");
        this.reference = reference;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(d.width/2 - (d.width/10), d.height/2 - (d.height/5), 400, 400);
        fileSelection = new JTextField();
        final FileChooser destroy = this;
        upload = new JButton("Add File");
            upload.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    File validate = new File(fileSelection.getText());
                    int option;
                    if (validate.exists() && (validate.listFiles() != null))
                        reference.addUpload(fileSelection.getText());
                    else {
                        option = JOptionPane.showConfirmDialog(
                            null,                       //Parent Component
                            Constants.fileError,        //Message
                            Constants.fileErrorTitle,   //Title
                            JOptionPane.YES_NO_OPTION   //JOP type
                        );
                        if (option == JOptionPane.NO_OPTION)
                            destroy.dispose();
                    }
                }
            });
            
        setVisible(true);
        setResizable(true);
        File home = new File(System.getProperty("user.home"));
        File[] files = home.listFiles();
        File[] list;
        for (File f : files) {
            /*
            list = f.listFiles();
            if (list != null)
                for (File ff : list)
                    System.out.println(ff.getPath());
            */
            System.out.println(f.getPath());
        }
    }
    
    public JPanel prepareUtils() {
        utilPanel = new JPanel(new BoxLayout(utilPanel, BoxLayout.Y_AXIS));
        ArrayList<File> files = new ArrayList<>();
        
        for (File f : new File(System.getProperty("user.home")).listFiles())
            if (f.listFiles() != null)
                files.add(f);
        
        File[] list = new File[files.size()];
        for (File f : list) {
            JButton button = new JButton(f.getName());
            button.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                   
               } 
            });
        }
        return utilPanel;
    }
}

package mydevicessync;

import java.awt.AWTError;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/* @author admin */
public class FileChooser extends JFrame {
    String[] favorites, contents;
    JPanel container, favoritePanel, selectPanel, savePanel, selectPanelHeader;
    JLabel workingDirectory;
    JTextField fileSelection;
    JButton uploadButton, directoryBackButton;
    JScrollPane favoriteScroller, selectScroller;
    MyDevicesSync reference;
    FileChooser thisReference;
    FileField[] favoriteFiles, selectionFiles;
    FileField currentSelection;
    ArrayList<String> directories;
    boolean favoriteSelected;

    public FileChooser(final MyDevicesSync reference) {
        super("Select File");       
        this.reference = reference;
        thisReference = this;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(d.width / 2 - (d.width / 10), d.height / 2 - (d.height / 5), 400, 400);
        fileSelection = new JTextField();
        selectScroller = new JScrollPane();
        favoriteScroller = new JScrollPane();
        uploadButton = new JButton("Add File");
        uploadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String fileName;
                //for (File f : ((favoriteSelected) ? favoriteFiles : selectionFiles))
                File validate = new File(currentSelection.getPath());
                try { FileInputStream fos = new FileInputStream(validate); } 
                    catch (FileNotFoundException ee) { validate = null; }
                
                int option = 0;
                if (validate == null)
                    option = 1;
                else if (validate.isDirectory()) 
                    option = 2;
                else if (!validate.exists())
                    option = 3;
                
                switch (option) {
                    case 0:
                        option = JOptionPane.NO_OPTION;
                        break;
                    case 1:
                        option = JOptionPane.showConfirmDialog(
                            null, //Parent Component
                            Constants.prepareFileError(null), //Message
                            Constants.fileErrorTitle, //Title
                            JOptionPane.YES_NO_OPTION //JOP type
                        ); break;
                    case 2:
                        option = JOptionPane.showConfirmDialog(
                            null, //Parent Component
                            Constants.prepareFileError(validate.getName()), //Message
                            Constants.fileErrorTitle, //Title
                            JOptionPane.YES_NO_OPTION //JOP type
                        ); break;
                    default: 
                        option = JOptionPane.NO_OPTION;
                        break;
                }
                if (option == JOptionPane.NO_OPTION) 
                    reference.dispose();
                else {
                    fileSelection.setText("");
                    clearFiles();
                }
            }
        });
        
        directoryBackButton = new JButton(Constants.directoryBackButton);
        directoryBackButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (directories.isEmpty())
                    return;
                System.out.println(directories.get(directories.size()-1));
                prepareMain(new File(directories.get(directories.size()-1)));
                directories.remove(directories.size()-1);
            }
        });
        directories = new ArrayList<>();
        workingDirectory = new JLabel();
        selectPanelHeader = new JPanel();
        selectPanelHeader.setLayout(new BoxLayout(selectPanelHeader, BoxLayout.X_AXIS));
        
        selectPanelHeader.add(directoryBackButton);
        selectPanelHeader.add(workingDirectory);
        
        container = new JPanel(new BorderLayout());
        container.add(new JButton("title"), BorderLayout.NORTH);
       
        add(prepareSavePanel(), BorderLayout.SOUTH);
        add(prepareFavorites(), BorderLayout.WEST);

        File home = new File(System.getProperty("user.home"));
        File[] files = home.listFiles();
        boolean directoryFound = false;
        for (File findDirectory : files) {
            if (findDirectory.isDirectory()) {
                add(prepareMain(findDirectory), BorderLayout.CENTER);
                directoryFound = true;
                break;
            }
        }
        if (!directoryFound) {
            add(new JLabel(Constants.noDirectoryInFavorites), BorderLayout.CENTER);
        }
        setVisible(true);
        setResizable(false);
    }
    
    public JPanel prepareSavePanel() {
        savePanel = new JPanel();
        savePanel.setLayout(new BoxLayout(savePanel, BoxLayout.X_AXIS));
        savePanel.add(uploadButton);
        savePanel.add(fileSelection);       
        return savePanel;
    }

    public JScrollPane prepareFavorites() {
        ArrayList<File> files = new ArrayList<>();
        for (File f : new File(System.getProperty("user.home")).listFiles()) {
            if ((f.listFiles() != null) && (f.getName().charAt(0) != '.')) {
                files.add(f);
            }
        }
        File[] list = new File[files.size()];
        files.toArray(list);
        favoritePanel = new JPanel();
        favoritePanel.setLayout(new BoxLayout(favoritePanel, BoxLayout.Y_AXIS));
        favoritePanel.add(new JLabel(Constants.favoritesList));
        favoriteFiles = new FileField[list.length];
        for (int x = 0; x < list.length; x++) {
            favoriteFiles[x] = prepareTextField(list[x], true);
            try {
                favoritePanel.add(favoriteFiles[x]);
            } catch (AWTError e) {
                System.out.println(e.getMessage());
            }
        }
        favoriteScroller.setViewportView(favoritePanel);
        return favoriteScroller;
    }

    public JScrollPane prepareMain(File file) {            
        if (file.isDirectory()) {
            ArrayList<File> files = new ArrayList<>();
            for (File f : file.listFiles())
                files.add(f);
            
            File[] list = new File[files.size()];
            files.toArray(list);
            selectPanel = new JPanel();
            selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.Y_AXIS));
            selectPanel.add(selectPanelHeader);
            workingDirectory.setText(file.getPath());                       
            
            selectionFiles = new FileField[list.length];
            for (int x = 0; x < list.length; x++) {
                selectionFiles[x] = prepareTextField(list[x], false);
                try {
                    selectPanel.add(selectionFiles[x]);
                } catch (AWTError e) {
                    System.out.println(e.getMessage());
                }
            }
            selectScroller.setViewportView(selectPanel);
            reference.validate();
            System.out.println("Changing directory to: " + file.getPath());
            return selectScroller;
        } else {
            return null;
        }
    }

    public FileField prepareTextField(File filename, final boolean favorite) {
        final FileField field = new FileField(filename, favorite);
        field.setEditable(false);
        field.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(field, favorite);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        return field;
    }
    public void handleClick(FileField field, boolean favorite) {
        synchronized (field) {
            FileField[] array;
            currentSelection = field;
            favoriteSelected = favorite;
            array = favorite ? favoriteFiles : selectionFiles; 
            if (!field.isClicked)
                for (FileField ff : array) 
                    if (ff == field) field.isClicked = !field.isClicked; 
                    else ff.clear();                   
            field.setBackground(field.isClicked ? Color.blue : Color.white);  //Set color based on click status
            if (System.currentTimeMillis() - field.getInterval() < Constants.sleepInterval) 
                fileSelected();            
            field.setInterval();
        }
    }
    
    public void fileSelected() {
        File file = new File(currentSelection.getPath());
        System.out.println(file.getPath());
        if (file.isDirectory()) {
            String[] path = file.getPath().split("/");
            StringBuilder name = new StringBuilder();
            for (int x = 0; x < path.length-1; x++) {
                name.append(path[x]);
                name.append('/');
            }
            directories.add(name.toString());
            prepareMain(file);
        } else {
            reference.addUpload(file);
            thisReference.dispose();
        }
    }
    
    public void clearFiles() {
        for (FileField ff : favoriteFiles)
            ff.clear();
        for (FileField ff : selectionFiles)
            ff.clear();
    }

    private class FileField extends JTextField {
        private String absolutePath, name;
        boolean isClicked = false, isFavorite;
        private long clickInterval;
        public FileField(File f, boolean isFavorite) {
            absolutePath = f.getAbsolutePath();
            name = f.getName();
            setText(name);
            clickInterval = System.currentTimeMillis();
            this.isFavorite = isFavorite;
        }
        String getPath() { return absolutePath; }
        String getPathName() { return name; }
        long getInterval() { return clickInterval; }
        void setInterval() { clickInterval = System.currentTimeMillis(); }
        void clear() {
            isClicked = false;
            setBackground(Color.white);            
        }
    }
}

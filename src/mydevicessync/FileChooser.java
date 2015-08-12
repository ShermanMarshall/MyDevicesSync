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

/*
 *
 * @author admin
 */
public class FileChooser extends JFrame {

    String[] favorites;
    String[] contents;
    JPanel container, favoritePanel, selectPanel, savePanel;
    JTextField fileSelection;
    JButton upload;
    JScrollPane favoriteScroller, selectScroller;
    MyDevicesSync reference;
    FileChooser thisReference;
    FileField[] favoriteFiles, selectionFiles;

    public FileChooser(final MyDevicesSync reference) {
        super("Select File");
        this.reference = reference;
        thisReference = this;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(d.width / 2 - (d.width / 10), d.height / 2 - (d.height / 5), 400, 400);
        fileSelection = new JTextField();
        final FileChooser destroy = this;
        upload = new JButton("Add File");
        upload.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File validate = new File(fileSelection.getText());
                try {
                    FileInputStream fos = new FileInputStream(validate);
                } catch (FileNotFoundException ee) {
                }
                int option;
                if (validate.exists() && (validate.listFiles() != null)) {
                    reference.addUpload(fileSelection.getText());
                } else {
                    option = JOptionPane.showConfirmDialog(
                            null, //Parent Component
                            Constants.fileError, //Message
                            Constants.fileErrorTitle, //Title
                            JOptionPane.YES_NO_OPTION //JOP type
                    );
                    if (option == JOptionPane.NO_OPTION) {
                        destroy.dispose();
                    }
                }
            }
        });

        container = new JPanel(new BorderLayout());
        container.add(new JButton("title"), BorderLayout.NORTH);
        add(prepareSavePanel(), BorderLayout.SOUTH);
        add(prepareFavorites(), BorderLayout.WEST);

        File home = new File(System.getProperty("user.home"));
        File[] files = home.listFiles();
        boolean directoryFound = false;
        for (File findDirectory : files) {
            if (findDirectory.isDirectory()) {
                add(prepareMain(findDirectory.getPath()), BorderLayout.CENTER);
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
        savePanel.add(upload);
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
        favoriteScroller = new JScrollPane();
        favoriteScroller.setViewportView(favoritePanel);
        return favoriteScroller;
    }

    public JScrollPane prepareMain(String dirName) {
        File file = new File(dirName);        
        
        if (file.isDirectory()) {
            ArrayList<File> files = new ArrayList<>();
            for (File f : file.listFiles())
                files.add(f);
            
            File[] list = new File[files.size()];
            files.toArray(list);
            selectPanel = new JPanel();
            selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.Y_AXIS));
            selectPanel.add(new JLabel(dirName));
            selectionFiles = new FileField[list.length];
            for (int x = 0; x < list.length; x++) {
                selectionFiles[x] = prepareTextField(list[x], false);
                try {
                    selectPanel.add(selectionFiles[x]);
                } catch (AWTError e) {
                    System.out.println(e.getMessage());
                }
            }
            selectScroller = new JScrollPane();
            selectScroller.setViewportView(selectPanel);
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
                synchronized (field) {
                    FileField[] array;
                    if (favorite) {
                        array = favoriteFiles;
                    } else {
                        array = selectionFiles;
                    }

                    for (FileField ff : array) {
                        if (ff == field) {
                            continue;
                        } else {
                            ff.clear();
                        }
                    }

                    field.isClicked = !field.isClicked;
                    if (field.isClicked) {
                        field.setBackground(Color.blue);
                    } else {
                        field.setBackground(Color.white);
                    }

                    //if (System.currentTimeMillis() - field.getInterval() > Constants.sleepInterval)
                    //  reference.fileSelected = new File(field.absolutePath);
                    //reference.addUpload(field.getPath());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        return field;
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

        String getPath() {
            return absolutePath;
        }

        String getPathName() {
            return name;
        }

        long getInterval() {
            return clickInterval;
        }

        void clear() {
            isClicked = false;
            setBackground(Color.white);
        }
    }
}

package mydevicessync;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Collections;

public class ConnectionManager {
    String data, ip; 
    boolean active, startServer;
    ServerSocket ss;
    ArrayList <String> messages;
    MyDevicesSync gui;

    public ConnectionManager (MyDevicesSync gui) {
        this.gui = gui;
        data = "";
        messages = new ArrayList<>();
        active = startServer = true;
        
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            for (int x = 1; nis.hasMoreElements(); x++) {
                NetworkInterface ni = nis.nextElement();
                for (InetAddress ia : Collections.list(ni.getInetAddresses())) {					
                    String[] set = ia.getHostAddress().split(":");
                    if (set.length == 1) {
                        ip = set[0];
                        MyDevicesSync.update(ip);
                        prepareServer();                        
                        return;
                    }
                }
            }
        } catch (SocketException e) {
            gui.showErrorMessage(Constants.configFailError);
            active = startServer = false;
        }
    }

    void prepareServer() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    ss = new ServerSocket(30000, 1, InetAddress.getByName(ip));                    
                    while (active) {
                        Socket s = ss.accept();
                        //handleConnection(s);
                        read(s);
                    }
                    ss.close();
                    prepareServer();
                } catch (NumberFormatException nfe) {
                    gui.showErrorMessage(Constants.numberFormatError);
                } catch (IOException e) {
                    gui.showErrorMessage(Constants.serverRestart);
                    active = false;
                }
            } 
        }); t.start();
    }
    
    public void handleConnection(Socket s) {
        try {
            InputStream is = s.getInputStream();
            byte data;
            String type = "";
            
            while ((data = (byte) is.read()) != ':')
                type += (char) data;
            
            if (type.equals(Constants.readMessage))
                read(s);
            else
                write(s);
            
        } catch (IOException e) {
            gui.showErrorMessage(Constants.connectionIOError);
        }
    }
    
    public void write(Socket s) {
        try {
            MessageManager mm = MessageManager.newInstance();
            Message message = mm.getNext();
            if (message != null) {
                OutputStream os = s.getOutputStream();
                String msg = message.getContents();
                os.write((msg.length() + ":" + msg).getBytes());
                os.close();
                gui.showNewMessage(s.getLocalAddress().getHostAddress(), 
                        "Message:\n----------\n" + msg + "\n-----------Sent successfully");
            } else
                gui.showErrorMessage("No messages in message queue");
        } catch (IOException e) {
            gui.showErrorMessage(Constants.writeError);
        }
    }
    
    public void read(Socket s) throws NumberFormatException {
        try {
            InputStream is = s.getInputStream();
            String ip = new String(s.getInetAddress().getHostAddress());           
            byte data;
            String message = "", size = "";

            while ((data = (byte) is.read()) != ':')
                size += (char) data;

            byte[] chars = new byte[Integer.parseInt(size)];
            System.out.println(chars.length);
            
            DataInputStream dis = new DataInputStream(is);    
            dis.readFully(chars);
            message = new String(chars);
            
            gui.showNewMessage(ip, message);
            dis.readFully(chars);
            dis.close();
            is.close();
            s.close();
        } catch (IOException e) {
            gui.showErrorMessage(Constants.IOE);
        }
    }
    
    static boolean validateIP (String ip) {
        String[] quad = ip.split("\\.");
        if (quad.length != 4)
            return false;        
        for (String s : quad)
            try {
                int num = Integer.parseInt(s);
                if (num < 0 || num > 255)
                    return false;
            } catch (NumberFormatException e) {
                return false;
            }
        //Ensure loopback is not included
        return ip.equals(Constants.localhost) ? false : true;
    }
    
    //Protocol format: totalLengthInBytes:fileType:fileLength
    //Byte-array format: #CharsInLength:fileLength:fileType
    public static byte[] prepareFileDataForSend(File file) throws IOException, FileNotFoundException {
        long length = file.length();
        String strLength = Long.toString(length);
        byte[] data = new byte[strLength.length() + 1 + ((int) length) + 1 + Constants.txtFile.length()];
        for (int x = 0; x < strLength.length(); x++)
                data[x] = (byte) strLength.charAt(x);

        data[strLength.length()] = Constants.colon;

        FileInputStream fis = new FileInputStream(file);
        fis.read(data, strLength.length() + 1, (int) length);
        data[strLength.length() + ((int) length) + 1] = Constants.colon;

        for (int x = 1; x <= Constants.txtFile.length(); x++)
                data[strLength.length() + ((int) length) + 1 + x] = (byte) Constants.txtFile.charAt(x-1);

        return data;
    }

}
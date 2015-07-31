package mydevicessync;

import java.io.*;
import java.net.*;
import java.util.*;

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
                        prepareServer();
                        return;
                    }
                }
            }
        } catch (SocketException e) {
            gui.showErrorMessage("Unable to identify network interface");
            active = startServer = false;
        }
    }

    void prepareServer() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    ss = new ServerSocket(30000, 1, InetAddress.getByName(ip));
                    System.out.println("Server Active");
                    while (active) {
                        Socket s = ss.accept();
                        read(s);
                    }
                    ss.close();
                    prepareServer();
                } catch (NumberFormatException nfe) {
                    gui.showErrorMessage("Message length parse failed");
                } catch (IOException e) {
                    gui.showErrorMessage("Server shut down. Restarting");
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
            
            if (type.equals("read"))
                read(s);
            else
                write(s);
        } catch (IOException e) {
            gui.showErrorMessage("Connection could not be handled");
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
            gui.showErrorMessage("Error sending data to device");
        }
    }
    
    public void read(Socket s) {
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
            gui.showErrorMessage("Message encountered an IOException");
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
        return ip.equals("127.0.0.1") ? false : true;
    }
}
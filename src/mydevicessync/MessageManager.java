package mydevicessync;

import java.util.ArrayList;

/**
 *
 * @author admin
 */
public class MessageManager {
    private static MessageManager reference;
    private ArrayList<Message> messages;
    
    MessageManager() {
        reference = this;
        messages = new ArrayList<>();
    }
    
    Message getNext() { return (messages.size() > 0) ? messages.get(0) : null; }
    
    void prepareNext() {
        if (messages.size() > 0)
            messages.remove(0);
    }
    
    static MessageManager newInstance() { 
        if (reference == null) 
            reference = new MessageManager();
        
        return reference; 
    }
    
}

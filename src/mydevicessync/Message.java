package mydevicessync;

public class Message {
    private String device;
    private String contents;
    
    Message (String string, String device) {
        contents = string;
        this.device = device;
    }
    
    String getContents() {
        return contents;
    }
    
    String getDevice () {
        return device;
    }
    
    void setDevice(String device) {
        this.device = device;
    }
    
}
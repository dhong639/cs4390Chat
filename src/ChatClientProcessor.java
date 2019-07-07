import java.io.DataInputStream;


public class ChatClientProcessor extends Thread {

	ChatClient client;
    DataInputStream dataIn;
    byte[] clientSecurityKey;
    
    public ChatClientProcessor(ChatClient client, DataInputStream dataIn, byte[] clientSecurityKey) {
       
    	this.client = client;
        this.dataIn = dataIn;
        this.clientSecurityKey = clientSecurityKey;
 
    }
    
    public void run() {
        
    	while(true) {
            try {
                String serverString = dataIn.readUTF();
                serverString = SecurityHelper.decrypt(serverString, clientSecurityKey);
                 
                switch(client.getChatClientState()) {
                
                    case ("IDLE"):
                        if(serverString.contains("CHAT_STARTED")){
                            System.out.println("Chat started with " + serverString.split("[(), ]+")[2]);
                            client.setSessionID(serverString.split("[(), ]+")[1]);
                            client.setState("CHAT");
                            client.startChat(serverString);
                        } 
                        else if(serverString.contains("HISTORY_RESP")) {
                           
                        	System.out.println(serverString.substring(serverString.indexOf("(") + 1, serverString.length() - 1));
                        }
                        
                        break;
                    case ("REQUEST"):
                        if(serverString.contains("UNREACHABLE")) {
                            System.out.println("Client " + serverString.split("[()]")[1] + " is unavialble");
                            client.setState("IDLE");
                        }
                        else {
                            System.out.println("Chat started with " + serverString.split("[(), ]+")[2]);
                            client.setSessionID(serverString.split("[(), ]+")[1]);
                            client.setState("CHAT");
                        }
                        break;
                    case ("CHAT"):
                        if(serverString.contains("END_NOTIF")) {
                            System.out.println("Chat Ended");
                            client.endChat(serverString);
                            client.setState("IDLE");
                        }
                        else {
                            String s = serverString.substring(serverString.indexOf(",") + 2, serverString.length() - 1);
                            System.out.println(s);
                        }
                        break;
                        
                }
            } 
            catch(Exception ex) {
            	
                System.out.println(ex);
                break;
            }
        }
    }
    
    public void StopChatClient() {
        try {
        	
        	dataIn.close();
        }
        catch(Exception ex) {
            
        	System.out.println(ex);
        }
    }
    

}

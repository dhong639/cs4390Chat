import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

public class ChatKeyboardProcessor extends Thread {

	ChatClient client;
    DataOutputStream dataOut;
    BufferedReader inFromUser;
    byte[] clientSecurityKey;
    
    public ChatKeyboardProcessor(ChatClient client, DataOutputStream outToServer, byte[] clientSecurityKey) {
    
    	this.client = client;
        this.dataOut = outToServer;
        this.inFromUser = new BufferedReader(new InputStreamReader(System.in));
        this.clientSecurityKey = clientSecurityKey;
        
    }
    
    public void run() {
    
    	String responseString;
     
        while(true) {
            try {
                String line = inFromUser.readLine();
                switch(client.getChatClientState()) {
                    case ("IDLE"):
                        if(line.toUpperCase().equals("LOG OFF")) {
                        	
                        	responseString = SecurityHelper.encryptTCP(line, clientSecurityKey);
                            dataOut.writeUTF(responseString);
                            dataOut.flush();
                            dataOut.close();
                            inFromUser.close();
                            client.stop();
                            break;
                            
                        }
                        else if(line.contains("Chat")) {
                           
                        	client.setState("REQUEST");
                            responseString = "CHAT_REQUEST(" + line.split("[ ]")[1] + ")";
                            responseString = SecurityHelper.encrypt(responseString, clientSecurityKey);
                            dataOut.writeUTF(responseString);
                            dataOut.flush();
                            
                        }
                        else if(line.toUpperCase().contains("HISTORY")) {                          
                        	   
                        		responseString = SecurityHelper.encrypt("HISTORY_REQUEST(" + line.split("[ ]")[1] + ")", clientSecurityKey);
                                dataOut.writeUTF(responseString);
                                dataOut.flush();
                                break;
                                
                        }
                        else {
                           
                        	System.out.println("Please type Log Off or Chat [Client-ID] or History [Client-ID]");
                        	
                        }
                        break;
                    case ("CHAT"):
                        if(line.toUpperCase().equals("END CHAT")) {
                        	
                            client.setState("IDLE");
                            responseString = SecurityHelper.encryptTCP("END_REQUEST(" + client.getSessionID() + ")", clientSecurityKey);
                            dataOut.writeUTF(responseString);
                            dataOut.flush();
                            break;
                            
                        }
                        else {
                        	
                        	responseString = SecurityHelper.encryptTCP("CHAT(" + client.getSessionID() + ", " + line + ")", clientSecurityKey);
                            dataOut.writeUTF(responseString);
                            dataOut.flush();
                            
                        }
                        break;
                }
            } 
            catch(Exception ex){
                
            	System.out.println(ex);
                break;
                
            }
        }
    }
    
    public void startChat(String message) {
        try {
        	
            client.setState("CHAT");
            String responseString = message;
            responseString = SecurityHelper.encryptTCP(responseString, clientSecurityKey);
            dataOut.writeUTF(responseString);
            dataOut.flush();
            
        }
        catch(Exception ex) {
        	
            System.out.println(ex);
            
        }
    }
    
    public void endChat(String message) {
        try {
        	
            client.setState("IDLE");
            String responseString = message.replace("END_NOTIF", "END_REC");
            responseString = SecurityHelper.encryptTCP(responseString, clientSecurityKey);
            dataOut.writeUTF(responseString);
            dataOut.flush();
            
        }
        catch(Exception ex) {
        	
            System.out.println(ex);
            
        }
    }
    
   

}

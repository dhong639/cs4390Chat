import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;


public class ChatClient extends Thread {
	   
	    private final String clientName;
	    private final String clientSecretKey;
	    private final String serverName;
	    private final int serverPort;
	    private byte[] clienEncrypttKey;
	    
	    private String state;
	    private String sessionID;
	    
	    BufferedReader inputFromUser;
	    
	    private DatagramSocket clientSocket;
	    private InetAddress ipAddress;
	    
	    ChatClientProcessor chatClientProcessor = null;
	    ChatKeyboardProcessor chatKeyboardProcessor = null;
	    
	    public ChatClient(String _clientName, String _clientSecretKey, String _serverName, int _serverPort){
	        
	    	this.clientName = _clientName;
	        this.clientSecretKey = _clientSecretKey;
	        this.serverName = _serverName;
	        this.serverPort = _serverPort;
	        
	        while(!connect()) {
	           
	        	System.out.println("Try to log on again");
	        }
	    }
	    
		private boolean connect() {
	     
			inputFromUser = new BufferedReader(new InputStreamReader(System.in));
	        
	        try {
	          
	        	clientSocket = new DatagramSocket();
	            ipAddress = InetAddress.getByName(serverName);
	            
	            String sentence = inputFromUser.readLine();
	            if(sentence.toUpperCase().equals("LOG ON")) {
	                System.out.println("Connecting...");
	                //HELLO to the server
	                String sendString = "HELLO (" + clientName + ")";
	                sendString(sendString);
	            
	                String response = receiveString().trim();
	                //System.out.println("FROM SERVER:" + response);
	                
	                String[] dataArray = response.split("[()]+");
	                for(int a = 0; a < dataArray.length; a ++) {
	                   
	                	dataArray[a] = dataArray[a].trim().toUpperCase();
	                }
	                
	                //RESPONSE to server
	                if(dataArray[0].equals("CHALLENGE")) {
	                   
	                	String xRES = SecurityHelper.SHA(dataArray[1], clientSecretKey);
	                    clienEncrypttKey = SecurityHelper.MD5(dataArray[1], clientSecretKey);
	                    //System.out.println(dataArray[1] + clientSecretKey);
	                    sendString = "RESPONSE(" + clientName + "," + xRES + ")";
	                    
	                }
	                else {
	                    
	                	System.out.println("Error in server response\n" + response);
	                    return false;
	                    
	                }
	                
	                sendString(sendString);
	                byte [] responseBytes = receiveRandCookie();
	                response = SecurityHelper.decryptUDP(responseBytes, clienEncrypttKey);
	                
	                dataArray = response.split("[(), ]+");
	                for(int a = 0; a < dataArray.length; a ++) {
	                    
	                	dataArray[a] = dataArray[a].trim().toUpperCase();
	                }
	                
	                if(dataArray[0].equals("AUTH_SUCCESS")) {
	                   
	                	runChatClient(dataArray[1], Integer.parseInt(dataArray[2]));
	                }
	                else {
	                   
	                	System.out.println("Error in server response\n" + response);
	                    return false;
	                }
	                
	            }
	            else {
	               
	            	System.out.println("Please type log on");
	                clientSocket.close();
	                return false;
	            }
	            
	            clientSocket.close();
	            
	        }
	        catch(Exception ex) {
	            
	        	System.out.println(ex);
	        }
	       
	        return true;
	    }
	    
	    //Sends a string to the UDP server
	    private void sendString(String message) throws IOException {
	       
	    	byte[] sendData = message.getBytes();      
	        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, serverPort);
	        clientSocket.send(sendPacket);
	        
	    }
	
	    private String receiveString() throws IOException {
	    	
	        byte[] receiveData = new byte[2048];
	        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	        clientSocket.receive(receivePacket);
	        return new String(receivePacket.getData());
	        
	    }
	    
	    private byte [] receiveRandCookie() throws IOException {
	      
	    	byte[] receiveData = new byte[1024];
	        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	        clientSocket.receive(receivePacket);
	        return receivePacket.getData();
	    }
	    
	    //Runs the TCP client
	    private void runChatClient(String cookie, int port) {
	      
	    	String responseString = null;
	        String receiveString = null;
	        
	        try {
	            //System.out.println("Connecting to: " + port);
	            Socket TCPClientSocket = new Socket(serverName, port);
	            DataOutputStream dataOut = new DataOutputStream(TCPClientSocket.getOutputStream());
	            DataInputStream dataIn = new DataInputStream(new BufferedInputStream(TCPClientSocket.getInputStream()));
	            
	            responseString = "CONNECT(" + cookie + ")";
	            responseString = SecurityHelper.encryptTCP(responseString, clienEncrypttKey);
	            dataOut.writeUTF(responseString);
	            dataOut.flush();
	            
	            receiveString = dataIn.readUTF();
	            receiveString = SecurityHelper.decryptTCP(receiveString, clienEncrypttKey);
	            setState("IDLE");
	            
	            System.out.println("Connected");
	            
	            chatClientProcessor = new ChatClientProcessor(this, dataIn, clienEncrypttKey);
	            Thread chatClientThread = new Thread(chatClientProcessor);
	            chatClientThread.start();
	            
	            chatKeyboardProcessor = new ChatKeyboardProcessor(this, dataOut, clienEncrypttKey);
	            Thread chatKeyBoardThread = new Thread(chatKeyboardProcessor);
	            chatKeyBoardThread.start();
	        }
	        catch(Exception ex) {
	        	
	            System.out.println(ex);
	            
	        }
	    }
	    
	    public void setState(String state) {
	    	
	        this.state = state;
	    }
	    
	    public String getChatClientState() {
	    	
	    	String state = null;
	       
	    	state = this.getState().toString();
	    	
	    	return state;
	    }
	    
	   
	    public void setSessionID(String session) {
	    	
	        this.sessionID = session;
	        
	    }
	    
	    public String getSessionID() {
	    	
	        return sessionID;
	        
	    }
	    
	    public void startChat(String message) {
	    	
	    	chatKeyboardProcessor.startChat(message);
	    	
	    }
	    
	    public void endChat(String message) {
	    	
	    	chatKeyboardProcessor.endChat(message);
	    	
	    }
	    
	    public void start() {
	    	
	    	chatClientProcessor.start();
	    }
	    
	    public void stopThread() {
	    	
	    	chatClientProcessor.StopChatClient();
	    	
	    }
	    
	    public String getClientName(){
	    	
	    	return this.clientName;
	    	
	    }
	    
	    public static void main(String args[]){
	        
	    	ChatClient client = null;
	        
	        if(args.length != 4) {
	        	
	            System.out.println("Please enter in the correct arguments, 1- client name, 2- client key, 3- host name, 4 - port number");
	            
	        } 
	        else {
	            
	        	client = new ChatClient(args[0], args[1], args[2], Integer.parseInt(args[3]));
	        }
	    }
	    

}

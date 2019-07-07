import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SecurityHelper {
	
	//Used for TCP connections
    public static String encrypt(String strClearText,byte[] digest) throws Exception {
      
    	String strData="";
        byte [] encrypted = null;

        try {
        	
            SecretKeySpec skeyspec=new SecretKeySpec(digest,"AES");
            Cipher cipher=Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeyspec);
            encrypted=cipher.doFinal(strClearText.getBytes());
            strData=new String(encrypted, "ISO-8859-1");
            

        } 
        catch (Exception ex) {
        	
            ex.printStackTrace();
            throw new Exception(ex);
            
        }
      
        return strData;
    }

	public static String decrypt(String strEncrypted, byte[] digest) throws Exception {
       
		String strData="";
        byte[] byteEncrypted = strEncrypted.getBytes("ISO-8859-1");
       
        try {
      
        	SecretKeySpec skeyspec=new SecretKeySpec(digest,"AES");
            Cipher cipher=Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeyspec);
            byte[] decrypted=cipher.doFinal(byteEncrypted);
            strData = new String(decrypted);

        } 
        catch (Exception ex) {
          
        	ex.printStackTrace();
            throw new Exception(ex);
            
        }
       
        return strData;
    }
	
	//Performs SHA encryption
    public static String SHA(String random, String secretKey) {
     
    	String plainText = random + secretKey;
        MessageDigest m = null;
       
        try {
            m = MessageDigest.getInstance("SHA-1");
        } 
        catch (NoSuchAlgorithmException ex) {
        	
            ex.printStackTrace();
            
        }
      
        m.reset();
        m.update(plainText.getBytes());
        byte[] digest = m.digest();
        BigInteger bigInt = new BigInteger(1,digest);
        
        String strData = bigInt.toString(16);
  
        while(strData.length() < 32 ){
            strData = "0"+strData;
        }
      
        return strData;
    }
    
   //Generate the ciphering key
   //Generated Key needs to be 16 byte length
    public static byte[] MD5(String ran, String strKey){
       
    	String clientSecretKey = ran + strKey;
        MessageDigest m = null;
      
        try {
        	
            m = MessageDigest.getInstance("MD5");
            
        } 
        catch (NoSuchAlgorithmException ex) {
        	
            ex.printStackTrace();
            
        }
        
        m.reset();
        m.update(clientSecretKey.getBytes());
        byte[] digest = m.digest();
       
        return digest;
        
    }
    
    public static String encryptUDP(String strClearText,byte[] digest) throws Exception {
      
    	String strData="";
		
        byte [] encrypted = null;
		
		try {
		
			SecretKeySpec skeyspec=new SecretKeySpec(digest,"AES");
			Cipher cipher=Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, skeyspec);
			encrypted=cipher.doFinal(strClearText.getBytes());
			strData=new String(encrypted, "ISO-8859-1");
			
		} 
		catch (Exception ex) {
			
			ex.printStackTrace();
			throw new Exception(ex);
			
		}
		return strData;
    }
    
    public static String encryptTCP(String strClearText,byte[] digest) throws Exception {
       
    	String strData="";
        byte [] encrypted = null;

        try {
        	
            SecretKeySpec skeyspec=new SecretKeySpec(digest,"AES");
            Cipher cipher=Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeyspec);
            encrypted=cipher.doFinal(strClearText.getBytes());
            strData=new String(encrypted, "ISO-8859-1");

        } 
        catch (Exception ex) {
        	
            ex.printStackTrace();
            throw new Exception(ex);
            
        }
       
        return strData;
    }
    
    public static String decryptUDP(byte[] strEncrypted, byte[] digest) throws Exception {
		
    	String strData="";
		byte[] byteEncrypted = strEncrypted;
		
		try {
			
			SecretKeySpec skeyspec=new SecretKeySpec(digest,"AES");
			Cipher cipher=Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, skeyspec);
			byte[] decrypted=cipher.doFinal(byteEncrypted);
			strData = new String(decrypted);
			
			
		} 
		catch (Exception ex) {
			
			ex.printStackTrace();
			throw new Exception(ex);
			
		}
		
		return strData;
		
    }
    
    public static String decryptTCP(String strEncrypted, byte[] digest) throws Exception {
       
    	String strData="";
        byte[] byteEncrypted = strEncrypted.getBytes("ISO-8859-1");
        
        try {
        	
            SecretKeySpec skeyspec=new SecretKeySpec(digest,"AES");
            Cipher cipher=Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeyspec);
            byte[] decrypted=cipher.doFinal(byteEncrypted);
            strData = new String(decrypted);

        }
        catch (Exception ex) {
        	
            ex.printStackTrace();
            throw new Exception(ex);
            
        }
       
        return strData;
    }
    
    

}

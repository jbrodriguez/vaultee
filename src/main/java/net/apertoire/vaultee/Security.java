package net.apertoire.vaultee;

import java.util.UUID;
import java.math.BigInteger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.io.UnsupportedEncodingException;
// import org.apache.commons.codec.binary.Hex;
// import org.apache.commons.codec.digest.DigestUtils;

import org.mindrot.jbcrypt.BCrypt;

public class Security {

  // HMAC SHA-512 hash
  public static String sign(String message, String key) {
    if (key.length() == 0) {
      return null;
    }

    String result = null;

    try {
      Mac mac = Mac.getInstance("HmacSHA512");
      SecretKeySpec signingKey = new SecretKeySpec(key.getBytes("utf-8"), "HmacSHA512");
      mac.init(signingKey);
  
      byte[] messageBytes = message.getBytes("utf-8");
      byte[] resultBytes = mac.doFinal(messageBytes);
  
      result = bytesToHex(resultBytes);
    } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    } catch (InvalidKeyException e) {
        e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
    };

    return result;
  }

  public static String hash(String password, String serverWidePasswordSecret) {
    if (password.length() == 0)
      throw new IllegalArgumentException("Password may not be zero-length");

    // we add the server's secret key to the password,
    // the idea is to require stealing both the server
    // key and the database, which might raise the bar
    // a bit.
    String intermediate = sign(password, serverWidePasswordSecret);

    return BCrypt.hashpw(intermediate, BCrypt.gensalt());
  }

  public static boolean check(String password, String passwordHash, String serverWidePasswordSecret) {
    String intermediate = sign(password, serverWidePasswordSecret);

    return BCrypt.checkpw(intermediate, passwordHash);
  }

  public static String bytesToHex(byte[] bytes) {
      final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
      char[] hexChars = new char[bytes.length * 2];
      int v;
      for ( int j = 0; j < bytes.length; j++ ) {
          v = bytes[j] & 0xFF;
          hexChars[j * 2] = hexArray[v >>> 4];
          hexChars[j * 2 + 1] = hexArray[v & 0x0F];
      }
      return new String(hexChars);
  }  
}
package net.apertoire.vaultee;

import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;
// import java.util.UUID;
import java.math.BigInteger;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


/**
 * Basic Authentication Manager Bus Module<p>
 * Please see the busmods manual for a full description<p>
 *
 * original
 * @author <a href="http://tfox.org">Tim Fox</a>
 *
 * modified
 * @author <a href="http://www.apertoire.net/">Juan B. Rodriguez</a>
 */
public class AuthManager extends BusModBase {

  private Handler<Message<JsonObject>> loginHandler;
  private Handler<Message<JsonObject>> logoutHandler;
  private Handler<Message<JsonObject>> authorizeHandler;
  private Handler<Message<JsonObject>> getuniqueidHandler;

  protected final Map<String, Long> sessions = new HashMap<>();
  // protected final Map<String, LoginInfo> logins = new HashMap<>();

  private static final long DEFAULT_SESSION_TIMEOUT = 30 * 60 * 1000;

  private static final String KEY = "mymostsecretkey";

  private String address;
  private String userCollection;
  private String persistorAddress;

  /**
   * Start the busmod
   */
  public void start() {
    super.start();

    this.address = getOptionalStringConfig("address", "vaultee.authmanager");
    this.userCollection = getOptionalStringConfig("user_collection", "users");
    this.persistorAddress = getOptionalStringConfig("persistor_address", "vaultee.dal");

    loginHandler = new Handler<Message<JsonObject>>() {
      public void handle(Message<JsonObject> message) {
        doLogin(message);
      }
    };
    eb.registerHandler(address + ".login", loginHandler);

    logoutHandler = new Handler<Message<JsonObject>>() {
      public void handle(Message<JsonObject> message) {
        doLogout(message);
      }
    };
    eb.registerHandler(address + ".logout", logoutHandler);

    authorizeHandler = new Handler<Message<JsonObject>>() {
      public void handle(Message<JsonObject> message) {
        doAuthorize(message);
      }
    };
    eb.registerHandler(address + ".authorize", authorizeHandler);

    getuniqueidHandler = new Handler<Message<JsonObject>>() {
      public void handle(Message<JsonObject> message) {
        doGetUniqueId(message);
      }
    };
    eb.registerHandler(address + ".getuniqueid", getuniqueidHandler);
  }

  private void doLogin(final Message<JsonObject> message) {

    final String email = getMandatoryString("email", message);
    if (email == null) {
      logger.error("Failed to execute login query: " + message.body.getString("message"));
      sendError(message, "Failed to execute login");  
    }
    
    final String password = getMandatoryString("password", message);
    if (password == null) {
      logger.error("Failed to execute login query: " + message.body.getString("message"));
      sendError(message, "Failed to execute login");  
    }

    logger.info(String.format("\nbefore auth message\nemail=%s\npassword=%s", email, password));
    eb.send(persistorAddress + ".authenticate", message.body, new Handler<Message<JsonObject>>() {
      public void handle(Message<JsonObject> reply) {
        if (reply.body.getString("status").equals("ok")) {
          Long uid = reply.body.getNumber("uid").longValue();
          if (uid != 0) {
            try {
                logger.info("seed="+Security.hash(password, KEY));
                
                String pwd = reply.body.getString("pwd");
                logger.info(String.format("body=%s",reply.body));
                logger.info(String.format("pwd=%s",pwd));
                if (!Security.check(password, pwd, KEY)) {
                  logger.error("Failed to execute login query: " + reply.body.getString("message"));
                  sendError(message, "Failed to execute login");
                  return;
                }

                SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
                String randomNum = Integer.toString(prng.nextInt());
                MessageDigest sha = MessageDigest.getInstance("SHA-1");
                byte[] digest = sha.digest(randomNum.getBytes());

                String sDigest = digest.toString();

                logger.info(String.format("\nsDigest=%s", sDigest));

                final String sessionID = Security.bytesToHex(digest);
                final String cookie =  sessionID + "-" + Security.sign(sessionID, KEY);

                logger.info(String.format("\nsessionID=%s\ncookie=%s", sessionID, cookie));

              sessions.put(cookie, uid);

              JsonObject jsonReply = new JsonObject().putString("sessionID", cookie);
              sendOK(message, jsonReply);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            };              
          } else {
            // Not found
            sendStatus("denied", null);            
          }
          logger.info(String.format("auth replied %s", reply.body));
        } else {
          logger.error("Failed to execute login query: " + reply.body.getString("message"));
          sendError(message, "Failed to execute login");          
        }
      }
    });

    // logger.info(String.format("\nemail=%s\npassword=%s", email, password));

    // JsonObject findMsg = new JsonObject().putString("action", "findone").putString("collection", userCollection);
    // JsonObject matcher = new JsonObject().putString("email", email).putString("password", password);
    // findMsg.putObject("matcher", matcher);

    // eb.send(persistorAddress, findMsg, new Handler<Message<JsonObject>>() {
    //   public void handle(Message<JsonObject> reply) {

    //     if (reply.body.getString("status").equals("ok")) {
    //       if (reply.body.getObject("result") != null) {

    //         // Found

    //         try {
    //             SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
    //             String randomNum = Integer.toString(prng.nextInt());
    //             MessageDigest sha = MessageDigest.getInstance("SHA-1");
    //             byte[] digest = sha.digest(randomNum.getBytes());

    //             String sDigest = digest.toString();

    //             logger.info(String.format("\nsDigest=%s", sDigest));

    //             final String sessionID = Security.bytesToHex(digest);
    //             final String cookie =  sessionID + "-" + Security.sign(sessionID, KEY);

    //             logger.info(String.format("\nsessionID=%s\ncookie=%s", sessionID, cookie));

	   //          sessions.put(cookie, email);

	   //          JsonObject jsonReply = new JsonObject().putString("sessionID", cookie);
	   //          sendOK(message, jsonReply);
    //         } catch (NoSuchAlgorithmException e) {
    //             e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    //         };


    //       } else {
    //         // Not found
    //         sendStatus("denied", message);
    //       }
    //     } else {
    //       logger.error("Failed to execute login query: " + reply.body.getString("message"));
    //       sendError(message, "Failed to excecute login");
    //     }
    //   }
    // });
  }

  protected void doLogout(final Message<JsonObject> message) {
    final String sessionID = getMandatoryString("sessionID", message);
    if (sessionID != null) {
      if (logout(sessionID)) {
        sendOK(message);
      } else {
        super.sendError(message, "Not logged in");
      }
    }
  }

  protected boolean logout(String sessionID) {
    Long uid = sessions.remove(sessionID);
    if (uid != null) {
      return true;
    } else {
      return false;
    }
  }

  protected void doAuthorize(Message<JsonObject> message) {
    String cookie = getMandatoryString("sessionID", message);
    if (cookie == null) {
      return;
    }

    if (sessions.containsKey(cookie)) {
      sendOK(message, null);
    } else {
      sendStatus("denied", message);      
    }

    // logger.info("got cookie = " + cookie);

    // String[] parts;
    // if (cookie.contains("-")) {
    //   parts = cookie.split("-");
    // } else {
    //   sendStatus("denied", message);
    //   return;
    // }

    // // logger.info("parts split fine");

    // // logger.info("part[0] = " + parts[0]);
    // // logger.info("part[1] = " + parts[1]);

    // String signature = Security.sign(parts[0], KEY);

    // // logger.info("signatu = " + signature);

    // if (!parts[1].equals(signature)) {
    //   sendStatus("denied", message);
    //   return;
    // }

    // String email = sessions.get(parts[0]);

    // // logger.info("this is my email: " + email);

    // // In this basic auth manager we don't do any resource specific authorisation
    // // The user is always authorised if they are logged in

    // if (email != null) {
    //   authCache.put(cookie, email);
    //   JsonObject reply = new JsonObject().putString("email", email);
    //   sendOK(message, reply);
    // } else {

    // }
  }

  protected void doGetUniqueId(Message<JsonObject> message) {
    String sid = getMandatoryString("sessionID", message);
    if (sessions.containsKey(sid)) {
      message.body.putNumber("uid", sessions.get(sid));
      sendOK(message, message.body);
    } else {
      sendStatus("denied", message);
    }
  }
}
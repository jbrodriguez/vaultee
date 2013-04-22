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


public class Core extends BusModBase {
	private Handler<Message<JsonObject>> getUserDataHandler;
	private Handler<Message<JsonObject>> getAssetsHandler;
	private Handler<Message<JsonObject>> getRevisionsHandler;
	private Handler<Message<JsonObject>> getItemsHandler;
	// private Handler<Message<JsonObject>> getAssetHandler;
	private Handler<Message<JsonObject>> getCategoriesHandler;
	private Handler<Message<JsonObject>> logoutHandler;
	private Handler<Message<JsonObject>> authorizeHandler;

	protected final Map<String, String> sessions = new HashMap<>();
	// protected final Map<String, LoginInfo> logins = new HashMap<>();

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
		this.persistorAddress = getOptionalStringConfig("persistor_address", "vertx.mongopersistor");

		getUserDataHandler = new Handler<Message<JsonObject>>() {
		  public void handle(Message<JsonObject> message) {
		    doGetUserData(message);
		  }
		};
		eb.registerHandler("vc.getuserdata", getUserDataHandler);

		getAssetsHandler = new Handler<Message<JsonObject>>() {
		  public void handle(Message<JsonObject> message) {
		    doGetAssets(message);
		  }
		};
		eb.registerHandler("vc.getassets", getAssetsHandler);		

		getRevisionsHandler = new Handler<Message<JsonObject>>() {
		  public void handle(Message<JsonObject> message) {
		    doGetRevisions(message);
		  }
		};
		eb.registerHandler("vc.getrevisions", getRevisionsHandler);		

		getItemsHandler = new Handler<Message<JsonObject>>() {
		  public void handle(Message<JsonObject> message) {
		    doGetItems(message);
		  }
		};
		eb.registerHandler("vc.getitems", getItemsHandler);		

		getCategoriesHandler = new Handler<Message<JsonObject>>() {
		  public void handle(Message<JsonObject> message) {
		    doGetCategories(message);
		  }
		};
		eb.registerHandler("vc.getcategories", getCategoriesHandler);		

		// getAssetHandler = new Handler<Message<JsonObject>>() {
		// 	public void handle(Message<JsonObject> message) {
		// 		doGetAsset(message);
		// 	}
		// };
		// eb.registerHandler("vc.getasset", getAssetHandler);

    	logger.info("Core.java service started");		
	}

	private void doGetUserData(final Message<JsonObject> message) {
		logger.info(String.format("message=%s", message.body));

		eb.send(address + ".getuniqueid", message.body, new Handler<Message<JsonObject>>() {
			public void handle(Message<JsonObject> reply) {
				if (getMandatoryString("status", reply).equals("ok")) {
					Long uid = reply.body.getNumber("uid").longValue();
					JsonObject args = new JsonObject().putNumber("uid", uid);
					eb.send("vaultee.dal.getuserdata", args, new Handler<Message<JsonObject>>() {
				    	public void handle(Message<JsonObject> reply) {
				    		if (getMandatoryString("status", reply).equals("ok")) {
								// JsonObject respond = new JsonObject().putString("name", "Juan B. Rodriguez").putString("email", "jbrodriguez@gmail.com");
								logger.info(String.format("reply=%s", reply.body));
								sendOK(message, reply.body);				    		
							}
				    	}						
					});

				}
			}
		});
	}

	private void doGetAssets(final Message<JsonObject> message) {
		logger.info("inside doGetAssets");
		eb.send(address + ".getuniqueid", message.body, new Handler<Message<JsonObject>>() {
			public void handle(Message<JsonObject> reply) {
				if (getMandatoryString("status", reply).equals("ok")) {
					Long uid = reply.body.getNumber("uid").longValue();
					JsonObject args = new JsonObject().putNumber("uid", uid);
					eb.send("vaultee.dal.getassets", args, new Handler<Message<JsonObject>>() {
				    	public void handle(Message<JsonObject> reply) {
				    		if (getMandatoryString("status", reply).equals("ok")) {
								// JsonObject respond = new JsonObject().putString("name", "Juan B. Rodriguez").putString("email", "jbrodriguez@gmail.com");
								logger.info(String.format("\nreply=%s", reply.body));
								sendOK(message, reply.body);				    		
							}
				    	}						
					});

				}
			}
		});
	}

	private void doGetRevisions(final Message<JsonObject> message) {
		logger.info("inside doGetRevisions");
		eb.send(address + ".getuniqueid", message.body, new Handler<Message<JsonObject>>() {
			public void handle(Message<JsonObject> reply) {
				if (getMandatoryString("status", reply).equals("ok")) {
					eb.send("vaultee.dal.getrevisions", reply.body, new Handler<Message<JsonObject>>() {
				    	public void handle(Message<JsonObject> reply) {
				    		if (getMandatoryString("status", reply).equals("ok")) {
								// JsonObject respond = new JsonObject().putString("name", "Juan B. Rodriguez").putString("email", "jbrodriguez@gmail.com");
								logger.info(String.format("\nreply=%s", reply.body));
								sendOK(message, reply.body);				    		
							}
				    	}						
					});

				}
			}
		});
	}

	private void doGetItems(final Message<JsonObject> message) {
		logger.info("inside doGetItems");
		eb.send(address + ".getuniqueid", message.body, new Handler<Message<JsonObject>>() {
			public void handle(Message<JsonObject> reply) {
				if (getMandatoryString("status", reply).equals("ok")) {
					eb.send("vaultee.dal.getitems", reply.body, new Handler<Message<JsonObject>>() {
				    	public void handle(Message<JsonObject> reply) {
				    		if (getMandatoryString("status", reply).equals("ok")) {
								// JsonObject respond = new JsonObject().putString("name", "Juan B. Rodriguez").putString("email", "jbrodriguez@gmail.com");
								logger.info(String.format("\nreply=%s", reply.body));
								sendOK(message, reply.body);				    		
							}
				    	}						
					});

				}
			}
		});
	}

	private void doGetCategories(final Message<JsonObject> message) {
		logger.info("inside doGetCategories");
		eb.send(address + ".getuniqueid", message.body, new Handler<Message<JsonObject>>() {
			public void handle(Message<JsonObject> reply) {
				if (getMandatoryString("status", reply).equals("ok")) {
					eb.send("vaultee.dal.getcategories", reply.body, new Handler<Message<JsonObject>>() {
				    	public void handle(Message<JsonObject> reply) {
				    		if (getMandatoryString("status", reply).equals("ok")) {
								// JsonObject respond = new JsonObject().putString("name", "Juan B. Rodriguez").putString("email", "jbrodriguez@gmail.com");
								logger.info(String.format("\nreply=%s", reply.body));
								sendOK(message, reply.body);				    		
							}
				    	}						
					});

				}
			}
		});
	}	

	// private void doGetAsset(final Message<JsonObject> message) {
	// 	logger.info("inside doGetAsset");
	// 	logger.info(String.format("getAsset.message=%s", message.body));
	// 	eb.send(address + ".getuniqueid", message.body, new Handler<Message<JsonObject>>() {
	// 		public void handle(Message<JsonObject> reply) {
	// 			if (getMandatoryString("status", reply).equals("ok")) {
	// 				logger.info(String.format("getAsset.reply=%s", reply.body));
	// 				eb.send("vaultee.dal.getasset", reply.body, new Handler<Message<JsonObject>>() {
	// 			    	public void handle(Message<JsonObject> reply) {
	// 			    		if (getMandatoryString("status", reply).equals("ok")) {
	// 							// JsonObject respond = new JsonObject().putString("name", "Juan B. Rodriguez").putString("email", "jbrodriguez@gmail.com");
	// 							logger.info(String.format("reply=%s", reply.body));
	// 							sendOK(message, reply.body);				    		
	// 						}
	// 			    	}						
	// 				});

	// 			}
	// 		}
	// 	});
	// }

};
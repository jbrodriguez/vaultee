package net.apertoire.vaultee;

import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.json.JsonArray;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Iterator;
import java.util.Map;
// import java.util.Date;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
// import java.text.SimpleDateFormat;
// import java.text.ParseException;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DAL extends BusModBase {
	private String address;
	private String host;
	private int port;
	private String dbName;
	private String username;
	private String password;
	private String dbUrl;	

	private Handler<Message<JsonObject>> authHandler;
	private Handler<Message<JsonObject>> getUserDataHandler;
	private Handler<Message<JsonObject>> getAssetsHandler;
	private Handler<Message<JsonObject>> getRevisionsHandler;
	private Handler<Message<JsonObject>> getItemsHandler;
	private Handler<Message<JsonObject>> getCategoriesHandler;
	private Handler<Message<JsonObject>> saveAssetHandler;

	// private Handler<Message<JsonObject>> getAssetHandler;

    private Connection con;

    private PreparedStatement authenticate;
    private PreparedStatement getUserDataById;
    private PreparedStatement getAssets;
    private PreparedStatement getRevisions;
    private PreparedStatement getItems;
    private PreparedStatement getCategories;
    private PreparedStatement getLastRevision;
    private PreparedStatement getAsset;

    private PreparedStatement putAsset;
    private PreparedStatement putProduct;
    private PreparedStatement putRevision;
    private PreparedStatement putItem;

    ObjectMapper mapper;
    // SimpleDateFormat format2;
	DateTimeFormatter format;

	public void start() {
		super.start();

	    address = getOptionalStringConfig("address", "vaultee.dal");
	    host = getOptionalStringConfig("host", "localhost");
	    port = getOptionalIntConfig("port", 5432);
	    dbName = getOptionalStringConfig("db_name", "vaultee");
	    username = getOptionalStringConfig("username", null);
	    password = getOptionalStringConfig("password", null);

	    String dbUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName);

		try {
			con = DriverManager.getConnection(dbUrl, username, password);

		    authenticate = con.prepareStatement("select id, password from account where email=?");
		    getUserDataById = con.prepareStatement("select name, email from account where id=?");
		    getAssets = con.prepareStatement("select asset.id, asset.name, asset.category, asset.created, asset.modified, assetCategory.name as categoryName from asset, assetCategory where asset.account_id = ? and assetCategory.id = asset.category order by asset.created desc");
		    getRevisions = con.prepareStatement("select id, asset_id, index, created from revision where asset_id = ? order by index desc");
		    getItems = con.prepareStatement("select prod.id, prod.name, prod.asin, prod.upc, it.id as typeId, it.name as typeName,  itm.quantity, itm.price, itm.reference from itemtype it, product prod, item itm, asset ast, revision rev where ast.id = ? and rev.id = ? and itm.revision_id = rev.id and prod.id = itm.product_id and it.id = prod.itemtype_id order by prod.itemtype_id asc");
		    getCategories = con.prepareStatement("select id, name from itemtype");
		    getLastRevision = con.prepareStatement("select max(index) as index from revision where asset_id = ?");
		    getAsset = con.prepareStatement("select asset.id, asset.name, asset.category, asset.created, asset.modified, assetCategory.name as categoryName from asset, assetCategory where asset.id = ? and asset.account_id = ? and assetCategory.id = asset.category order by asset.created desc");

		    putAsset = con.prepareStatement("insert into asset (account_id, name, category, created, modified) values (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		    putProduct = con.prepareStatement("insert into product (itemtype_id, name, asin, sku, upc, ean) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		    putRevision = con.prepareStatement("insert into revision (asset_id, index, created) values (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		    putItem = con.prepareStatement("insert into item (revision_id, product_id, reference, quantity, price) values (?, ?, ?, ?, ?)");

		    mapper = new ObjectMapper();
		    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		    // format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssX");
		    format  = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSSZ");

			authHandler = new Handler<Message<JsonObject>>() {
			  public void handle(Message<JsonObject> message) {
			    doAuthenticate(message);
			  }
			};
			eb.registerHandler(address + ".authenticate", authHandler);

			getUserDataHandler = new Handler<Message<JsonObject>>() {
				public void handle(Message<JsonObject> message) {
					doGetUserData(message);
				}
			};
			eb.registerHandler(address + ".getuserdata", getUserDataHandler);


			getAssetsHandler = new Handler<Message<JsonObject>>() {
				public void handle(Message<JsonObject> message) {
					doGetAssets(message);
				}
			};
			eb.registerHandler(address + ".getassets", getAssetsHandler);

			getRevisionsHandler = new Handler<Message<JsonObject>>() {
				public void handle(Message<JsonObject> message) {
					doGetRevisions(message);
				}
			};
			eb.registerHandler(address + ".getrevisions", getRevisionsHandler);			

			getItemsHandler = new Handler<Message<JsonObject>>() {
				public void handle(Message<JsonObject> message) {
					doGetItems(message);
				}
			};
			eb.registerHandler(address + ".getitems", getItemsHandler);			

			getCategoriesHandler = new Handler<Message<JsonObject>>() {
				public void handle(Message<JsonObject> message) {
					doGetCategories(message);
				}
			};
			eb.registerHandler(address + ".getcategories", getCategoriesHandler);

			saveAssetHandler = new Handler<Message<JsonObject>>() {
				public void handle(Message<JsonObject> message) {
					doSaveAsset(message);
				}
			};
			eb.registerHandler(address + ".saveasset", saveAssetHandler);				
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}	

	public void stop() {
		try {
			con.close();
			// handle.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void doAuthenticate(final Message<JsonObject> message) {
	    final String email = getMandatoryString("email", message);
	    if (email == null) {
	      return;
	    }

	    final String password = getMandatoryString("password", message);
	    if (password == null) {
	      return;
	    }

	    try {
	    	// Long id = handle.createQuery("select id from account where email = :email")
	    	// 				.bind("email", email);

	    	authenticate.setString(1, email);

	    	ResultSet rs = authenticate.executeQuery();
	    	if (rs.next()) {
	    		Long id = rs.getLong("id");
	    		String pwd = rs.getString("password");
				logger.info(String.format("Getting ready to send back uid = %s", id.toString()));

	            JsonObject reply = new JsonObject().putNumber("uid", id).putString("pwd", pwd);
				sendOK(message, reply);
	    	}


	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}

	private void doGetUserData(final Message<JsonObject> message) {
		final Long uid = message.body.getNumber("uid").longValue();

	    try {
			getUserDataById.setLong(1, uid);

	    	ResultSet row = getUserDataById.executeQuery();
	    	if (row.next()) {
				sendOK(message, new JsonObject().putString("name", row.getString("name")).putString("email", row.getString("email")));
	    	}
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }		
	}

	private void doGetAssets(final Message<JsonObject> message) {
		final Long uid = message.body.getNumber("uid").longValue();

		try {
			// String sql = String.format("select asset.id, asset.name, asset.category, asset.created, asset.modified, assetCategory.name as categoryName from asset, assetCategory where asset.account_id = %d and assetCategory.id = asset.category order by asset.created desc;", uid);
			// String sql = String.format("select asset.id, asset.name, asset.category, asset.created, asset.modified, assetCategory.name as categoryName from asset, assetCategory where asset.account_id = %d and assetCategory.id = asset.category order by asset.created desc;", uid);

			getAssets.setLong(1, uid);

			JsonArray assets = new JsonArray();

			ResultSet row = getAssets.executeQuery();
			while (row.next()) {
				DateTime created = DateTime.parse(row.getString("created"), format);
				DateTime modified = DateTime.parse(row.getString("modified"), format);

				JsonObject asset = new JsonObject().putNumber("id", row.getLong("id")).putString("hash" , "").putString("name" , row.getString("name")).putNumber("category" , row.getLong("category")).putString("created" , format.print(created).replace(" ", "T")).putString("modified", format.print(modified).replace(" ", "T")).putString("categoryName", row.getString("categoryName"));
				assets.add(asset);
			}

			JsonObject reply = new JsonObject();
		   	reply.putArray("results", assets);
		   	sendOK(message, reply);			

		} catch(Exception e) {
			e.printStackTrace();
		}
	}	

	private void doGetRevisions(final Message<JsonObject> message) {
		final Long uid = message.body.getLong("uid");
		final Long aid = message.body.getLong("aid");

		logger.info(String.format("revision-message.body=%s", message.body));

		logger.info(String.format("rev-uid=%d\nrev-aid=%d", uid, aid));

		try {
			getRevisions.setLong(1, aid);

			JsonArray revisions = new JsonArray();

			ResultSet row = getRevisions.executeQuery();
			while (row.next()) {
				DateTime created = DateTime.parse(row.getString("created"), format);

				JsonObject rev = new JsonObject().putNumber("id", row.getLong("id")).putNumber("assetId" , row.getLong("asset_id")).putNumber("index" , row.getLong("index")).putString("created" , format.print(created).replace(" ", "T"));
				revisions.add(rev);
			}			

			// logger.info(String.format("assetId=%s,name=%s,assetcreated=%s,revid=%s,revindex=%s", row[0], row[2], row[4], row[6], row[7]));
			JsonObject reply = new JsonObject();
		   	reply.putArray("results", revisions);
		   	sendOK(message, reply);			

		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	private void doGetItems(final Message<JsonObject> message) {
		final Long uid = message.body.getLong("uid");
		final Long aid = message.body.getLong("aid");
		final Long rev = message.body.getLong("rev");

		logger.info(String.format("items-message.body=%s", message.body));

		logger.info(String.format("itm-uid=%d\nitm-aid=%d\nitm-rev=%d", uid, aid, rev));

		try {
		    // getItems = con.prepareStatement("select prod.id, prod.name, prod.upc, it.id as typeId, it.name as typeName,  itm.quantity, itm.price, itm.reference from itemtype it, product prod, item itm, asset ast, revision rev where ast.id = ? and rev.id = ? and itm.revision_id = rev.id and prod.id = itm.product_id and it.id = prod.itemtype_id order by prod.itemtype_id asc");

			getItems.setLong(1, aid);
			getItems.setLong(2, rev);

			JsonArray items = new JsonArray();

			ResultSet row = getItems.executeQuery();
			while (row.next()) {
				JsonObject item = new JsonObject().
										putObject("product", new JsonObject().
																putNumber("id" , row.getLong("id")).
																putObject("itemType", new JsonObject().
																						putNumber("id", row.getLong("typeId")).
																						putString("name", row.getString("typeName"))).
																putString("name" , row.getString("name")).
																putString("asin" , row.getString("asin")).
																putString("upc" , row.getString("upc"))).
										putString("reference" , row.getString("reference")).
										putNumber("quantity" , row.getLong("quantity")).
										putNumber("price" , new BigDecimal(row.getString("price")));
				items.add(item);
			}				

			// logger.info(String.format("assetId=%s,name=%s,assetcreated=%s,revid=%s,revindex=%s", row[0], row[2], row[4], row[6], row[7]));
			JsonObject reply = new JsonObject();
		   	reply.putArray("results", items);
		   	sendOK(message, reply);			
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	private void doGetCategories(final Message<JsonObject> message) {
		logger.info("getcategories");

		try {
			JsonArray types = new JsonArray();

			ResultSet row = getCategories.executeQuery();
			while (row.next()) {
				JsonObject type = new JsonObject().putNumber("id", row.getLong("id")).putString("name" , row.getString("name"));
				types.add(type);
			}				

			// logger.info(String.format("assetId=%s,name=%s,assetcreated=%s,revid=%s,revindex=%s", row[0], row[2], row[4], row[6], row[7]));
			JsonObject reply = new JsonObject();
		   	reply.putArray("results", types);
		   	sendOK(message, reply);			

		} catch(Exception e) {
			e.printStackTrace();
		}

	}	

	private Long getId(ResultSet rs) {
		Long id = 0L;

		try {
			if (rs != null && rs.next()) {
			    id = rs.getLong(1);
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}

		return id;
	}

	private void doSaveAsset(final Message<JsonObject> message) {
		final Long uid = message.body.getLong("uid");
		final Long aid = message.body.getLong("aid");
		String hash = message.body.getString("hash");

		Long productId = 0L;
		Long assetId = 0L;
		Long indexId = 0L;
		Long revisionId = 0L;

		try {
			con.setAutoCommit(false);

			logger.info(String.format("saveasset-message.body=%s", message.body));

			JsonArray array = message.body.getArray("items");

			logger.info("gotarray");

			String json = array.encode();

			logger.info(String.format("saveasset-items=%s", json));

			// save the products in the database, but only the ones with id 0
			Item[] items = mapper.readValue(json, Item[].class);

			logger.info(String.format("mapped just fine=%d", items.length));

			for (Item item : items) {
				if (item.getProduct().getId() == 0) {
					// send to database and save id back in the pojo
			    	// putProduct = con.prepareStatement("insert into product (itemtype_id, name, asin, sku, upc, ean) VALUES (?, ?, ?, ?, ?, ?)")

					logger.info("line 1");

					ItemType it = item.getProduct().getItemType();
					if (it == null) {
						logger.info("it is null");
					} else {
						logger.info("it is not null");
					}
					Long itid = it.getId();

					logger.info(String.format("line 1 - interstitial = %d", itid));

			    	putProduct.setLong(1, item.getProduct().getItemType().getId());
					logger.info("line 2");
					putProduct.setString(2, item.getProduct().getName());
					logger.info("line 3");
					putProduct.setString(3, item.getProduct().getAsin());
					logger.info("line 4");
					putProduct.setString(4, "");
					logger.info("line 5");
					putProduct.setString(5, item.getProduct().getUpc());
					logger.info("line 6");
					putProduct.setString(6, "");

					logger.info("prepareStatement = "+ putProduct.toString());

					putProduct.executeUpdate();
					productId = getId(putProduct.getGeneratedKeys());

					item.getProduct().setId(productId);

					logger.info(String.format("productloop.id=%s", productId));
				}
			}

			// get latest revision for this asset & add 1 to it
			if (aid == 0) {

				Long milis = DateTime.now().getMillis();

			    // putAsset = con.prepareStatement("insert into (account_id, name, category, created, modified) values (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
				putAsset.setLong(1, uid);
				putAsset.setString(2, message.body.getString("name"));
				putAsset.setLong(3, message.body.getLong("category"));
				putAsset.setTimestamp(4, new Timestamp(milis));
				putAsset.setTimestamp(5, new Timestamp(milis));

				putAsset.executeUpdate();
				assetId = getId(putAsset.getGeneratedKeys());

				logger.info(String.format("ifaid-1.assetid=%s", assetId));

				indexId = 1L;
			} else {
				// fetch from database
				getLastRevision.setLong(1, aid);
				ResultSet rs = getLastRevision.executeQuery();
				if (rs != null && rs.next()) {
					indexId = rs.getLong("index") + 1;
				}
				assetId = aid;
			}

		    // putRevision = con.prepareStatement("insert into revision (asset_id, index, created) values (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			putRevision.setLong(1, assetId);
			putRevision.setLong(2, indexId);
			putRevision.setTimestamp(3, new Timestamp(DateTime.now().getMillis()));

			putRevision.executeUpdate();
			revisionId = getId(putRevision.getGeneratedKeys());

			for (Item item : items) {
			    // putItem = con.prepareStatement("insert into item (revision_id, product_id, reference, quantity, price) values (?, ?, ?, ?, ?)");

				putItem.setLong(1, revisionId);
				putItem.setLong(2, item.getProduct().getId());
				putItem.setString(3, item.getReference());
				putItem.setLong(4, item.getQuantity());
				putItem.setBigDecimal(5, item.getPrice());

			    logger.info("putItem = "+ putItem.toString());

				putItem.executeUpdate();
			}

			con.commit();

			getAsset.setLong(1, assetId);
			getAsset.setLong(2, uid);

			JsonObject asset = null;

			ResultSet row = getAsset.executeQuery();
			if (row.next()) {
				DateTime created = DateTime.parse(row.getString("created"), format);
				DateTime modified = DateTime.parse(row.getString("modified"), format);

				asset = new JsonObject().putNumber("id", row.getLong("id")).putString("hash" , hash).putString("name" , row.getString("name")).putNumber("category" , row.getLong("category")).putString("created" , format.print(created).replace(" ", "T")).putString("modified", format.print(modified).replace(" ", "T")).putString("categoryName", row.getString("categoryName"));
			}

			JsonObject reply = new JsonObject();
		   	reply.putObject("asset", asset);
		   	sendOK(message, reply);		

			// update modified date on the asset


		} catch (SQLException se) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex1) {
                    logger.info(String.format("ex1.getMessage() = %s\nex1 = %s", ex1.getMessage(), ex1));
                }
            }

            logger.info(String.format("ex1.getMessage() = %s\nex1 = %s", se.getMessage(), se));
			se.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}
}
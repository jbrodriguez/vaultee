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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Iterator;
import java.util.Map;
// import java.util.Date;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
// import java.text.SimpleDateFormat;
// import java.text.ParseException;

public class Scraper extends BusModBase {
	private String address;
	// private String host;
	// private int port;
	// private String dbName;
	// private String username;
	// private String password;
	// private String dbUrl;	

	private Handler<Message<JsonObject>> scrapeItemHandler;

    ObjectMapper mapper;
    // SimpleDateFormat format2;
	DateTimeFormatter format;

	public void start() {
		super.start();

	    address = getOptionalStringConfig("address", "vaultee.scraper");
	    // host = getOptionalStringConfig("host", "localhost");
	    // port = getOptionalIntConfig("port", 5432);
	    // dbName = getOptionalStringConfig("db_name", "vaultee");
	    // username = getOptionalStringConfig("username", null);
	    // password = getOptionalStringConfig("password", null);

		try {

		    mapper = new ObjectMapper();
		    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		    // format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssX");
		    format  = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSSZ");

			scrapeItemHandler = new Handler<Message<JsonObject>>() {
			  public void handle(Message<JsonObject> message) {
			    doScrapeItem(message);
			  }
			};
			eb.registerHandler(address + ".scrapeitem", scrapeItemHandler);

			logger.info("Scraper.java service started");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}	

	public void stop() {
		// try {
		// 	con.close();
		// 	// handle.close();
		// } catch (SQLException e) {
		// 	e.printStackTrace();
		// }
	}

	private void doScrapeItem(final Message<JsonObject> message) {
		logger.info(String.format("scrapItem-message.body=%s", message.body));

		final String url = message.body.getString("reference");

		try {

			Document doc = Jsoup.connect(url).get();
			String name = doc.select("span#btAsinTitle").first().text();
			String asin = "";
			String upc = "";

			// String content = doc.select("td.bucket > div.content").first().html();
			// logger.info(String.format("td.bucket > div.content %s", content));

			for (Element e : doc.select("td.bucket > div.content > ul > li")) {
				// logger.info(String.format("td.bucket > div.content >%s", e.html()));
				logger.info(String.format("egetelementsb (%s)", e.getElementsByTag("b").text()));
				logger.info(String.format("etext (%s)", e.text()));

				if (e.getElementsByTag("b").text().equals("ASIN:")) {
					logger.info("inside asin ");
					asin = e.text().substring(6);
				}

				if (e.getElementsByTag("b").text().equals("Item model number:")) {
					logger.info("inside itm-model-nbr");
					upc = e.text().substring(19);
				}
			}

			logger.info(String.format("yes the name is really: (%s)", name));
			logger.info(String.format("yes the asin is really: (%s)", asin));
			logger.info(String.format("yes the upc is really: (%s)", upc));
			message.body.putObject("product", new JsonObject().putString("name", name).putString("asin", asin).putString("upc", upc));
			// JsonObject reply = new JsonObject().putString("name", name).putString("asin", asin).putString("upc", upc);
		   	sendOK(message, message.body);			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
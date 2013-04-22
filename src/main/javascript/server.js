load('vertx.js');

var webConf = {
	port: parseInt(vertx.env['PORT']),
	
	// Configuration for the event bus client side bridge
	// This bridges messages from the client side to the server side event bus
	bridge: true,

	// This defines which messages from the client we will let through
	// to the server side
	inbound_permitted: [
	// Allow calls to login and authorise
	{ address: 'load:user' },
	{ address: 'load:assets' },
	{ address: 'load:revisions' },
	{ address: 'load:items' },
	{ address: 'load:itemTypes' },
	// { address: 'vc.getuserdata' },
	// { address: 'vc.getassets' },
	// { address: 'vc.getrevisions' },
	// { address: 'vc.getitems' },
	// { address: 'vc.getasset' },
	// { address: 'vc.getcategories' },
	{ address: 'save:asset' },
	{ address: 'scrape:item' }
	],

	// This defines which messages from the server we will let through to the client
	outbound_permitted: [
	{}
	]    
};

var re = /^(postgres):\/\/(\S+):(\S+)@(\S+):(\S+)\/(\S+)$/;
var db = re.exec(vertx.env['DATABASE_URL']);

// logger.info("protocol: " + result[1]);
// logger.info("username: " + result[2]);
// logger.info("password: " + result[3]);
// logger.info("address: " + result[4]);
// logger.info("port: " + result[5]);
// logger.info("dbname: " + result[6]);

var dalConf = {
	host: db[4],
	username: db[2],
	password: db[3],
	port: parseInt(db[5]),
	dbname: db[6]
}

// vertx.deployVerticle('mongo-persistor', mongoConf);
vertx.deployWorkerVerticle('net.apertoire.vaultee.DAL', dalConf);
vertx.deployVerticle('net.apertoire.vaultee.AuthManager');
vertx.deployVerticle('net.apertoire.vaultee.Server', webConf);
//// vertx.deployVerticle('web-server', webConf);

vertx.deployVerticle('src/main/javascript/core.js');
//vertx.deployVerticle('net.apertoire.vaultee.Core');
vertx.deployVerticle('net.apertoire.vaultee.Scraper');

//vertx.deployModule('vertx.web-server-v1.0', webConf);
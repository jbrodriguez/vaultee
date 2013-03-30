load('vertx.js');

var webConf = {
	port: 9000,
	
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

var dalConf = {
	host: 'kepler.apertoire.org',
	username: 'apertoire',
	password: 'secret'
}

// vertx.deployVerticle('mongo-persistor', mongoConf);
vertx.deployWorkerVerticle('net.apertoire.vaultee.DAL', dalConf);
vertx.deployVerticle('net.apertoire.vaultee.AuthManager');
vertx.deployVerticle('net.apertoire.vaultee.Server', webConf);
//// vertx.deployVerticle('web-server', webConf);

vertx.deployVerticle('src/core2.js');
vertx.deployVerticle('net.apertoire.vaultee.Core');
vertx.deployVerticle('net.apertoire.vaultee.Scraper');

//vertx.deployModule('vertx.web-server-v1.0', webConf);
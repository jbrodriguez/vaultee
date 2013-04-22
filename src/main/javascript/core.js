load('vertx.js');
load('src/main/javascript/rsvp.js');

var config = vertx.config;
var logger = vertx.logger;
var eb = vertx.eventBus;

// this.address = getOptionalStringConfig("address", "vaultee.authmanager");
// this.userCollection = getOptionalStringConfig("user_collection", "users");
// this.persistorAddress = getOptionalStringConfig("persistor_address", "vertx.mongopersistor");

var send = function(message, params) {
    console.log("message: " + message);
    console.log("params: " + JSON.stringify(params));

    var promise = new RSVP.Promise();

    eb.send(message, params, handler);

    function handler(reply) {
    	console.log('this is what i got: '+JSON.stringify(reply));
    	if (reply.status === 'ok') {
    		promise.resolve(reply)
    	} else {
    		promise.reject(this);
    	}
    }

    return promise;
};


var address = "vaultee.authmanager";

logger.info("core.js service started")

eb.registerHandler('load:user', function(message, replier) {
	console.log('inside get user = ' + JSON.stringify(message));

	send(address + ".getuniqueid", message).
		then(function(json) {
			console.log('inside getunique = ' + JSON.stringify(json));
			return send("vaultee.dal.getuserdata", json);
		}).
		then(function(reply) {
			console.log('im the replirr');
			replier(reply);
		});
});

eb.registerHandler('load:assets', function(message, replier) {
	console.log('inside get assets = ' + JSON.stringify(message));

	send(address + ".getuniqueid", message).
		then(function(json) {
			console.log('inside getunique = ' + JSON.stringify(json));
			return send("vaultee.dal.getassets", json);
		}).
		then(function(reply) {
			console.log('ive got assets='+JSON.stringify(reply));
			replier(reply);
		});
});

eb.registerHandler('load:itemTypes', function(message, replier) {
	console.log('inside get itemTypes = ' + JSON.stringify(message));

	send(address + ".getuniqueid", message).
		then(function(json) {
			console.log('inside getunique itemtypes = ' + JSON.stringify(json));
			return send("vaultee.dal.getcategories", json);
		}).
		then(function(reply) {
			console.log('ive got itemTypes='+JSON.stringify(reply));
			replier(reply);
		});
});

eb.registerHandler('load:revisions', function(message, replier) {
	console.log('inside get revisions = ' + JSON.stringify(message));

	send(address + ".getuniqueid", message).
		then(function(json) {
			console.log('inside getunique revisions = ' + JSON.stringify(json));
			return send("vaultee.dal.getrevisions", json);
		}).
		then(function(reply) {
			console.log('ive got revisions='+JSON.stringify(reply));
			replier(reply);
		});
});


eb.registerHandler('load:items', function(message, replier) {
	console.log('inside get items = ' + JSON.stringify(message));

	send(address + ".getuniqueid", message).
		then(function(json) {
			console.log('inside getunique items = ' + JSON.stringify(json));
			return send("vaultee.dal.getitems", json);
		}).
		then(function(reply) {
			console.log('ive got items='+JSON.stringify(reply));
			replier(reply);
		});
});

eb.registerHandler('save:asset', function(message, replier) {
	logger.info("core2.js - saveasset= "+JSON.stringify(message));
	send(address + ".getuniqueid", message).
		then(function(message) {
			console.log('inside save: ' + JSON.stringify(message));

			send("vaultee.dal.saveasset", message).
				then(function(reply) {
					replier(reply);
				});
	});	
});

eb.registerHandler('scrape:item', function(message, replier) {
	logger.info('inside scrape:item = '+JSON.stringify(message));

	send('vaultee.scraper.scrapeitem', message).
		then(function(json) {
			logger.info('the ends justifies the means = '+JSON.stringify(json));
			replier(json);
		})
})
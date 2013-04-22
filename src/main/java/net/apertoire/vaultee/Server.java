/*
 * Copyright 2011-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.apertoire.vaultee;

import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.SockJSServer;
import org.vertx.java.core.buffer.Buffer;

import java.io.File;
import java.util.Map;
import java.util.List;

import org.jboss.netty.handler.codec.http.QueryStringDecoder;
/**
 * A simple web server module that can serve static files, and also can
 * bridge event bus messages to/from client side JavaScript and the server side
 * event bus.
 *
 * Please see the modules manual for full description of what configuration
 * parameters it takes.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Server extends BusModBase implements Handler<HttpServerRequest> {

  private String webRootPrefix;
  private String indexPage;

  public void start() {
    super.start();

    HttpServer server = vertx.createHttpServer();

    if (getOptionalBooleanConfig("ssl", false)) {
      server.setSSL(true).setKeyStorePassword(getOptionalStringConfig("key_store_password", "wibble"))
                         .setKeyStorePath(getOptionalStringConfig("key_store_path", "server-keystore.jks"));
    }

    if (getOptionalBooleanConfig("static_files", true)) {
      server.requestHandler(this);
    }

    boolean bridge = getOptionalBooleanConfig("bridge", false);
    if (bridge) {
      SockJSServer sjsServer = vertx.createSockJSServer(server);
      JsonArray inboundPermitted = getOptionalArrayConfig("inbound_permitted", new JsonArray());
      JsonArray outboundPermitted = getOptionalArrayConfig("outbound_permitted", new JsonArray());

      sjsServer.bridge(getOptionalObjectConfig("sjs_config", new JsonObject().putString("prefix", "/eventbus")),
                       inboundPermitted, outboundPermitted,
                       getOptionalLongConfig("auth_timeout", 5 * 60 * 1000),
                       getOptionalStringConfig("auth_address", "vaultee.authmanager.authorize"));
    }

    String webRoot = getOptionalStringConfig("web_root", "web");
    String index = getOptionalStringConfig("index_page", "index.html");
    webRootPrefix = webRoot + File.separator;
    indexPage = webRootPrefix + index;

    server.listen(getOptionalIntConfig("port", 80), getOptionalStringConfig("host", "0.0.0.0"));

    logger.info("Server.java service started");

  }

  public void handle(final HttpServerRequest req) {
    if (req.path.equals("/")) {
      req.response.sendFile(indexPage);
    } else if (req.path.equals("/auth")) {
      req.bodyHandler(new Handler<Buffer>() {
          @Override
          public void handle(Buffer buff) {
              String contentType = req.headers().get("Content-Type");
              if ("application/x-www-form-urlencoded".equals(contentType)) {
                  QueryStringDecoder qsd = new QueryStringDecoder(buff.toString(), false);
                  Map<String, List<String>> params = qsd.getParameters();

                  String email = params.get("email").get(0);
                  String password = params.get("password").get(0);

                  JsonObject args = new JsonObject();
                  args.putString("email", email);
                  args.putString("password", password);

                  eb.send("vaultee.authmanager.login", args, new Handler<Message<JsonObject>>() {
                    public void handle(Message<JsonObject> reply) {
                      if (getMandatoryString("status", reply).equals("ok")) {
                        req.response.headers().put("Set-Cookie", "vaultee_sessionid=" + getMandatoryString("sessionID", reply));
                        req.response.headers().put("Location", "/home.html");
                        req.response.statusCode = 302;
                        req.response.end();
                      }
                      else {
                        req.response.end("life is bad");
                      }
                    }
                  });
              }
          }
      });
    } else if (!req.path.contains("..")) {
      logger.info("serving " + req.path);
      req.response.sendFile(webRootPrefix + req.path);
    } else {
      req.response.statusCode = 404;
      req.response.end();
    }
  }
}

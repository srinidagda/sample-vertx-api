package com.abcplusd.sample;

import com.abcplusd.sample.model.IdName;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;


public class ApplicationVerticle  extends AbstractVerticle {
	private static final Logger LOG = LoggerFactory.getLogger(ApplicationVerticle.class);

@Override
	public void start(Future<Void> future) {
	LOG.info("Function begins from here - {start}");
	Router router = Router.router(vertx);
	
	router.route(HttpMethod.GET,"/welcome").handler(routingContext -> {
		LOG.info("Path is <{/welcome}>");
		routingContext.response()
		.putHeader("content-type", "text/html")
			.end("<h1>Hello from My First  Vert.x 3 application</h1>");
	});
	
	router.route(HttpMethod.GET, "/values").handler(routingContext -> {
		LOG.info("Path is <{/values}>");
		routingContext.response()
			.putHeader("content-type", "application/json charset=utf-8")
			.end(Json.encodePrettily(getIdName()));
		}
	);
	vertx.createHttpServer()
		.requestHandler(router::accept)
		.listen(config().getInteger("http.port", 9000),
			result -> {
				if (result.succeeded()) {
					future.complete();
				} else {
					future.fail(result.cause());
				}
			}
		);
	}
	
	public IdName getIdName() {
		IdName idName = new IdName();
		idName.setId(1l);
		idName.setName("Test");
		idName.setDescription("Test value");
		return idName;
	}
}

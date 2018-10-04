package com.abcplusd.sample;

import com.abcplusd.sample.model.IdName;
import com.abcplusd.sample.util.Constants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.reactivestreams.ReactiveReadStream;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.Date;
import java.util.stream.Stream;

public class ApplicationVerticle  extends AbstractVerticle {
	private static final Logger LOG = LoggerFactory.getLogger(ApplicationVerticle.class);
	private JDBCClient jdbcClient;
@Override
	public void start(Future<Void> future) {
	LOG.info("Function begins from here - <{}>","start");
		Future<Void> steps = prepareDatabase().compose(v -> startServer());
		steps.setHandler(future.completer());
	}
	
	private Future<Void> startServer() {
		LOG.info("Function begins from here - <{}>", "startServer");
		Future<Void> future = Future.future();
		final Router router = Router.router(vertx);
		
		router.route(HttpMethod.GET,"/welcome").handler(this::indexHandler);
		
		router.route(HttpMethod.GET, "/values").handler(this::streamData);
		
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
		return future;
	}
	
	private Future<Void> prepareDatabase() {
		LOG.info("Function begins from here - <{}>", "prepareDatabase");
		Future<Void> future = Future.future();
		jdbcClient = JDBCClient.createNonShared(vertx, new JsonObject()
			.put("url", "jdbc:postgresql://localhost:5432/testdb")
			.put("user", "test_admin")
			.put("password", "test123")
			.put("driver_class","org.postgresql.Driver"));
		
		jdbcClient.getConnection(res -> {
			if(res.succeeded()) {
				SQLConnection connection = res.result();
				connection.query(Constants.PING_QUERY, fetch -> {
					connection.close();
				if (fetch.failed()) {
					LOG.error("Database preparation error", fetch.cause());
					future.fail(fetch.cause());
				} else {
					future.complete();
				}
				});
			} else if (res.failed()){
				LOG.error("Could not open a database connection", res.cause());
				future.fail(res.cause());
			}
		});
		return future;
	}
	
	private void indexHandler(RoutingContext routingContext) {
		routingContext.response()
			.putHeader("content-type", "text/html")
			.end("<h1>Hello from My First  Vert.x 3 application</h1>");
	}
	
	private void streamData(RoutingContext routingContext) {
		LOG.info("Function begins from here", "streamData");
		Flux<IdName> stream = Flux.fromStream(Stream.generate(() -> new IdName(System.currentTimeMillis(), new Date())));
		Flux<Long> durationFlux = Flux.interval(Duration.ofSeconds(1));
		Flux<IdName> zipFlux = Flux.zip(stream, durationFlux).map(Tuple2::getT1);
		ReactiveReadStream<IdName> reactiveReadStream = ReactiveReadStream.readStream();
		zipFlux.subscribe(reactiveReadStream);
		HttpServerResponse httpServerResponse = routingContext.response();
		reactiveReadStream.handler(data -> {
			if (httpServerResponse.closed()) {
				return;
			}
			if(httpServerResponse.writeQueueFull()) {
				httpServerResponse.drainHandler((s) -> {
					reactiveReadStream.resume();
				});
				reactiveReadStream.pause();
			} else {
				httpServerResponse.putHeader("content-type", "application/json charset=utf-8");
				httpServerResponse.setChunked(true);
				System.out.println(data.toString());
				httpServerResponse.write(Json.encodePrettily(data));
			}
		}).endHandler(h -> {
			httpServerResponse.putHeader("content-type", "application/json charset=utf-8");
			httpServerResponse.end();
		});
	}
}

package com.abcplusd.sample;

import io.vertx.core.Vertx;

public class ApplicationStarted {
	public static void main(String args[]) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new ApplicationVerticle());
	}
}

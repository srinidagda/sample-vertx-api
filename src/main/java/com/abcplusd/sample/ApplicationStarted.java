package com.abcplusd.sample;

import io.vertx.core.Vertx;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class ApplicationStarted {
	public static void main(String args[]) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new ApplicationVerticle());
	}
	
	private static void applyBackPressure() {
		List<Integer> elements = new ArrayList<>();
		Flux.just(1,2,3,4)
			.log()
			.map(i -> i * 2)
			.onBackpressureBuffer()
			.subscribe(new Subscriber<Integer>() {
				private Subscription s;
				int onNextAmount;
				@Override public void onSubscribe(Subscription s) {
					this.s = s;
					s.request(4);
				}
				
				@Override public void onNext(Integer integer) {
					elements.add(integer);
					onNextAmount ++;
					if (onNextAmount%2 == 0) {
						s.request(4);
					}
				}
				
				@Override public void onError(Throwable t) {
				
				}
				
				@Override public void onComplete() {
					int ham = 2;
				}
			});
		elements.forEach(i -> System.out.println(i));
	}
}

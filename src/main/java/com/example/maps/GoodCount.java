package com.example.maps;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class GoodCount {

	private static Logger logger = Logger.getLogger(GoodCount.class.getName());
	
	private static Map<String, Integer> mapConcurrent = new ConcurrentHashMap<>();
	
	private static Map<String, AtomicInteger> mapConcurrentAtomic = new ConcurrentHashMap<>();
	
	private static final String KEY_TEST = "KEY";
	
	
	// Ejecutar con -Djava.util.concurrent.ForkJoinPool.common.parallelism=50
	public static void main(String[] args) {		
		IntStream.range(1, 10001).parallel().forEach(GoodCount::increaseCountCompute);
		
		logger.info("Valor contador:" + mapConcurrent.get(KEY_TEST));
		
		
		IntStream.range(1, 10001).parallel().forEach(a -> increaseCountCompute());
		
		logger.info("Valor contador AtomicInteger:" + mapConcurrentAtomic.get(KEY_TEST));
		
	}
	
	private static void increaseCountCompute(Integer count) {
		mapConcurrent.compute(KEY_TEST, (k ,v) -> v != null ? v + 1 : 1);
	}
	
	private static void increaseCountCompute() {
		
		mapConcurrentAtomic.compute(KEY_TEST, (k ,v) -> {
			if (v != null) {
				v.incrementAndGet();
				return v;
			}
			return new AtomicInteger(1);
		});
	}	
}

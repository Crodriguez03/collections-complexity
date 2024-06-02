package com.example.collectionscomplexity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class BadCount {

	private static Logger logger = Logger.getLogger(BadCount.class.getName());
	
	private static Map<String, Integer> map = new HashMap<>();
	
	private static Map<String, AtomicInteger> mapAtomicInteger = new HashMap<>();
	
	private static final String KEY_TEST = "KEY";
	
	
	// Ejecutar con -Djava.util.concurrent.ForkJoinPool.common.parallelism=50
	public static void main(String[] args) {		
		IntStream.range(1, 10001).parallel().forEach(BadCount::increaseCount);
		
		logger.info("Valor contador:" + map.get(KEY_TEST));
		
		map = new HashMap<>();
		
		// Aqui puede saltar una excepción de concurrencia dado que
		// HashMap no está preparado para ello
		IntStream.range(1, 10001).parallel().forEach(BadCount::increaseCountCompute);
		
		logger.info("Valor contador mediante compute siendo un HashMap:" + map.get(KEY_TEST));
		
		
		// Aquí puede no contar bien al principio cuando no existe la key en el mapa
		// al tener varios hilos a la vez preguntando por la misma key, si no existe los dos
		// la inicializan a la vez perdiendo valores
		IntStream.range(1, 10001).parallel().forEach(a -> increaseCount());
		
		logger.info("Valor contador AtomicInteger:" + mapAtomicInteger.get(KEY_TEST));
		
		mapAtomicInteger = new HashMap<>();
		
		IntStream.range(1, 10001).parallel().forEach(a -> increaseCountCompute());
		
		// Aqui puede saltar una excepción de concurrencia dado que
		// HashMap no está preparado para ello
		logger.info("Valor contador AtomicInteger mediante compute siendo un HashMap:" + mapAtomicInteger.get(KEY_TEST));
	}
	
	private static void increaseCount(Integer count) {
		
		Integer countMap = map.get(KEY_TEST);
		
		if(countMap != null) {
			map.put(KEY_TEST, countMap + 1);
		} else {
			map.put(KEY_TEST, 1);
		}
	}
	
	private static void increaseCountCompute(Integer count) {
		
		map.compute(KEY_TEST, (k ,v) -> {
			if (v != null) {
				return v + 1;
			}
			return 1;
		});
	}
	
	private static void increaseCount() {
		
		AtomicInteger countMap = mapAtomicInteger.get(KEY_TEST);
		
		if(countMap != null) {
			countMap.incrementAndGet();
		} else {
			mapAtomicInteger.put(KEY_TEST, new AtomicInteger(1));
		}
	}
	
	private static void increaseCountCompute() {
		
		mapAtomicInteger.compute(KEY_TEST, (k ,v) -> {
			if (v != null) {
				v.incrementAndGet();
				return v;
			}
			return new AtomicInteger(1);
		});
	}
}

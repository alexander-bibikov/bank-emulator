package com.app.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class Utils {
	public static BigDecimal amountOf(double value) {
		return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_DOWN);
	}

	public static void awaitWork(long mills) {
		long start_time = System.currentTimeMillis();
		long wait_time = mills;
		long end_time = start_time + wait_time;

		while (System.currentTimeMillis() < end_time) {
		}
	}

	public static void shutdownAndWait(ExecutorService es, long mills) {
		es.shutdown();
		try {
			if (!es.awaitTermination(mills, TimeUnit.MILLISECONDS)) {
				es.shutdownNow();
			}
		} catch (InterruptedException e) {
		}
	}
}

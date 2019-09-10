package com.app.main;

import com.app.api.Bank;
import com.app.bean.BankBean;
import com.app.task.MultipleTransferTask;
import com.app.utils.Utils;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Main {
	private static final int NACCOUNTS = 100;
	private static final BigDecimal INITIAL_BALANCE = Utils.amountOf(1000);
	private static Bank bank = new BankBean(NACCOUNTS, INITIAL_BALANCE);
	private static ExecutorService es = Executors.newFixedThreadPool(NACCOUNTS);


	public static void main(String[] args) {
		transfer();
		Utils.awaitWork(10000);
		Utils.shutdownAndWait(es, 1000);
	}

	static void transfer() {
		IntStream.range(0, NACCOUNTS).forEach(i -> {
			MultipleTransferTask task = new MultipleTransferTask(bank, i, INITIAL_BALANCE);
			es.submit(task);
		});
	}
}

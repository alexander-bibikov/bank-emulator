package com.app.task;

import com.app.api.Bank;
import com.app.utils.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MultipleTransferTask implements Runnable {
	private Bank bank;
	private int fromAccount;
	private BigDecimal maxAmount;

	public MultipleTransferTask(Bank b, int from, BigDecimal max) {
		bank = b;
		fromAccount = from;
		maxAmount = max;
	}

	@Override
	public void run() {
		try {
			while (!Thread.currentThread().isInterrupted()) {
				int toAccount = (int) (bank.getAccounts().size() * Math.random());
				BigDecimal amount = maxAmount.multiply(Utils.amountOf(Math.random())).setScale(2, RoundingMode.HALF_UP);
				bank.transfer(fromAccount, toAccount, amount);
				Thread.sleep((int) (10 * Math.random()));
			}
		} catch (InterruptedException e) {
		}
	}
}

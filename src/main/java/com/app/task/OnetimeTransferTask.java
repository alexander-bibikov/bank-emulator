package com.app.task;

import com.app.api.Bank;

import java.math.BigDecimal;

public class OnetimeTransferTask implements Runnable {
	private Bank bank;
	private int fromAccount;
	private int toAccount;
	private BigDecimal amount;
	private long mills = 0;

	public OnetimeTransferTask(Bank b, int from, int to, BigDecimal amount) {
		bank = b;
		fromAccount = from;
		toAccount = to;
		this.amount = amount;
	}

	public OnetimeTransferTask(Bank b, int from, int to, BigDecimal amount, long mills) {
		this(b, from, to, amount);
		this.mills = mills;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(mills);
			bank.transfer(fromAccount, toAccount, amount);
		} catch (Exception e) {
		}
	}
}

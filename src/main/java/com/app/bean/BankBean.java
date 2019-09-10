package com.app.bean;

import com.app.api.Bank;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BankBean implements Bank {
	private final List<BigDecimal> accounts;
	private Lock bankWriteLock;
	private Lock bankReadLock;
	private Condition sufficientFunds;

	public BankBean(int n, BigDecimal initialBalance) {
		accounts = new ArrayList<>();
		for (int i = 0; i < n; i++)
			accounts.add(initialBalance);
		ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
		bankWriteLock = rwl.writeLock();
		bankReadLock = rwl.readLock();
		sufficientFunds = bankWriteLock.newCondition();
	}

	@Override
	public void transfer(int from, int to, BigDecimal amount) throws InterruptedException {
		bankWriteLock.lock();
		try {
			while (getBalance(from).compareTo(amount) < 0)
				sufficientFunds.await();
			System.out.println(String.format("%s processed transfer with amount %s from account [{%s} Balance: %s] to [{%s} Balance: %s]",
					Thread.currentThread().getName(), amount, from, getBalance(from), to, getBalance(to)));
			accounts.set(from, accounts.get(from).subtract(amount));
			System.out.println(String.format("Account {%s} is debited with amount %s. Balance of {%s}: %s", from, amount, from, getBalance(from)));
			accounts.set(to, accounts.get(to).add(amount));
			System.out.println(String.format("Account {%s} is credited with amount %s. Balance of {%s}: %s", to, amount, to, getBalance(to)));
			System.out.println(String.format("Total Balance: %s", getTotalBalance()));
			System.out.println("---------------------------------------");
			sufficientFunds.signalAll();
		} finally {
			bankWriteLock.unlock();
		}
	}

	@Override
	public BigDecimal getTotalBalance() {
		bankReadLock.lock();
		try {
			return accounts.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
		} finally {
			bankReadLock.unlock();
		}
	}

	@Override
	public BigDecimal getBalance(int n) {
		bankReadLock.lock();
		try {
			return accounts.get(n);
		} finally {
			bankReadLock.unlock();
		}
	}

	@Override
	public List<BigDecimal> getAccounts() {
		return accounts;
	}
}
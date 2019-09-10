package com.app.api;

import java.math.BigDecimal;
import java.util.List;

public interface Bank {
	void transfer(int from, int to, BigDecimal amount) throws InterruptedException;

	BigDecimal getTotalBalance();

	BigDecimal getBalance(int n);

	List<BigDecimal> getAccounts();
}

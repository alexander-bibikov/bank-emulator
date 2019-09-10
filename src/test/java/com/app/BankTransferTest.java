package com.app;

import com.app.api.Bank;
import com.app.bean.BankBean;
import com.app.task.MultipleTransferTask;
import com.app.task.OnetimeTransferTask;
import com.app.utils.Utils;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertEquals;

public class BankTransferTest {
	private static final BigDecimal INITIAL_BALANCE = Utils.amountOf(1000);
	private ExecutorService es;
	private Bank bank;

	@Before
	public void setup() {
		es = Executors.newFixedThreadPool(10);
	}

	@Test
	public void simpleTransfer() {
		int naccounts = 2;
		bank = new BankBean(naccounts, INITIAL_BALANCE);

		OnetimeTransferTask task_1 = new OnetimeTransferTask(bank, 0, 1, Utils.amountOf(458.22));
		OnetimeTransferTask task_2 = new OnetimeTransferTask(bank, 1, 0, Utils.amountOf(78.99));

		Arrays.asList(task_1, task_2).forEach(task -> es.execute(task));

		Utils.shutdownAndWait(es, 5000);

		assertEquals(naccounts, bank.getAccounts().size());
		assertEquals(Utils.amountOf(620.77), bank.getBalance(0));
		assertEquals(Utils.amountOf(1379.23), bank.getBalance(1));
		assertEquals(Utils.amountOf(2000.00), bank.getTotalBalance());
	}

	@Test
	public void insufficientFunds() {
		int naccounts = 2;
		bank = new BankBean(naccounts, INITIAL_BALANCE);

		OnetimeTransferTask task_1 = new OnetimeTransferTask(bank, 0, 1, Utils.amountOf(1999.99));
		OnetimeTransferTask task_2 = new OnetimeTransferTask(bank, 1, 0, Utils.amountOf(999.99), 2000);

		Arrays.asList(task_1, task_2).forEach(task -> es.execute(task));

		Utils.shutdownAndWait(es, 5000);

		assertEquals(naccounts, bank.getAccounts().size());
		assertEquals(Utils.amountOf(0.00), bank.getBalance(0));
		assertEquals(Utils.amountOf(2000.00), bank.getBalance(1));
		assertEquals(Utils.amountOf(2000.00), bank.getTotalBalance());
	}

	@Test
	public void multipleTransfer() {
		int naccounts = 10;
		List<Runnable> taskList = new ArrayList<>();
		bank = new BankBean(naccounts, INITIAL_BALANCE);

		IntStream.range(0, naccounts).forEach(i ->
				taskList.add(new MultipleTransferTask(bank, i, INITIAL_BALANCE))
		);
		taskList.forEach(task -> es.execute(task));

		Utils.awaitWork(5000);
		Utils.shutdownAndWait(es, 0);

		assertEquals(naccounts, bank.getAccounts().size());
		assertEquals(Utils.amountOf(10000.00), bank.getTotalBalance());
	}
}

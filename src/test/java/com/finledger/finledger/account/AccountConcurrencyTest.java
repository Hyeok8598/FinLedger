package com.finledger.finledger.account;

import com.finledger.finledger.support.BaseIntegrationTest;
import org.aspectj.weaver.ast.Call;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class AccountConcurrencyTest extends BaseIntegrationTest {

    @Autowired
    AccountService accountService;

    @Test
    @DisplayName("동시 출금 시 optimistic lock 충돌이 발생한다")
    void concurrent_withdraw_triggers_optimistic_lock() throws Exception {
        Long accountId = accountService.createAccount("이아무개", new BigDecimal(0));
        accountService.deposit(accountId, new BigDecimal("1000"));

        int threads = 2;
        ExecutorService pool = Executors.newFixedThreadPool(threads);

        // 두 스레드를 같은 순간 출발
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);

        // 실행 결과 - 콘솔에 안나올 가능성이 있음
        List<Future<Void>> futures = new ArrayList<>();

        for(int i = 0; i < threads; i++) {
            futures.add(pool.submit(() -> {
                ready.countDown();
                start.await();

                accountService.withdraw(accountId, new BigDecimal("300"));
                return null;
            }));
        }

        ready.await();
        start.countDown();

        int optimisticFailures = 0;
        for(Future<Void> f : futures) {
            try {
                f.get();
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();

                if(cause instanceof ObjectOptimisticLockingFailureException) {
                    optimisticFailures++;
                } else {
                    throw e;
                }
            }
        }

        pool.shutdown();

        assertThat(optimisticFailures)
                .as("동시 수정 충돌로 Optimistic Lock 실패로 최소 1회는 발생해야 한다.")
                .isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("동일 출금계좌에 대한 동시 이체 요청에서도 잔액 정합성이 보장된다")
    void concurrenct_transfer_preserves_balance_consistency() throws Exception {
        Long fromId = accountService.createAccount("출금고객", new BigDecimal("10000"));
        Long toId1  = accountService.createAccount("입금고객1", new BigDecimal("0"));
        Long toId2  = accountService.createAccount("입금고객2", new BigDecimal("0"));

        int threads = 3;
        ExecutorService pool = Executors.newFixedThreadPool(threads);

        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);

        List<Callable<Void>> tasks = List.of(
                () -> {
                    ready.countDown();
                    start.await();
                    accountService.transfer(fromId, toId1, new BigDecimal("3000"));
                    return null;
                },
                () -> {
                    ready.countDown();
                    start.await();
                    accountService.transfer(fromId, toId2, new BigDecimal("4000"));
                    return null;
                },
                () -> {
                    ready.countDown();
                    start.await();
                    accountService.transfer(fromId, toId1, new BigDecimal("2000"));
                    return null;
                }
        );

        List<Future<Void>> futures = new ArrayList<>();

        for(Callable<Void> task : tasks) {
            futures.add(pool.submit(task));
        }

        ready.await();
        start.countDown();

        for(Future<Void> future : futures) {
            future.get();
        }

        pool.shutdown();

        BigDecimal fromBalance = accountService.getBalance(fromId);
        BigDecimal to1Balance  = accountService.getBalance(toId1);
        BigDecimal to2Balance  = accountService.getBalance(toId2);

        assertThat(fromBalance).isEqualByComparingTo("1000");
        assertThat(to1Balance).isEqualByComparingTo("5000");
        assertThat(to2Balance).isEqualByComparingTo("4000");
    }

    @Test
    @DisplayName("양밥향 동시 이체 요청에서도 deadlock 없이 정상 처리된다")
    void concurrency_bidirectional_transfer_completes_without_deadlock() throws Exception {
        Long accountAId = accountService.createAccount("고객A", new BigDecimal("10000"));
        Long accountBId = accountService.createAccount("고객B", new BigDecimal("10000"));

        int threads = 2;
        ExecutorService pool = Executors.newFixedThreadPool(threads);

        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);

        Callable<Void> task1 = () -> {
            ready.countDown();
            start.await();
            accountService.transfer(accountAId, accountBId, new BigDecimal("1000"));
            return null;
        };

        Callable<Void> task2 = () -> {
            ready.countDown();
            start.await();
            accountService.transfer(accountBId, accountAId, new BigDecimal("500"));
            return null;
        };

        List<Future<Void>> futures = new ArrayList<>();
        futures.add(pool.submit(task1));
        futures.add(pool.submit(task2));

        ready.await();
        start.countDown();

        for(Future<Void> future : futures) {
            /* 5초안에 작업 끝나면 통과, 안끝나면 TimeOut 예외발생(DeadLock 테스트) */
            future.get(5, TimeUnit.SECONDS);
        }

        pool.shutdown();

        BigDecimal accountABalance = accountService.getBalance(accountAId);
        BigDecimal accountBBalance = accountService.getBalance(accountBId);

        assertThat(accountABalance).isEqualByComparingTo("9500");
        assertThat(accountBBalance).isEqualByComparingTo("10500");
    }
}

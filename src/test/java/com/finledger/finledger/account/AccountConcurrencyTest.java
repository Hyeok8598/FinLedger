package com.finledger.finledger.account;

import com.finledger.finledger.support.BaseIntegrationTest;
import org.assertj.core.api.Assertions;
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
}

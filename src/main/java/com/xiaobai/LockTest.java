package com.xiaobai;


import com.xiaobai.lock.DistributedLock;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LockTest {

    public static DistributedLock lock = new DistributedLock();

    public static void main(String[] args) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(1000);
        ExecutorService executorService = Executors.newFixedThreadPool(1000);
        for (int i = 0; i < 1000; i++) {
            executorService.submit(new Client(i, cyclicBarrier));
        }
        executorService.shutdown();
    }

    static class Client implements Runnable {
        static int count = 1000;

        private int id;

        private CyclicBarrier cyclicBarrier;

        public Client(int id, CyclicBarrier cyclicBarrier) {
            this.id = id;
            this.cyclicBarrier = cyclicBarrier;
        }

        @Override
        public void run() {
            try {
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            lock.lock(id, 1000);
            System.out.println(count--);
            lock.unlock(id);
        }
    }
}

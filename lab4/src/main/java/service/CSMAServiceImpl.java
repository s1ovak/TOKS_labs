package service;

import java.util.Random;

public class CSMAServiceImpl implements CSMAService {

    @Override
    public void wait(int time) throws InterruptedException {
        Thread.sleep(time);
    }

    @Override
    public boolean isCollision() {
        return isSecondOdd();
    }

    @Override
    public int calculateDelay(int numberOfTry) {
        return new Random().nextInt((int) Math.pow(2, Math.min(10, numberOfTry))) * SLOT_TIME;
    }

    @Override
    public void waitChannelFree() throws InterruptedException {
        long interval = 100000;
        while (!isSecondOdd()) {
            long start = System.nanoTime();
            long end = 0;
            do {
                end = System.nanoTime();
            } while(start + interval >= end);
        }
    }


    private boolean isSecondOdd() {
        return (System.currentTimeMillis() % 2) == 0;
    }
}

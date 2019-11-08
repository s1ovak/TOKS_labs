package service;

public interface CSMAService {
    int SLOT_TIME = 10;
    int COLLISION_WINDOW = 5;

    void wait(int time) throws InterruptedException;
    boolean isCollision();
    int calculateDelay(int numberOfTry);
    void waitChannelFree() throws InterruptedException;
}

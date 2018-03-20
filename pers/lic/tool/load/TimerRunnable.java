package pers.lic.tool.load;

public interface TimerRunnable {
    void run();

    void stop();

    boolean timeOut(int i);
}

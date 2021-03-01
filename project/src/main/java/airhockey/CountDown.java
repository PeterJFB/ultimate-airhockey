package airhockey;

import javafx.application.Platform;

import java.util.Timer;
import java.util.TimerTask;

public class CountDown {
    private int startTime;
    private float time=startTime;
    private float timeIntervalInSeconds;

    public CountDown(int startTime, float timeIntervalInSeconds) {
        if (startTime <= 0) {
            throw new IllegalArgumentException("startTime must be a positive integer: " + startTime);
        }
        this.startTime = startTime;

        if (timeIntervalInSeconds <= 0) {
            throw new IllegalArgumentException("timeIntervalInSeconds must be a non-negative number: " + timeIntervalInSeconds);
        }
        this.timeIntervalInSeconds = timeIntervalInSeconds;
    }

    public boolean isFinished() {
        return time <= 0;
    }

    public void start() {
        time = startTime;
    }

    public void tick() {
        time = time > 0 ? time - timeIntervalInSeconds: time;
    }

    public int getTimeInSeconds() {
        return (int) Math.ceil(time);
}

}

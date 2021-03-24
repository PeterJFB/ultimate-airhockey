package airhockey;

public class CountDown {
    private final int startTime;
    private float time;
    private final float timeIntervalInSeconds;

    public CountDown(int startTime, float timeIntervalInSeconds) {
        if (startTime <= 0) {
            throw new IllegalArgumentException("startTime must be a positive integer: " + startTime);
        }
        this.startTime = startTime;
        this.time = startTime;

        if (timeIntervalInSeconds <= 0) {
            throw new IllegalArgumentException("timeIntervalInSeconds must be a non-negative number: " + timeIntervalInSeconds);
        }
        this.timeIntervalInSeconds = timeIntervalInSeconds;
    }

    // Getters and Setters

    public int getTimeInSeconds() {
        return (int) Math.ceil(getTime());
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        if (time <= 0)
            throw new IllegalArgumentException("Time left must be positive: " + time);
        this.time = time;
    }

    // Logic

    public boolean isFinished() {
        return getTime() <= 0;
    }

    public void tick() {
        if (!isFinished())
            time =  getTime() - timeIntervalInSeconds;
    }

}

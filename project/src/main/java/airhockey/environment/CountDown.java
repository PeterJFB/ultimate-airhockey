package airhockey.environment;

class CountDown {
    private final int startTime;
    private float time;
    private final float timeIntervalInSeconds;

    public CountDown(int startTime, float timeIntervalInSeconds) {
        if (startTime <= 0) {
            throw new IllegalArgumentException("startTime must be a non-negative integer: " + startTime);
        }
        this.startTime = startTime;
        this.time = startTime;

        if (timeIntervalInSeconds <= 0) {
            throw new IllegalArgumentException("timeIntervalInSeconds must be a positive number: " + timeIntervalInSeconds);
        }
        this.timeIntervalInSeconds = timeIntervalInSeconds;
    }

    // Getters and Setters

    public int getTimeInWholeSeconds() {
        return (int) Math.ceil(getTime());
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        if (time < 0)
            throw new IllegalArgumentException("Time left must be non-negative: " + time);
        this.time = time;
    }

    // Logic

    public boolean isFinished() {
        return getTime() <= 0;
    }

    public void tick() {
        if (!isFinished())
            time =  getTime() - timeIntervalInSeconds;
            if (time < 0)
                time = 0;
    }
}

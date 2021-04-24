package airhockey.environment;

/*
* Delegate used by Rink (as a countdown for the game) and PuckSpawner (as a countdown to spawn puck)
* */
class CountDown {
    private final int initialTime;
    private float time;
    private final float timeIntervalInSeconds;

    public CountDown(int initialTime, float timeIntervalInSeconds) {
        if (initialTime <= 0) {
            throw new IllegalArgumentException("initialTime must be a positive integer: " + initialTime);
        }
        this.initialTime = initialTime;
        this.time = initialTime;

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

    public int getInitialTime() {
        return initialTime;
    }

    public void setTime(float time) {
        if (time > initialTime)
            throw new IllegalArgumentException("Time left must be less than initial time (%s): %s".formatted(getInitialTime(), time));
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

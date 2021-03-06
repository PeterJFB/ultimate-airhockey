package airhockey.environment;

interface DiskObject {
    float getX();
    float getY();

    float getRadius();

    /*
    * Circles doesn't necessarily have a velocity, and can therefore choose to implement it.
    * */
    default float getVx() {
        return 0;
    }
    default float getVy() {
        return 0;
    }
}

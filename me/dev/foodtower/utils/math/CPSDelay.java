package me.dev.foodtower.utils.math;

public final class CPSDelay {
    private final TimerUtil timerUtils = new TimerUtil();

    public boolean shouldAttack(int cps) {
        int aps = 20 / cps;
        return timerUtils.hasReached(50 * aps);
    }

    public void reset() {
        timerUtils.reset();
    }
}
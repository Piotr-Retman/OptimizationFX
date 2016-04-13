package pl.zut.gui;

/**
 * Created by Retman on 2016-04-09.
 */
public class SolutionObject {
    private long finalDelay;
    private String finalOrder;
    private String baseOrder;
    private long staticBaseDelay;
    private String currentMakeTimes;
    private String currentDeadlineTimes;

    public long getFinalDelay() {
        return finalDelay;
    }

    public void setFinalDelay(long finalDelay) {
        this.finalDelay = finalDelay;
    }

    public String getFinalOrder() {
        return finalOrder;
    }

    public void setFinalOrder(String finalOrder) {
        this.finalOrder = finalOrder;
    }

    public String getBaseOrder() {
        return baseOrder;
    }

    public void setBaseOrder(String baseOrder) {
        this.baseOrder = baseOrder;
    }

    public long getStaticBaseDelay() {
        return staticBaseDelay;
    }

    public void setStaticBaseDelay(long staticBaseDelay) {
        this.staticBaseDelay = staticBaseDelay;
    }

    public String getCurrentMakeTimes() {
        return currentMakeTimes;
    }

    public void setCurrentMakeTimes(String currentMakeTimes) {
        this.currentMakeTimes = currentMakeTimes;
    }

    public String getCurrentDeadlineTimes() {
        return currentDeadlineTimes;
    }

    public void setCurrentDeadlineTimes(String currentDeadlineTimes) {
        this.currentDeadlineTimes = currentDeadlineTimes;
    }
}

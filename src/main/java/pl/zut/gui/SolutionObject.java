package pl.zut.gui;

import java.util.List;

/**
 * Created by Retman on 2016-04-09.
 */
public class SolutionObject {
    private String makeTimes;
    private String deadLineTimes;

    //MT
    private long mtDelay;
    private String mtOrder;
    //Mza
    private long mZaDelay;
    private String mZaOrder;
    //MO
    private long mODelay;
    private String mOOrder;
    //Mopt
    private long mOptDelay;
    private String mOptOrder;

    public long getMtDelay() {
        return mtDelay;
    }

    public void setMtDelay(long mtDelay) {
        this.mtDelay = mtDelay;
    }

    public String getMtOrder() {
        return mtOrder;
    }

    public void setMtOrder(String mtOrder) {
        this.mtOrder = mtOrder;
    }

    public long getmZaDelay() {
        return mZaDelay;
    }

    public void setmZaDelay(long mZaDelay) {
        this.mZaDelay = mZaDelay;
    }

    public String getmZaOrder() {
        return mZaOrder;
    }

    public void setmZaOrder(String mZaOrder) {
        this.mZaOrder = mZaOrder;
    }

    public long getmODelay() {
        return mODelay;
    }

    public void setmODelay(long mODelay) {
        this.mODelay = mODelay;
    }

    public String getmOOrder() {
        return mOOrder;
    }

    public void setmOOrder(String mOOrder) {
        this.mOOrder = mOOrder;
    }

    public long getmOptDelay() {
        return mOptDelay;
    }

    public void setmOptDelay(long mOptDelay) {
        this.mOptDelay = mOptDelay;
    }

    public String getmOptOrder() {
        return mOptOrder;
    }

    public void setmOptOrder(String mOptOrder) {
        this.mOptOrder = mOptOrder;
    }

    public String getMakeTimes() {
        return makeTimes;
    }

    public void setMakeTimes(String makeTimes) {
        this.makeTimes = makeTimes;
    }

    public String getDeadLineTimes() {
        return deadLineTimes;
    }

    public void setDeadLineTimes(String deadLineTimes) {
        this.deadLineTimes = deadLineTimes;
    }
}

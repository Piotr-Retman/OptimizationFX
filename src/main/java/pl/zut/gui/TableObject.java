package pl.zut.gui;

/**
 * Obiekt tablicowy do GUI
 */
public class TableObject {

    public TableObject(String order, String makeOrderTime, String deadlineOrderTime) {
        this.order = order;
        this.makeOrderTime = makeOrderTime;
        this.deadlineOrderTime = deadlineOrderTime;
    }

    private String order;
    private String makeOrderTime;
    private String deadlineOrderTime;

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getMakeOrderTime() {
        return makeOrderTime;
    }

    public void setMakeOrderTime(String makeOrderTime) {
        this.makeOrderTime = makeOrderTime;
    }

    public String getDeadlineOrderTime() {
        return deadlineOrderTime;
    }

    public void setDeadlineOrderTime(String deadlineOrderTime) {
        this.deadlineOrderTime = deadlineOrderTime;
    }
}

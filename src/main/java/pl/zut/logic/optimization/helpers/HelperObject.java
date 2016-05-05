package pl.zut.logic.optimization.helpers;

import pl.zut.logic.optimization.TypeSolution;

import java.util.List;

/**
 * Obiekt pomocniczy
 */
public class HelperObject {

    private TypeSolution typeSolution;
    private List<String> order;
    private String orderAsString;
    private long delay;

    public HelperObject(TypeSolution typeSolution, List<String> order, String orderAsString, long delay) {
        this.typeSolution = typeSolution;
        this.order = order;
        this.orderAsString = orderAsString;
        this.delay = delay;
    }

    public TypeSolution getTypeSolution() {
        return typeSolution;
    }

    public void setTypeSolution(TypeSolution typeSolution) {
        this.typeSolution = typeSolution;
    }

    public List<String> getOrder() {
        return order;
    }

    public void setOrder(List<String> order) {
        this.order = order;
    }

    public String getOrderAsString() {
        return orderAsString;
    }

    public void setOrderAsString(String orderAsString) {
        this.orderAsString = orderAsString;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }
}

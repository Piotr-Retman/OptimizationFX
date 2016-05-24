import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.junit.Assert;
import pl.zut.logic.optimization.LogicSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Retman on 2016-05-24.
 */
public class Test {

    @org.junit.Test
    public void test() {
        LogicSolution ls = new LogicSolution();
        List<String> order = new ArrayList<>();
        List<String> orderNames = new ArrayList<>();
        List<Long> makeOrderData = new ArrayList<>();
        List<Long> deadlineOrderData = new ArrayList<>();
        Long sum = 270L;

        orderNames.add("z1");
        orderNames.add("z2");
        orderNames.add("z3");
        orderNames.add("z4");
        orderNames.add("z5");

        deadlineOrderData.add(50L);
        deadlineOrderData.add(20L);
        deadlineOrderData.add(11L);
        deadlineOrderData.add(40L);
        deadlineOrderData.add(60L);

        makeOrderData.add(10L);
        makeOrderData.add(100L);
        makeOrderData.add(40L);
        makeOrderData.add(50L);
        makeOrderData.add(70L);

        order.add("z3");
        order.add("z1");
        order.add("z4");
        order.add("z5");
        order.add("z2");
        long l = ls.finalizeAlgorithm(order, makeOrderData, deadlineOrderData, sum, orderNames);
        Assert.assertTrue((l == 449L));
        System.out.println(l);
        order.clear();
        deadlineOrderData.clear();
        makeOrderData.clear();


        sum = 180L;
        orderNames.add("z1");
        orderNames.add("z2");
        orderNames.add("z3");
        orderNames.add("z4");

        deadlineOrderData.add(150L);
        deadlineOrderData.add(30L);
        deadlineOrderData.add(110L);
        deadlineOrderData.add(60L);

        makeOrderData.add(10L);
        makeOrderData.add(20L);
        makeOrderData.add(100L);
        makeOrderData.add(50L);

        order.add("z2");
        order.add("z4");
        order.add("z1");
        order.add("z3");
        l = ls.finalizeAlgorithm(order, makeOrderData, deadlineOrderData, sum, orderNames);
        System.out.println(l);
        Assert.assertTrue((l == 80L));
    }


    @org.junit.Test
    public void ordered_permutations () {

        List<Integer> vals = Lists.newArrayList(1, 2, 3);

        Collection<List<Integer>> orderPerm =
                Collections2.orderedPermutations(vals);

        for (List<Integer> val : orderPerm) {
            val.stream().forEach(System.out::println);
        }

        assertEquals(6, orderPerm.size());
    }
}

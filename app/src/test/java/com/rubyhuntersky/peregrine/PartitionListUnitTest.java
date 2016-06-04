package com.rubyhuntersky.peregrine;

import com.rubyhuntersky.peregrine.model.Partition;
import com.rubyhuntersky.peregrine.model.PartitionList;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author wehjin
 * @since 1/20/16.
 */

public class PartitionListUnitTest {

    private PartitionList partitionList;

    @Before
    public void setUp() throws Exception {
        partitionList = new PartitionList();
        final Partition p1 = new Partition("p1", "Cash", 3);
        final Partition p2 = new Partition("p2", "Bonds", 30);
        final Partition p3 = new Partition("p3", "Stocks", 100);
        partitionList.add(p1);
        partitionList.add(p2);
        partitionList.add(p3);
    }

    @Test
    public void partitionList_hasPartitions() {
        assertEquals(3, partitionList.getCount());
    }

    @Test
    public void partitionList_computesPortion() {
        assertEquals(.03, partitionList.getPortion("p1"), .000001);
        assertEquals((1 - .03) * .3, partitionList.getPortion("p2"), .000001);
        assertEquals((1 - .03) * (1 - .3) * 1, partitionList.getPortion("p3"), .000001);

        assertEquals(1,
              partitionList.getPortion("p1") + partitionList.getPortion("p2") + partitionList.getPortion("p3"),
              .000001);
    }

    // TODO Verify allocation errors and amounts add up.
    /*
                BigDecimal totalError = BigDecimal.ZERO;
            BigDecimal totalErrorDollars = BigDecimal.ZERO;
            BigDecimal totalTarget = BigDecimal.ZERO;
            for (Group group : groups) {
                final BigDecimal allocationError = group.getAllocationError(fullValue);
                totalError = totalError.add(allocationError);
                totalErrorDollars = totalErrorDollars.add(getAllocationErrorDollars(group));
                totalTarget = totalTarget.add(group.getTargetAllocation());
            }
            Log.d(TAG,
                  "Total error in groups: " + totalError.toPlainString() + ", " + UiHelper.getCurrencyDisplayString(
                        totalErrorDollars) + ", " + totalTarget);

     */
}

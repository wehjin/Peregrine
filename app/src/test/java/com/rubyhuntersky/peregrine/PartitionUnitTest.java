package com.rubyhuntersky.peregrine;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author wehjin
 * @since 1/20/16.
 */

public class PartitionUnitTest {

    private Partition partition;

    @Before
    public void setUp() throws Exception {
        partition = new Partition("p1", "Bonds", .3);
    }

    @Test
    public void partition_hasId() {
        assertEquals("p1", partition.id);
    }

    @Test
    public void partition_hasName() {
        assertEquals("Bonds", partition.name);
    }

    @Test
    public void partition_hasPortion() {
        assertEquals(.3, partition.percent, .01);
    }
}

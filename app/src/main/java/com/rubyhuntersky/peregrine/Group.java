package com.rubyhuntersky.peregrine;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wehjin
 * @since 11/30/15.
 */

public class Group {
    private final Partition partition;
    private final PartitionList partitionList;
    private final List<Asset> assets;

    public Group(Partition partition, PartitionList partitionList) {

        this.partition = partition;
        this.partitionList = partitionList;
        this.assets = new ArrayList<>();
    }

    public String getPartitionId() {
        return partition.id;
    }

    public BigDecimal getCurrentAllocation(BigDecimal portfolioValue) {
        return getValue().divide(portfolioValue, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getTargetAllocation() {
        double fraction = 1;
        for (Partition partition : partitionList.partitions) {
            final double partitionFraction = partition.relativePoints / 100;
            if (partition.id.equals(this.partition.id)) {
                fraction *= partitionFraction;
                break;
            } else {
                fraction *= (1 - partitionFraction);
            }
        }
        return new BigDecimal(fraction);
    }

    public String getName() {
        return partition.name;
    }

    public BigDecimal getValue() {
        BigDecimal value = BigDecimal.ZERO;
        for (Asset asset : assets) {
            value = value.add(asset.marketValue);
        }
        return value;
    }

    public void setAssets(PortfolioAssets portfolioAssets, Assignments assignments) {
        assets.clear();
        for (Asset asset : portfolioAssets.getAssets()) {
            final boolean inGroup = asset.isInGroup(this, assignments);
            if (inGroup) {
                assets.add(asset);
            }
        }
    }

    public BigDecimal getAllocationError(BigDecimal fullValue) {
        BigDecimal currentAllocation = getCurrentAllocation(fullValue);
        BigDecimal targetAllocation = getTargetAllocation();
        return currentAllocation.subtract(targetAllocation);
    }
}

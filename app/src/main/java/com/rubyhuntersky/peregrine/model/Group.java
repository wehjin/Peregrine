package com.rubyhuntersky.peregrine.model;

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
        return getValue().divide(portfolioValue, Values.SCALE, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getTargetAllocation() {
        return partitionList.getPortionBigDecimal(partition.id);
    }

    public String getName() {
        return partition.name;
    }

    public BigDecimal getValue() {
        BigDecimal value = BigDecimal.ZERO;
        for (Asset asset : assets) {
            value = value.add(asset.getMarketValue());
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

    public List<Asset> getAssets() {
        return assets;
    }
}

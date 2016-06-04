package com.rubyhuntersky.peregrine.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wehjin
 * @since 11/28/15.
 */

public class PartitionList {

    public List<Partition> partitions = new ArrayList<>();

    public PartitionList() {
    }

    public PartitionList(JSONObject jsonObject) throws JSONException {
        final JSONArray jsonArray = jsonObject.getJSONArray("partitions");
        for (int i = 0; i < jsonArray.length(); i++) {
            partitions.add(new Partition(jsonArray.getJSONObject(i)));
        }
    }

    public int getCount() {
        return partitions.size();
    }

    public String getName(String partitionId) {
        if (partitionId != null) {
            for (Partition partition : partitions) {
                if (partition.id.equals(partitionId)) {
                    return partition.name;
                }
            }
        }
        return null;
    }

    public BigDecimal getPortionBigDecimal(String partitionId) {

        BigDecimal cumulativePortion = BigDecimal.ONE;
        for (Partition partition : partitions) {
            final BigDecimal percent = BigDecimal.valueOf(partition.percent);
            final BigDecimal portion = percent.divide(BigDecimal.valueOf(100), Values.SCALE, BigDecimal.ROUND_HALF_UP);
            if (partition.id.equals(partitionId)) {
                return cumulativePortion.multiply(portion);
            } else {
                cumulativePortion = cumulativePortion.multiply(BigDecimal.ONE.subtract(portion));
            }
        }
        return BigDecimal.ZERO;
    }

    public double getPortion(String partitionId) {
        return getPortionBigDecimal(partitionId).doubleValue();
    }

    public int getIndex(String partitionId) {
        if (partitionId != null) {
            int index = 0;
            for (Partition partition : partitions) {
                if (partition.id.equals(partitionId)) {
                    return index;
                }
                index++;
            }
        }
        return -1;
    }

    public String[] toNamesArray(String first) {
        final String[] names = new String[partitions.size() + 1];
        int index = 0;
        names[index++] = first;
        for (Partition partition : partitions) {
            names[index++] = partition.name;
        }
        return names;
    }

    @Override
    public String toString() {
        return "PartitionList{" +
              "partitions=" + partitions +
              '}';
    }

    public List<Group> getGroups(PortfolioAssets portfolioAssets, Assignments assignments) {
        final ArrayList<Group> groups = new ArrayList<>();
        for (Partition partition : partitions) {
            final Group group = new Group(partition, this);
            group.setAssets(portfolioAssets, assignments);
            groups.add(group);
        }
        return groups;
    }

    public void add(Partition partition) {
        partitions.add(partition);
    }
}

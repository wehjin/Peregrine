package com.rubyhuntersky.peregrine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wehjin
 * @since 11/28/15.
 */

public class PartitionList {

    public List<Partition> partitions = new ArrayList<>();

    public PartitionList(JSONObject jsonObject) throws JSONException {
        final JSONArray jsonArray = jsonObject.getJSONArray("partitions");
        for (int i = 0; i < jsonArray.length(); i++) {
            partitions.add(new Partition(jsonArray.getJSONObject(i)));
        }
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
}

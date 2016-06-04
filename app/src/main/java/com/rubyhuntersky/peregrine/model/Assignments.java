package com.rubyhuntersky.peregrine.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author wehjin
 * @since 11/29/15.
 */

public class Assignments {
    public static final String ASSIGNMENTS = "assignments";
    private Map<String, String> assetIdToPartitionId = new HashMap<>();

    public Assignments() {
    }

    public Assignments(String jsonString) throws JSONException {
        if (jsonString == null) {
            return;
        }
        final JSONObject jsonObject = new JSONObject(jsonString);
        final JSONObject assignments = jsonObject.getJSONObject("assignments");
        final Iterator<String> keys = assignments.keys();
        while (keys.hasNext()) {
            final String assetId = keys.next();
            final String partitionId = assignments.getString(assetId);
            assetIdToPartitionId.put(assetId, partitionId);
        }
    }

    public Assignments(Map<String, String> assetIdToPartitionId) {
        this.assetIdToPartitionId.putAll(assetIdToPartitionId);
    }


    public JSONObject toJSONObject() throws JSONException {
        final JSONObject assignments = new JSONObject();
        for (String key : assetIdToPartitionId.keySet()) {
            final String value = assetIdToPartitionId.get(key);
            assignments.put(key, value);
        }
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(ASSIGNMENTS, assignments);
        return jsonObject;
    }

    public Assignments erasePartitionId(String assetId) {
        final HashMap<String, String> newMap = new HashMap<>(assetIdToPartitionId);
        newMap.remove(assetId);
        return new Assignments(newMap);
    }

    public Assignments setPartitionId(String assetId, String partitionId) {
        final HashMap<String, String> newMap = new HashMap<>(assetIdToPartitionId);
        newMap.put(assetId, partitionId);
        return new Assignments(newMap);
    }

    public String getPartitionId(String assetId) {
        if (assetId == null) {
            return null;
        }
        return assetIdToPartitionId.get(assetId);
    }
}

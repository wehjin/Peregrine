package com.rubyhuntersky.peregrine;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author wehjin
 * @since 11/28/15.
 */

public class Partition {

    public String name;
    public String id;
    public double relativePoints;

    public Partition(JSONObject jsonObject) throws JSONException {
        this.name = jsonObject.getString("name");
        this.id = jsonObject.getString("id");
        this.relativePoints = jsonObject.getDouble("relative-points");
    }

    @Override
    public String toString() {
        return "Partition{" +
              "name='" + name + '\'' +
              ", id='" + id + '\'' +
              ", relativePoints=" + relativePoints +
              '}';
    }
}

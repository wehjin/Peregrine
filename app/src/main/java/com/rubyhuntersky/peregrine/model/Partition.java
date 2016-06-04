package com.rubyhuntersky.peregrine.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author wehjin
 * @since 11/28/15.
 */

public class Partition {

    public String name;
    public String id;
    public double percent;

    public Partition(String id, String name, double percent) {
        this.id = id;
        this.name = name;
        this.percent = percent;
    }

    public Partition(JSONObject jsonObject) throws JSONException {
        this.name = jsonObject.getString("name");
        this.id = jsonObject.getString("id");
        this.percent = jsonObject.getDouble("relative-points");
    }

    @Override
    public String toString() {
        return "Partition{" +
              "name='" + name + '\'' +
              ", id='" + id + '\'' +
              ", percent=" + percent +
              '}';
    }
}

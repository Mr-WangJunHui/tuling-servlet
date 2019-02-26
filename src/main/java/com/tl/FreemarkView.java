package com.tl;

import java.util.HashMap;
import java.util.Map;

public class FreemarkView {
    private String path;
    private Map<String,Object> map = new HashMap<String,Object>();

    public FreemarkView(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(String key,Object value) {
        this.map.put(key,value);
    }
}

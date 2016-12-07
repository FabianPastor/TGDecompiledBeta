package org.telegram.messenger.volley.toolbox;

import org.telegram.messenger.volley.Cache;
import org.telegram.messenger.volley.Cache.Entry;

public class NoCache implements Cache {
    public void clear() {
    }

    public Entry get(String key) {
        return null;
    }

    public void put(String key, Entry entry) {
    }

    public void invalidate(String key, boolean fullExpire) {
    }

    public void remove(String key) {
    }

    public void initialize() {
    }
}

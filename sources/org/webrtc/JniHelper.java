package org.webrtc;

import java.io.UnsupportedEncodingException;
import java.util.Map;

class JniHelper {
    JniHelper() {
    }

    static byte[] getStringBytes(String s) {
        try {
            return s.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("ISO-8859-1 is unsupported");
        }
    }

    static Object getStringClass() {
        return String.class;
    }

    static Object getKey(Map.Entry entry) {
        return entry.getKey();
    }

    static Object getValue(Map.Entry entry) {
        return entry.getValue();
    }
}

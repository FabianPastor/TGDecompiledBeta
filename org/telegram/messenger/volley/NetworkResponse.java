package org.telegram.messenger.volley;

import java.util.Collections;
import java.util.Map;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;

public class NetworkResponse {
    public final byte[] data;
    public final Map<String, String> headers;
    public final long networkTimeMs;
    public final boolean notModified;
    public final int statusCode;

    public NetworkResponse(int statusCode, byte[] data, Map<String, String> headers, boolean notModified, long networkTimeMs) {
        this.statusCode = statusCode;
        this.data = data;
        this.headers = headers;
        this.notModified = notModified;
        this.networkTimeMs = networkTimeMs;
    }

    public NetworkResponse(int statusCode, byte[] data, Map<String, String> headers, boolean notModified) {
        this(statusCode, data, headers, notModified, 0);
    }

    public NetworkResponse(byte[] data) {
        this(Callback.DEFAULT_DRAG_ANIMATION_DURATION, data, Collections.emptyMap(), false, 0);
    }

    public NetworkResponse(byte[] data, Map<String, String> headers) {
        this(Callback.DEFAULT_DRAG_ANIMATION_DURATION, data, headers, false, 0);
    }
}

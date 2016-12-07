package org.telegram.messenger.volley.toolbox;

import android.os.Handler;
import android.os.Looper;
import org.telegram.messenger.volley.Cache;
import org.telegram.messenger.volley.NetworkResponse;
import org.telegram.messenger.volley.Request;
import org.telegram.messenger.volley.Request.Priority;
import org.telegram.messenger.volley.Response;

public class ClearCacheRequest extends Request<Object> {
    private final Cache mCache;
    private final Runnable mCallback;

    public ClearCacheRequest(Cache cache, Runnable callback) {
        super(0, null, null);
        this.mCache = cache;
        this.mCallback = callback;
    }

    public boolean isCanceled() {
        this.mCache.clear();
        if (this.mCallback != null) {
            new Handler(Looper.getMainLooper()).postAtFrontOfQueue(this.mCallback);
        }
        return true;
    }

    public Priority getPriority() {
        return Priority.IMMEDIATE;
    }

    protected Response<Object> parseNetworkResponse(NetworkResponse response) {
        return null;
    }

    protected void deliverResponse(Object response) {
    }
}

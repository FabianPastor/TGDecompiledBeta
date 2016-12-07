package org.telegram.messenger.volley.toolbox;

import android.os.SystemClock;
import com.coremedia.iso.boxes.AuthorBox;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.cookie.DateUtils;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.messenger.volley.AuthFailureError;
import org.telegram.messenger.volley.Cache.Entry;
import org.telegram.messenger.volley.Network;
import org.telegram.messenger.volley.NetworkError;
import org.telegram.messenger.volley.NetworkResponse;
import org.telegram.messenger.volley.NoConnectionError;
import org.telegram.messenger.volley.Request;
import org.telegram.messenger.volley.RetryPolicy;
import org.telegram.messenger.volley.ServerError;
import org.telegram.messenger.volley.TimeoutError;
import org.telegram.messenger.volley.VolleyError;
import org.telegram.messenger.volley.VolleyLog;

public class BasicNetwork implements Network {
    protected static final boolean DEBUG = VolleyLog.DEBUG;
    private static int DEFAULT_POOL_SIZE = 4096;
    private static int SLOW_REQUEST_THRESHOLD_MS = 3000;
    protected final HttpStack mHttpStack;
    protected final ByteArrayPool mPool;

    public BasicNetwork(HttpStack httpStack) {
        this(httpStack, new ByteArrayPool(DEFAULT_POOL_SIZE));
    }

    public BasicNetwork(HttpStack httpStack, ByteArrayPool pool) {
        this.mHttpStack = httpStack;
        this.mPool = pool;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public NetworkResponse performRequest(Request<?> request) throws VolleyError {
        int statusCode;
        IOException e;
        NetworkResponse networkResponse;
        long requestStart = SystemClock.elapsedRealtime();
        while (true) {
            byte[] responseContents;
            HttpResponse httpResponse = null;
            Map<String, String> responseHeaders = Collections.emptyMap();
            try {
                Map<String, String> headers = new HashMap();
                addCacheHeaders(headers, request.getCacheEntry());
                httpResponse = this.mHttpStack.performRequest(request, headers);
                StatusLine statusLine = httpResponse.getStatusLine();
                statusCode = statusLine.getStatusCode();
                responseHeaders = convertHeaders(httpResponse.getAllHeaders());
                if (statusCode == 304) {
                    break;
                }
                if (httpResponse.getEntity() != null) {
                    responseContents = entityToBytes(httpResponse.getEntity());
                } else {
                    responseContents = new byte[0];
                }
                try {
                    logSlowRequests(SystemClock.elapsedRealtime() - requestStart, request, responseContents, statusLine);
                    if (statusCode >= Callback.DEFAULT_DRAG_ANIMATION_DURATION && statusCode <= 299) {
                        return new NetworkResponse(statusCode, responseContents, responseHeaders, false, SystemClock.elapsedRealtime() - requestStart);
                    }
                } catch (SocketTimeoutException e2) {
                } catch (ConnectTimeoutException e3) {
                    attemptRetryOnException("connection", request, new TimeoutError());
                } catch (MalformedURLException e4) {
                    MalformedURLException e5 = e4;
                } catch (IOException e6) {
                    e = e6;
                    if (httpResponse == null) {
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                        VolleyLog.e("Unexpected response code %d for %s", Integer.valueOf(statusCode), request.getUrl());
                        if (responseContents == null) {
                            networkResponse = new NetworkResponse(statusCode, responseContents, responseHeaders, false, SystemClock.elapsedRealtime() - requestStart);
                            if (statusCode != 401 || statusCode == 403) {
                                attemptRetryOnException(AuthorBox.TYPE, request, new AuthFailureError(networkResponse));
                            } else {
                                throw new ServerError(networkResponse);
                            }
                        }
                        throw new NetworkError(null);
                    }
                    throw new NoConnectionError(e);
                }
            } catch (SocketTimeoutException e7) {
                responseContents = null;
                attemptRetryOnException("socket", request, new TimeoutError());
            } catch (ConnectTimeoutException e8) {
                responseContents = null;
                attemptRetryOnException("connection", request, new TimeoutError());
            } catch (MalformedURLException e9) {
                e5 = e9;
                responseContents = null;
            } catch (IOException e10) {
                e = e10;
                responseContents = null;
                if (httpResponse == null) {
                    throw new NoConnectionError(e);
                }
                statusCode = httpResponse.getStatusLine().getStatusCode();
                VolleyLog.e("Unexpected response code %d for %s", Integer.valueOf(statusCode), request.getUrl());
                if (responseContents == null) {
                    throw new NetworkError(null);
                }
                networkResponse = new NetworkResponse(statusCode, responseContents, responseHeaders, false, SystemClock.elapsedRealtime() - requestStart);
                if (statusCode != 401) {
                }
                attemptRetryOnException(AuthorBox.TYPE, request, new AuthFailureError(networkResponse));
            }
        }
        Entry entry = request.getCacheEntry();
        if (entry == null) {
            responseContents = null;
            return new NetworkResponse(304, null, responseHeaders, true, SystemClock.elapsedRealtime() - requestStart);
        }
        entry.responseHeaders.putAll(responseHeaders);
        responseContents = null;
        return new NetworkResponse(304, entry.data, entry.responseHeaders, true, SystemClock.elapsedRealtime() - requestStart);
        throw new RuntimeException("Bad URL " + request.getUrl(), e5);
    }

    private void logSlowRequests(long requestLifetime, Request<?> request, byte[] responseContents, StatusLine statusLine) {
        if (DEBUG || requestLifetime > ((long) SLOW_REQUEST_THRESHOLD_MS)) {
            String str = "HTTP response for request=<%s> [lifetime=%d], [size=%s], [rc=%d], [retryCount=%s]";
            Object[] objArr = new Object[5];
            objArr[0] = request;
            objArr[1] = Long.valueOf(requestLifetime);
            objArr[2] = responseContents != null ? Integer.valueOf(responseContents.length) : "null";
            objArr[3] = Integer.valueOf(statusLine.getStatusCode());
            objArr[4] = Integer.valueOf(request.getRetryPolicy().getCurrentRetryCount());
            VolleyLog.d(str, objArr);
        }
    }

    private static void attemptRetryOnException(String logPrefix, Request<?> request, VolleyError exception) throws VolleyError {
        RetryPolicy retryPolicy = request.getRetryPolicy();
        int oldTimeout = request.getTimeoutMs();
        try {
            retryPolicy.retry(exception);
            request.addMarker(String.format("%s-retry [timeout=%s]", new Object[]{logPrefix, Integer.valueOf(oldTimeout)}));
        } catch (VolleyError e) {
            request.addMarker(String.format("%s-timeout-giveup [timeout=%s]", new Object[]{logPrefix, Integer.valueOf(oldTimeout)}));
            throw e;
        }
    }

    private void addCacheHeaders(Map<String, String> headers, Entry entry) {
        if (entry != null) {
            if (entry.etag != null) {
                headers.put("If-None-Match", entry.etag);
            }
            if (entry.lastModified > 0) {
                headers.put("If-Modified-Since", DateUtils.formatDate(new Date(entry.lastModified)));
            }
        }
    }

    protected void logError(String what, String url, long start) {
        long now = SystemClock.elapsedRealtime();
        VolleyLog.v("HTTP ERROR(%s) %d ms to fetch %s", what, Long.valueOf(now - start), url);
    }

    private byte[] entityToBytes(HttpEntity entity) throws IOException, ServerError {
        PoolingByteArrayOutputStream bytes = new PoolingByteArrayOutputStream(this.mPool, (int) entity.getContentLength());
        byte[] buffer = null;
        try {
            InputStream in = entity.getContent();
            if (in == null) {
                throw new ServerError();
            }
            buffer = this.mPool.getBuf(1024);
            while (true) {
                int count = in.read(buffer);
                if (count == -1) {
                    break;
                }
                bytes.write(buffer, 0, count);
            }
            byte[] toByteArray = bytes.toByteArray();
            return toByteArray;
        } finally {
            try {
                entity.consumeContent();
            } catch (IOException e) {
                VolleyLog.v("Error occured when calling consumingContent", new Object[0]);
            }
            this.mPool.returnBuf(buffer);
            bytes.close();
        }
    }

    protected static Map<String, String> convertHeaders(Header[] headers) {
        Map<String, String> result = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < headers.length; i++) {
            result.put(headers[i].getName(), headers[i].getValue());
        }
        return result;
    }
}

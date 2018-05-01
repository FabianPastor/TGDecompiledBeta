package org.telegram.messenger.exoplayer2.upstream;

import android.net.Uri;
import android.util.Log;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.NoRouteToHostException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer2.upstream.HttpDataSource.HttpDataSourceException;
import org.telegram.messenger.exoplayer2.upstream.HttpDataSource.InvalidContentTypeException;
import org.telegram.messenger.exoplayer2.upstream.HttpDataSource.InvalidResponseCodeException;
import org.telegram.messenger.exoplayer2.upstream.HttpDataSource.RequestProperties;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Predicate;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;

public class DefaultHttpDataSource implements HttpDataSource {
    private static final Pattern CONTENT_RANGE_HEADER = Pattern.compile("^bytes (\\d+)-(\\d+)/(\\d+)$");
    public static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 8000;
    public static final int DEFAULT_READ_TIMEOUT_MILLIS = 8000;
    private static final long MAX_BYTES_TO_DRAIN = 2048;
    private static final int MAX_REDIRECTS = 20;
    private static final String TAG = "DefaultHttpDataSource";
    private static final AtomicReference<byte[]> skipBufferReference = new AtomicReference();
    private final boolean allowCrossProtocolRedirects;
    private long bytesRead;
    private long bytesSkipped;
    private long bytesToRead;
    private long bytesToSkip;
    private final int connectTimeoutMillis;
    private HttpURLConnection connection;
    private final Predicate<String> contentTypePredicate;
    private DataSpec dataSpec;
    private final RequestProperties defaultRequestProperties;
    private InputStream inputStream;
    private final TransferListener<? super DefaultHttpDataSource> listener;
    private boolean opened;
    private final int readTimeoutMillis;
    private final RequestProperties requestProperties;
    private final String userAgent;

    public DefaultHttpDataSource(String str, Predicate<String> predicate) {
        this(str, predicate, null);
    }

    public DefaultHttpDataSource(String str, Predicate<String> predicate, TransferListener<? super DefaultHttpDataSource> transferListener) {
        this(str, predicate, transferListener, 8000, 8000);
    }

    public DefaultHttpDataSource(String str, Predicate<String> predicate, TransferListener<? super DefaultHttpDataSource> transferListener, int i, int i2) {
        this(str, predicate, transferListener, i, i2, false, null);
    }

    public DefaultHttpDataSource(String str, Predicate<String> predicate, TransferListener<? super DefaultHttpDataSource> transferListener, int i, int i2, boolean z, RequestProperties requestProperties) {
        this.userAgent = Assertions.checkNotEmpty(str);
        this.contentTypePredicate = predicate;
        this.listener = transferListener;
        this.requestProperties = new RequestProperties();
        this.connectTimeoutMillis = i;
        this.readTimeoutMillis = i2;
        this.allowCrossProtocolRedirects = z;
        this.defaultRequestProperties = requestProperties;
    }

    public Uri getUri() {
        return this.connection == null ? null : Uri.parse(this.connection.getURL().toString());
    }

    public Map<String, List<String>> getResponseHeaders() {
        return this.connection == null ? null : this.connection.getHeaderFields();
    }

    public void setRequestProperty(String str, String str2) {
        Assertions.checkNotNull(str);
        Assertions.checkNotNull(str2);
        this.requestProperties.set(str, str2);
    }

    public void clearRequestProperty(String str) {
        Assertions.checkNotNull(str);
        this.requestProperties.remove(str);
    }

    public void clearAllRequestProperties() {
        this.requestProperties.clear();
    }

    public long open(DataSpec dataSpec) throws HttpDataSourceException {
        StringBuilder stringBuilder;
        this.dataSpec = dataSpec;
        long j = 0;
        this.bytesRead = 0;
        this.bytesSkipped = 0;
        try {
            this.connection = makeConnection(dataSpec);
            try {
                int responseCode = this.connection.getResponseCode();
                if (responseCode >= Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                    if (responseCode <= 299) {
                        String contentType = this.connection.getContentType();
                        if (this.contentTypePredicate == null || this.contentTypePredicate.evaluate(contentType)) {
                            if (responseCode == Callback.DEFAULT_DRAG_ANIMATION_DURATION && dataSpec.position != 0) {
                                j = dataSpec.position;
                            }
                            this.bytesToSkip = j;
                            if (dataSpec.isFlagSet(1)) {
                                this.bytesToRead = dataSpec.length;
                            } else {
                                long j2 = -1;
                                if (dataSpec.length != -1) {
                                    this.bytesToRead = dataSpec.length;
                                } else {
                                    j = getContentLength(this.connection);
                                    if (j != -1) {
                                        j2 = j - this.bytesToSkip;
                                    }
                                    this.bytesToRead = j2;
                                }
                            }
                            try {
                                this.inputStream = this.connection.getInputStream();
                                this.opened = true;
                                if (this.listener != null) {
                                    this.listener.onTransferStart(this, dataSpec);
                                }
                                return this.bytesToRead;
                            } catch (IOException e) {
                                closeConnectionQuietly();
                                throw new HttpDataSourceException(e, dataSpec, 1);
                            }
                        }
                        closeConnectionQuietly();
                        throw new InvalidContentTypeException(contentType, dataSpec);
                    }
                }
                Map headerFields = this.connection.getHeaderFields();
                closeConnectionQuietly();
                InvalidResponseCodeException invalidResponseCodeException = new InvalidResponseCodeException(responseCode, headerFields, dataSpec);
                if (responseCode == 416) {
                    invalidResponseCodeException.initCause(new DataSourceException(0));
                }
                throw invalidResponseCodeException;
            } catch (IOException e2) {
                closeConnectionQuietly();
                stringBuilder = new StringBuilder();
                stringBuilder.append("Unable to connect to ");
                stringBuilder.append(dataSpec.uri.toString());
                throw new HttpDataSourceException(stringBuilder.toString(), e2, dataSpec, 1);
            }
        } catch (IOException e22) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Unable to connect to ");
            stringBuilder.append(dataSpec.uri.toString());
            throw new HttpDataSourceException(stringBuilder.toString(), e22, dataSpec, 1);
        }
    }

    public int read(byte[] bArr, int i, int i2) throws HttpDataSourceException {
        try {
            skipInternal();
            return readInternal(bArr, i, i2);
        } catch (IOException e) {
            throw new HttpDataSourceException(e, this.dataSpec, 2);
        }
    }

    public void close() throws HttpDataSourceException {
        try {
            if (this.inputStream != null) {
                maybeTerminateInputStream(this.connection, bytesRemaining());
                this.inputStream.close();
            }
            this.inputStream = null;
            closeConnectionQuietly();
            if (this.opened) {
                this.opened = false;
                if (this.listener != null) {
                    this.listener.onTransferEnd(this);
                }
            }
        } catch (IOException e) {
            throw new HttpDataSourceException(e, this.dataSpec, 3);
        } catch (Throwable th) {
            this.inputStream = null;
            closeConnectionQuietly();
            if (this.opened) {
                this.opened = false;
                if (this.listener != null) {
                    this.listener.onTransferEnd(this);
                }
            }
        }
    }

    protected final HttpURLConnection getConnection() {
        return this.connection;
    }

    protected final long bytesSkipped() {
        return this.bytesSkipped;
    }

    protected final long bytesRead() {
        return this.bytesRead;
    }

    protected final long bytesRemaining() {
        return this.bytesToRead == -1 ? this.bytesToRead : this.bytesToRead - this.bytesRead;
    }

    private HttpURLConnection makeConnection(DataSpec dataSpec) throws IOException {
        DataSpec dataSpec2 = dataSpec;
        URL url = new URL(dataSpec2.uri.toString());
        byte[] bArr = dataSpec2.postBody;
        long j = dataSpec2.position;
        long j2 = dataSpec2.length;
        boolean isFlagSet = dataSpec2.isFlagSet(1);
        if (!this.allowCrossProtocolRedirects) {
            DefaultHttpDataSource defaultHttpDataSource;
            return makeConnection(url, bArr, j, j2, isFlagSet, true);
        }
        HttpURLConnection makeConnection;
        int i = 0;
        while (true) {
            int i2 = i + 1;
            int i3;
            if (i <= 20) {
                long j3 = j;
                i3 = i2;
                makeConnection = defaultHttpDataSource.makeConnection(url, bArr, j, j2, isFlagSet, false);
                int responseCode = makeConnection.getResponseCode();
                if (!(responseCode == 300 || responseCode == 301 || responseCode == 302 || responseCode == 303)) {
                    if (bArr == null) {
                        if (responseCode != 307) {
                            if (responseCode != 308) {
                                break;
                            }
                        } else {
                            continue;
                        }
                    } else {
                        break;
                    }
                }
                bArr = null;
                String headerField = makeConnection.getHeaderField("Location");
                makeConnection.disconnect();
                url = handleRedirect(url, headerField);
                defaultHttpDataSource = this;
                i = i3;
                j = j3;
            } else {
                i3 = i2;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Too many redirects: ");
                stringBuilder.append(i3);
                throw new NoRouteToHostException(stringBuilder.toString());
            }
        }
        return makeConnection;
    }

    private HttpURLConnection makeConnection(URL url, byte[] bArr, long j, long j2, boolean z, boolean z2) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setConnectTimeout(this.connectTimeoutMillis);
        httpURLConnection.setReadTimeout(this.readTimeoutMillis);
        if (this.defaultRequestProperties != null) {
            for (Entry entry : this.defaultRequestProperties.getSnapshot().entrySet()) {
                httpURLConnection.setRequestProperty((String) entry.getKey(), (String) entry.getValue());
            }
        }
        for (Entry entry2 : this.requestProperties.getSnapshot().entrySet()) {
            httpURLConnection.setRequestProperty((String) entry2.getKey(), (String) entry2.getValue());
        }
        if (!(j == 0 && j2 == -1)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("bytes=");
            stringBuilder.append(j);
            stringBuilder.append("-");
            String stringBuilder2 = stringBuilder.toString();
            if (j2 != -1) {
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append(stringBuilder2);
                stringBuilder3.append((j + j2) - 1);
                stringBuilder2 = stringBuilder3.toString();
            }
            httpURLConnection.setRequestProperty("Range", stringBuilder2);
        }
        httpURLConnection.setRequestProperty("User-Agent", this.userAgent);
        if (!z) {
            httpURLConnection.setRequestProperty("Accept-Encoding", "identity");
        }
        httpURLConnection.setInstanceFollowRedirects(z2);
        httpURLConnection.setDoOutput(bArr != null ? 1 : null);
        if (bArr != null) {
            httpURLConnection.setRequestMethod("POST");
            if (bArr.length == null) {
                httpURLConnection.connect();
            } else {
                httpURLConnection.setFixedLengthStreamingMode(bArr.length);
                httpURLConnection.connect();
                j = httpURLConnection.getOutputStream();
                j.write(bArr);
                j.close();
            }
        } else {
            httpURLConnection.connect();
        }
        return httpURLConnection;
    }

    private static URL handleRedirect(URL url, String str) throws IOException {
        if (str == null) {
            throw new ProtocolException("Null location redirect");
        }
        URL url2 = new URL(url, str);
        url = url2.getProtocol();
        if ("https".equals(url) != null || "http".equals(url) != null) {
            return url2;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unsupported protocol redirect: ");
        stringBuilder.append(url);
        throw new ProtocolException(stringBuilder.toString());
    }

    private static long getContentLength(java.net.HttpURLConnection r10) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r0 = "Content-Length";
        r0 = r10.getHeaderField(r0);
        r1 = android.text.TextUtils.isEmpty(r0);
        if (r1 != 0) goto L_0x002c;
    L_0x000c:
        r1 = java.lang.Long.parseLong(r0);	 Catch:{ NumberFormatException -> 0x0011 }
        goto L_0x002e;
    L_0x0011:
        r1 = "DefaultHttpDataSource";
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Unexpected Content-Length [";
        r2.append(r3);
        r2.append(r0);
        r3 = "]";
        r2.append(r3);
        r2 = r2.toString();
        android.util.Log.e(r1, r2);
    L_0x002c:
        r1 = -1;
    L_0x002e:
        r3 = "Content-Range";
        r10 = r10.getHeaderField(r3);
        r3 = android.text.TextUtils.isEmpty(r10);
        if (r3 != 0) goto L_0x00ae;
    L_0x003a:
        r3 = CONTENT_RANGE_HEADER;
        r3 = r3.matcher(r10);
        r4 = r3.find();
        if (r4 == 0) goto L_0x00ae;
    L_0x0046:
        r4 = 2;
        r4 = r3.group(r4);	 Catch:{ NumberFormatException -> 0x0093 }
        r4 = java.lang.Long.parseLong(r4);	 Catch:{ NumberFormatException -> 0x0093 }
        r6 = 1;	 Catch:{ NumberFormatException -> 0x0093 }
        r3 = r3.group(r6);	 Catch:{ NumberFormatException -> 0x0093 }
        r6 = java.lang.Long.parseLong(r3);	 Catch:{ NumberFormatException -> 0x0093 }
        r8 = r4 - r6;	 Catch:{ NumberFormatException -> 0x0093 }
        r3 = 1;	 Catch:{ NumberFormatException -> 0x0093 }
        r5 = r8 + r3;	 Catch:{ NumberFormatException -> 0x0093 }
        r3 = 0;	 Catch:{ NumberFormatException -> 0x0093 }
        r7 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));	 Catch:{ NumberFormatException -> 0x0093 }
        if (r7 >= 0) goto L_0x0066;	 Catch:{ NumberFormatException -> 0x0093 }
    L_0x0064:
        r1 = r5;	 Catch:{ NumberFormatException -> 0x0093 }
        goto L_0x00ae;	 Catch:{ NumberFormatException -> 0x0093 }
    L_0x0066:
        r3 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1));	 Catch:{ NumberFormatException -> 0x0093 }
        if (r3 == 0) goto L_0x00ae;	 Catch:{ NumberFormatException -> 0x0093 }
    L_0x006a:
        r3 = "DefaultHttpDataSource";	 Catch:{ NumberFormatException -> 0x0093 }
        r4 = new java.lang.StringBuilder;	 Catch:{ NumberFormatException -> 0x0093 }
        r4.<init>();	 Catch:{ NumberFormatException -> 0x0093 }
        r7 = "Inconsistent headers [";	 Catch:{ NumberFormatException -> 0x0093 }
        r4.append(r7);	 Catch:{ NumberFormatException -> 0x0093 }
        r4.append(r0);	 Catch:{ NumberFormatException -> 0x0093 }
        r0 = "] [";	 Catch:{ NumberFormatException -> 0x0093 }
        r4.append(r0);	 Catch:{ NumberFormatException -> 0x0093 }
        r4.append(r10);	 Catch:{ NumberFormatException -> 0x0093 }
        r0 = "]";	 Catch:{ NumberFormatException -> 0x0093 }
        r4.append(r0);	 Catch:{ NumberFormatException -> 0x0093 }
        r0 = r4.toString();	 Catch:{ NumberFormatException -> 0x0093 }
        android.util.Log.w(r3, r0);	 Catch:{ NumberFormatException -> 0x0093 }
        r3 = java.lang.Math.max(r1, r5);	 Catch:{ NumberFormatException -> 0x0093 }
        r1 = r3;
        goto L_0x00ae;
    L_0x0093:
        r0 = "DefaultHttpDataSource";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Unexpected Content-Range [";
        r3.append(r4);
        r3.append(r10);
        r10 = "]";
        r3.append(r10);
        r10 = r3.toString();
        android.util.Log.e(r0, r10);
    L_0x00ae:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.upstream.DefaultHttpDataSource.getContentLength(java.net.HttpURLConnection):long");
    }

    private void skipInternal() throws IOException {
        if (this.bytesSkipped != this.bytesToSkip) {
            Object obj = (byte[]) skipBufferReference.getAndSet(null);
            if (obj == null) {
                obj = new byte[4096];
            }
            while (this.bytesSkipped != this.bytesToSkip) {
                int read = this.inputStream.read(obj, 0, (int) Math.min(this.bytesToSkip - this.bytesSkipped, (long) obj.length));
                if (Thread.interrupted()) {
                    throw new InterruptedIOException();
                } else if (read == -1) {
                    throw new EOFException();
                } else {
                    this.bytesSkipped += (long) read;
                    if (this.listener != null) {
                        this.listener.onBytesTransferred(this, read);
                    }
                }
            }
            skipBufferReference.set(obj);
        }
    }

    private int readInternal(byte[] bArr, int i, int i2) throws IOException {
        if (i2 == 0) {
            return null;
        }
        if (this.bytesToRead != -1) {
            long j = this.bytesToRead - this.bytesRead;
            if (j == 0) {
                return -1;
            }
            i2 = (int) Math.min((long) i2, j);
        }
        bArr = this.inputStream.read(bArr, i, i2);
        if (bArr != -1) {
            this.bytesRead += (long) bArr;
            if (this.listener != 0) {
                this.listener.onBytesTransferred(this, bArr);
            }
            return bArr;
        } else if (this.bytesToRead == -1) {
            return -1;
        } else {
            throw new EOFException();
        }
    }

    private static void maybeTerminateInputStream(java.net.HttpURLConnection r3, long r4) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r0 = org.telegram.messenger.exoplayer2.util.Util.SDK_INT;
        r1 = 19;
        if (r0 == r1) goto L_0x000d;
    L_0x0006:
        r0 = org.telegram.messenger.exoplayer2.util.Util.SDK_INT;
        r1 = 20;
        if (r0 == r1) goto L_0x000d;
    L_0x000c:
        return;
    L_0x000d:
        r3 = r3.getInputStream();	 Catch:{ Exception -> 0x0058 }
        r0 = -1;	 Catch:{ Exception -> 0x0058 }
        r2 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1));	 Catch:{ Exception -> 0x0058 }
        if (r2 != 0) goto L_0x001f;	 Catch:{ Exception -> 0x0058 }
    L_0x0017:
        r4 = r3.read();	 Catch:{ Exception -> 0x0058 }
        r5 = -1;	 Catch:{ Exception -> 0x0058 }
        if (r4 != r5) goto L_0x0026;	 Catch:{ Exception -> 0x0058 }
    L_0x001e:
        return;	 Catch:{ Exception -> 0x0058 }
    L_0x001f:
        r0 = 2048; // 0x800 float:2.87E-42 double:1.0118E-320;	 Catch:{ Exception -> 0x0058 }
        r2 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1));	 Catch:{ Exception -> 0x0058 }
        if (r2 > 0) goto L_0x0026;	 Catch:{ Exception -> 0x0058 }
    L_0x0025:
        return;	 Catch:{ Exception -> 0x0058 }
    L_0x0026:
        r4 = r3.getClass();	 Catch:{ Exception -> 0x0058 }
        r4 = r4.getName();	 Catch:{ Exception -> 0x0058 }
        r5 = "com.android.okhttp.internal.http.HttpTransport$ChunkedInputStream";	 Catch:{ Exception -> 0x0058 }
        r5 = r4.equals(r5);	 Catch:{ Exception -> 0x0058 }
        if (r5 != 0) goto L_0x003e;	 Catch:{ Exception -> 0x0058 }
    L_0x0036:
        r5 = "com.android.okhttp.internal.http.HttpTransport$FixedLengthInputStream";	 Catch:{ Exception -> 0x0058 }
        r4 = r4.equals(r5);	 Catch:{ Exception -> 0x0058 }
        if (r4 == 0) goto L_0x0058;	 Catch:{ Exception -> 0x0058 }
    L_0x003e:
        r4 = r3.getClass();	 Catch:{ Exception -> 0x0058 }
        r4 = r4.getSuperclass();	 Catch:{ Exception -> 0x0058 }
        r5 = "unexpectedEndOfInput";	 Catch:{ Exception -> 0x0058 }
        r0 = 0;	 Catch:{ Exception -> 0x0058 }
        r1 = new java.lang.Class[r0];	 Catch:{ Exception -> 0x0058 }
        r4 = r4.getDeclaredMethod(r5, r1);	 Catch:{ Exception -> 0x0058 }
        r5 = 1;	 Catch:{ Exception -> 0x0058 }
        r4.setAccessible(r5);	 Catch:{ Exception -> 0x0058 }
        r5 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x0058 }
        r4.invoke(r3, r5);	 Catch:{ Exception -> 0x0058 }
    L_0x0058:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.upstream.DefaultHttpDataSource.maybeTerminateInputStream(java.net.HttpURLConnection, long):void");
    }

    private void closeConnectionQuietly() {
        if (this.connection != null) {
            try {
                this.connection.disconnect();
            } catch (Throwable e) {
                Log.e(TAG, "Unexpected error while disconnecting", e);
            }
            this.connection = null;
        }
    }
}

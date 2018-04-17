package org.telegram.messenger.exoplayer2.upstream;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.NoRouteToHostException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer2.upstream.HttpDataSource.HttpDataSourceException;
import org.telegram.messenger.exoplayer2.upstream.HttpDataSource.InvalidContentTypeException;
import org.telegram.messenger.exoplayer2.upstream.HttpDataSource.InvalidResponseCodeException;
import org.telegram.messenger.exoplayer2.upstream.HttpDataSource.RequestProperties;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Predicate;
import org.telegram.messenger.exoplayer2.util.Util;
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

    public DefaultHttpDataSource(String userAgent, Predicate<String> contentTypePredicate) {
        this(userAgent, contentTypePredicate, null);
    }

    public DefaultHttpDataSource(String userAgent, Predicate<String> contentTypePredicate, TransferListener<? super DefaultHttpDataSource> listener) {
        this(userAgent, contentTypePredicate, listener, 8000, 8000);
    }

    public DefaultHttpDataSource(String userAgent, Predicate<String> contentTypePredicate, TransferListener<? super DefaultHttpDataSource> listener, int connectTimeoutMillis, int readTimeoutMillis) {
        this(userAgent, contentTypePredicate, listener, connectTimeoutMillis, readTimeoutMillis, false, null);
    }

    public DefaultHttpDataSource(String userAgent, Predicate<String> contentTypePredicate, TransferListener<? super DefaultHttpDataSource> listener, int connectTimeoutMillis, int readTimeoutMillis, boolean allowCrossProtocolRedirects, RequestProperties defaultRequestProperties) {
        this.userAgent = Assertions.checkNotEmpty(userAgent);
        this.contentTypePredicate = contentTypePredicate;
        this.listener = listener;
        this.requestProperties = new RequestProperties();
        this.connectTimeoutMillis = connectTimeoutMillis;
        this.readTimeoutMillis = readTimeoutMillis;
        this.allowCrossProtocolRedirects = allowCrossProtocolRedirects;
        this.defaultRequestProperties = defaultRequestProperties;
    }

    public Uri getUri() {
        return this.connection == null ? null : Uri.parse(this.connection.getURL().toString());
    }

    public Map<String, List<String>> getResponseHeaders() {
        return this.connection == null ? null : this.connection.getHeaderFields();
    }

    public void setRequestProperty(String name, String value) {
        Assertions.checkNotNull(name);
        Assertions.checkNotNull(value);
        this.requestProperties.set(name, value);
    }

    public void clearRequestProperty(String name) {
        Assertions.checkNotNull(name);
        this.requestProperties.remove(name);
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
                Map<String, List<String>> headers = this.connection.getHeaderFields();
                closeConnectionQuietly();
                InvalidResponseCodeException exception = new InvalidResponseCodeException(responseCode, headers, dataSpec);
                if (responseCode == 416) {
                    exception.initCause(new DataSourceException(0));
                }
                throw exception;
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

    public int read(byte[] buffer, int offset, int readLength) throws HttpDataSourceException {
        try {
            skipInternal();
            return readInternal(buffer, offset, readLength);
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
        byte[] postBody = dataSpec2.postBody;
        long position = dataSpec2.position;
        long length = dataSpec2.length;
        boolean allowGzip = dataSpec2.isFlagSet(1);
        if (!this.allowCrossProtocolRedirects) {
            DefaultHttpDataSource defaultHttpDataSource;
            return makeConnection(url, postBody, position, length, allowGzip, true);
        }
        HttpURLConnection connection;
        int redirectCount = 0;
        while (true) {
            int redirectCount2 = redirectCount + 1;
            long position2;
            if (redirectCount <= 20) {
                long length2 = length;
                position2 = position;
                connection = makeConnection(url, postBody, position, length2, allowGzip, false);
                int responseCode = connection.getResponseCode();
                if (!(responseCode == 300 || responseCode == 301 || responseCode == 302 || responseCode == 303)) {
                    if (postBody == null) {
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
                postBody = null;
                String location = connection.getHeaderField("Location");
                connection.disconnect();
                url = handleRedirect(url, location);
                defaultHttpDataSource = this;
                redirectCount = redirectCount2;
                length = length2;
                position = position2;
            } else {
                position2 = position;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Too many redirects: ");
                stringBuilder.append(redirectCount2);
                throw new NoRouteToHostException(stringBuilder.toString());
            }
        }
        return connection;
    }

    private HttpURLConnection makeConnection(URL url, byte[] postBody, long position, long length, boolean allowGzip, boolean followRedirects) throws IOException {
        byte[] bArr = postBody;
        long j = position;
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(this.connectTimeoutMillis);
        connection.setReadTimeout(this.readTimeoutMillis);
        if (this.defaultRequestProperties != null) {
            for (Entry<String, String> property : r0.defaultRequestProperties.getSnapshot().entrySet()) {
                connection.setRequestProperty((String) property.getKey(), (String) property.getValue());
            }
        }
        for (Entry<String, String> property2 : r0.requestProperties.getSnapshot().entrySet()) {
            connection.setRequestProperty((String) property2.getKey(), (String) property2.getValue());
        }
        if (!(j == 0 && length == -1)) {
            String rangeRequest = new StringBuilder();
            rangeRequest.append("bytes=");
            rangeRequest.append(j);
            rangeRequest.append("-");
            rangeRequest = rangeRequest.toString();
            if (length != -1) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(rangeRequest);
                stringBuilder.append((j + length) - 1);
                rangeRequest = stringBuilder.toString();
            }
            connection.setRequestProperty("Range", rangeRequest);
        }
        connection.setRequestProperty("User-Agent", r0.userAgent);
        if (!allowGzip) {
            connection.setRequestProperty("Accept-Encoding", "identity");
        }
        connection.setInstanceFollowRedirects(followRedirects);
        connection.setDoOutput(bArr != null);
        if (bArr != null) {
            connection.setRequestMethod("POST");
            if (bArr.length == 0) {
                connection.connect();
            } else {
                connection.setFixedLengthStreamingMode(bArr.length);
                connection.connect();
                OutputStream os = connection.getOutputStream();
                os.write(bArr);
                os.close();
            }
        } else {
            connection.connect();
        }
        return connection;
    }

    private static URL handleRedirect(URL originalUrl, String location) throws IOException {
        if (location == null) {
            throw new ProtocolException("Null location redirect");
        }
        URL url = new URL(originalUrl, location);
        String protocol = url.getProtocol();
        if ("https".equals(protocol) || "http".equals(protocol)) {
            return url;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unsupported protocol redirect: ");
        stringBuilder.append(protocol);
        throw new ProtocolException(stringBuilder.toString());
    }

    private static long getContentLength(HttpURLConnection connection) {
        long contentLength = -1;
        String contentLengthHeader = connection.getHeaderField("Content-Length");
        if (!TextUtils.isEmpty(contentLengthHeader)) {
            try {
                contentLength = Long.parseLong(contentLengthHeader);
            } catch (NumberFormatException e) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unexpected Content-Length [");
                stringBuilder.append(contentLengthHeader);
                stringBuilder.append("]");
                Log.e(str, stringBuilder.toString());
            }
        }
        String contentRangeHeader = connection.getHeaderField("Content-Range");
        if (!TextUtils.isEmpty(contentRangeHeader)) {
            Matcher matcher = CONTENT_RANGE_HEADER.matcher(contentRangeHeader);
            if (matcher.find()) {
                try {
                    long contentLengthFromRange = (Long.parseLong(matcher.group(2)) - Long.parseLong(matcher.group(1))) + 1;
                    if (contentLength < 0) {
                        contentLength = contentLengthFromRange;
                    } else if (contentLength != contentLengthFromRange) {
                        String str2 = TAG;
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("Inconsistent headers [");
                        stringBuilder2.append(contentLengthHeader);
                        stringBuilder2.append("] [");
                        stringBuilder2.append(contentRangeHeader);
                        stringBuilder2.append("]");
                        Log.w(str2, stringBuilder2.toString());
                        contentLength = Math.max(contentLength, contentLengthFromRange);
                    }
                } catch (NumberFormatException e2) {
                    String str3 = TAG;
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("Unexpected Content-Range [");
                    stringBuilder3.append(contentRangeHeader);
                    stringBuilder3.append("]");
                    Log.e(str3, stringBuilder3.toString());
                }
            }
        }
        return contentLength;
    }

    private void skipInternal() throws IOException {
        if (this.bytesSkipped != this.bytesToSkip) {
            byte[] skipBuffer = (byte[]) skipBufferReference.getAndSet(null);
            if (skipBuffer == null) {
                skipBuffer = new byte[4096];
            }
            while (this.bytesSkipped != this.bytesToSkip) {
                int read = this.inputStream.read(skipBuffer, 0, (int) Math.min(this.bytesToSkip - this.bytesSkipped, (long) skipBuffer.length));
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
            skipBufferReference.set(skipBuffer);
        }
    }

    private int readInternal(byte[] buffer, int offset, int readLength) throws IOException {
        if (readLength == 0) {
            return 0;
        }
        if (this.bytesToRead != -1) {
            long bytesRemaining = this.bytesToRead - this.bytesRead;
            if (bytesRemaining == 0) {
                return -1;
            }
            readLength = (int) Math.min((long) readLength, bytesRemaining);
        }
        int read = this.inputStream.read(buffer, offset, readLength);
        if (read != -1) {
            this.bytesRead += (long) read;
            if (this.listener != null) {
                this.listener.onBytesTransferred(this, read);
            }
            return read;
        } else if (this.bytesToRead == -1) {
            return -1;
        } else {
            throw new EOFException();
        }
    }

    private static void maybeTerminateInputStream(HttpURLConnection connection, long bytesRemaining) {
        if (Util.SDK_INT == 19 || Util.SDK_INT == 20) {
            try {
                InputStream inputStream = connection.getInputStream();
                if (bytesRemaining == -1) {
                    if (inputStream.read() == -1) {
                        return;
                    }
                } else if (bytesRemaining <= MAX_BYTES_TO_DRAIN) {
                    return;
                }
                String className = inputStream.getClass().getName();
                if (className.equals("com.android.okhttp.internal.http.HttpTransport$ChunkedInputStream") || className.equals("com.android.okhttp.internal.http.HttpTransport$FixedLengthInputStream")) {
                    Method unexpectedEndOfInput = inputStream.getClass().getSuperclass().getDeclaredMethod("unexpectedEndOfInput", new Class[0]);
                    unexpectedEndOfInput.setAccessible(true);
                    unexpectedEndOfInput.invoke(inputStream, new Object[0]);
                }
            } catch (Exception e) {
            }
        }
    }

    private void closeConnectionQuietly() {
        if (this.connection != null) {
            try {
                this.connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Unexpected error while disconnecting", e);
            }
            this.connection = null;
        }
    }
}

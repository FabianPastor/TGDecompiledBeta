package org.telegram.messenger.exoplayer2.drm;

import android.annotation.TargetApi;
import android.net.Uri;
import android.text.TextUtils;
import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.telegram.messenger.exoplayer2.C0621C;
import org.telegram.messenger.exoplayer2.drm.ExoMediaDrm.KeyRequest;
import org.telegram.messenger.exoplayer2.drm.ExoMediaDrm.ProvisionRequest;
import org.telegram.messenger.exoplayer2.upstream.DataSourceInputStream;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.HttpDataSource;
import org.telegram.messenger.exoplayer2.upstream.HttpDataSource.Factory;
import org.telegram.messenger.exoplayer2.upstream.HttpDataSource.InvalidResponseCodeException;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

@TargetApi(18)
public final class HttpMediaDrmCallback implements MediaDrmCallback {
    private static final int MAX_MANUAL_REDIRECTS = 5;
    private final Factory dataSourceFactory;
    private final String defaultLicenseUrl;
    private final boolean forceDefaultLicenseUrl;
    private final Map<String, String> keyRequestProperties;

    public HttpMediaDrmCallback(String defaultLicenseUrl, Factory dataSourceFactory) {
        this(defaultLicenseUrl, false, dataSourceFactory);
    }

    public HttpMediaDrmCallback(String defaultLicenseUrl, boolean forceDefaultLicenseUrl, Factory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
        this.defaultLicenseUrl = defaultLicenseUrl;
        this.forceDefaultLicenseUrl = forceDefaultLicenseUrl;
        this.keyRequestProperties = new HashMap();
    }

    public void setKeyRequestProperty(String name, String value) {
        Assertions.checkNotNull(name);
        Assertions.checkNotNull(value);
        synchronized (this.keyRequestProperties) {
            this.keyRequestProperties.put(name, value);
        }
    }

    public void clearKeyRequestProperty(String name) {
        Assertions.checkNotNull(name);
        synchronized (this.keyRequestProperties) {
            this.keyRequestProperties.remove(name);
        }
    }

    public void clearAllKeyRequestProperties() {
        synchronized (this.keyRequestProperties) {
            this.keyRequestProperties.clear();
        }
    }

    public byte[] executeProvisionRequest(UUID uuid, ProvisionRequest request) throws IOException {
        return executePost(this.dataSourceFactory, request.getDefaultUrl() + "&signedRequest=" + new String(request.getData()), new byte[0], null);
    }

    public byte[] executeKeyRequest(UUID uuid, KeyRequest request) throws Exception {
        String url = request.getDefaultUrl();
        if (this.forceDefaultLicenseUrl || TextUtils.isEmpty(url)) {
            url = this.defaultLicenseUrl;
        }
        Map<String, String> requestProperties = new HashMap();
        String contentType = C0621C.PLAYREADY_UUID.equals(uuid) ? "text/xml" : C0621C.CLEARKEY_UUID.equals(uuid) ? "application/json" : "application/octet-stream";
        requestProperties.put("Content-Type", contentType);
        if (C0621C.PLAYREADY_UUID.equals(uuid)) {
            requestProperties.put("SOAPAction", "http://schemas.microsoft.com/DRM/2007/03/protocols/AcquireLicense");
        }
        synchronized (this.keyRequestProperties) {
            requestProperties.putAll(this.keyRequestProperties);
        }
        return executePost(this.dataSourceFactory, url, request.getData(), requestProperties);
    }

    private static byte[] executePost(Factory dataSourceFactory, String url, byte[] data, Map<String, String> requestProperties) throws IOException {
        Closeable inputStream;
        byte[] toByteArray;
        boolean manuallyRedirect;
        HttpDataSource dataSource = dataSourceFactory.createDataSource();
        if (requestProperties != null) {
            for (Entry<String, String> requestProperty : requestProperties.entrySet()) {
                dataSource.setRequestProperty((String) requestProperty.getKey(), (String) requestProperty.getValue());
            }
        }
        int manualRedirectCount = 0;
        while (true) {
            inputStream = new DataSourceInputStream(dataSource, new DataSpec(Uri.parse(url), data, 0, 0, -1, null, 1));
            try {
                toByteArray = Util.toByteArray(inputStream);
                break;
            } catch (InvalidResponseCodeException e) {
                if (e.responseCode == 307 || e.responseCode == 308) {
                    int manualRedirectCount2 = manualRedirectCount + 1;
                    if (manualRedirectCount < 5) {
                        manuallyRedirect = true;
                        manualRedirectCount = manualRedirectCount2;
                        url = manuallyRedirect ? getRedirectUrl(e) : null;
                        if (url != null) {
                            throw e;
                        }
                        Util.closeQuietly(inputStream);
                    } else {
                        manualRedirectCount = manualRedirectCount2;
                    }
                }
                manuallyRedirect = false;
                if (manuallyRedirect) {
                }
                if (url != null) {
                    Util.closeQuietly(inputStream);
                } else {
                    throw e;
                }
            } catch (Throwable th) {
                Util.closeQuietly(inputStream);
                throw th;
            }
        }
        Util.closeQuietly(inputStream);
        return toByteArray;
    }

    private static String getRedirectUrl(InvalidResponseCodeException exception) {
        Map<String, List<String>> headerFields = exception.headerFields;
        if (headerFields != null) {
            List<String> locationHeaders = (List) headerFields.get("Location");
            if (!(locationHeaders == null || locationHeaders.isEmpty())) {
                return (String) locationHeaders.get(0);
            }
        }
        return null;
    }
}

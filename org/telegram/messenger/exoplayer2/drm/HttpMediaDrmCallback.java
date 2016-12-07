package org.telegram.messenger.exoplayer2.drm;

import android.annotation.TargetApi;
import android.net.Uri;
import android.text.TextUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.drm.ExoMediaDrm.KeyRequest;
import org.telegram.messenger.exoplayer2.drm.ExoMediaDrm.ProvisionRequest;
import org.telegram.messenger.exoplayer2.upstream.DataSourceInputStream;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.HttpDataSource;
import org.telegram.messenger.exoplayer2.upstream.HttpDataSource.Factory;
import org.telegram.messenger.exoplayer2.util.Util;

@TargetApi(18)
public final class HttpMediaDrmCallback implements MediaDrmCallback {
    private static final Map<String, String> PLAYREADY_KEY_REQUEST_PROPERTIES = new HashMap();
    private final Factory dataSourceFactory;
    private final String defaultUrl;
    private final Map<String, String> keyRequestProperties;

    static {
        PLAYREADY_KEY_REQUEST_PROPERTIES.put("Content-Type", "text/xml");
        PLAYREADY_KEY_REQUEST_PROPERTIES.put("SOAPAction", "http://schemas.microsoft.com/DRM/2007/03/protocols/AcquireLicense");
    }

    public HttpMediaDrmCallback(String defaultUrl, Factory dataSourceFactory) {
        this(defaultUrl, dataSourceFactory, null);
    }

    public HttpMediaDrmCallback(String defaultUrl, Factory dataSourceFactory, Map<String, String> keyRequestProperties) {
        this.dataSourceFactory = dataSourceFactory;
        this.defaultUrl = defaultUrl;
        this.keyRequestProperties = keyRequestProperties;
    }

    public byte[] executeProvisionRequest(UUID uuid, ProvisionRequest request) throws IOException {
        return executePost(request.getDefaultUrl() + "&signedRequest=" + new String(request.getData()), new byte[0], null);
    }

    public byte[] executeKeyRequest(UUID uuid, KeyRequest request) throws Exception {
        String url = request.getDefaultUrl();
        if (TextUtils.isEmpty(url)) {
            url = this.defaultUrl;
        }
        Map<String, String> requestProperties = new HashMap();
        requestProperties.put("Content-Type", "application/octet-stream");
        if (C.PLAYREADY_UUID.equals(uuid)) {
            requestProperties.putAll(PLAYREADY_KEY_REQUEST_PROPERTIES);
        }
        if (this.keyRequestProperties != null) {
            requestProperties.putAll(this.keyRequestProperties);
        }
        return executePost(url, request.getData(), requestProperties);
    }

    private byte[] executePost(String url, byte[] data, Map<String, String> requestProperties) throws IOException {
        HttpDataSource dataSource = this.dataSourceFactory.createDataSource();
        if (requestProperties != null) {
            for (Entry<String, String> requestProperty : requestProperties.entrySet()) {
                dataSource.setRequestProperty((String) requestProperty.getKey(), (String) requestProperty.getValue());
            }
        }
        DataSourceInputStream inputStream = new DataSourceInputStream(dataSource, new DataSpec(Uri.parse(url), data, 0, 0, -1, null, 1));
        try {
            byte[] toByteArray = Util.toByteArray(inputStream);
            return toByteArray;
        } finally {
            inputStream.close();
        }
    }
}

package com.google.android.gms.internal;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;

public class zzaa implements zzz {
    private final zza zzaE;
    private final SSLSocketFactory zzaF;

    public interface zza {
        String zzh(String str);
    }

    public zzaa() {
        this(null);
    }

    public zzaa(zza com_google_android_gms_internal_zzaa_zza) {
        this(com_google_android_gms_internal_zzaa_zza, null);
    }

    public zzaa(zza com_google_android_gms_internal_zzaa_zza, SSLSocketFactory sSLSocketFactory) {
        this.zzaE = com_google_android_gms_internal_zzaa_zza;
        this.zzaF = sSLSocketFactory;
    }

    private HttpURLConnection zza(URL url, zzl<?> com_google_android_gms_internal_zzl_) throws IOException {
        HttpURLConnection zza = zza(url);
        int zzp = com_google_android_gms_internal_zzl_.zzp();
        zza.setConnectTimeout(zzp);
        zza.setReadTimeout(zzp);
        zza.setUseCaches(false);
        zza.setDoInput(true);
        if ("https".equals(url.getProtocol()) && this.zzaF != null) {
            ((HttpsURLConnection) zza).setSSLSocketFactory(this.zzaF);
        }
        return zza;
    }

    private static HttpEntity zza(HttpURLConnection httpURLConnection) {
        InputStream inputStream;
        HttpEntity basicHttpEntity = new BasicHttpEntity();
        try {
            inputStream = httpURLConnection.getInputStream();
        } catch (IOException e) {
            inputStream = httpURLConnection.getErrorStream();
        }
        basicHttpEntity.setContent(inputStream);
        basicHttpEntity.setContentLength((long) httpURLConnection.getContentLength());
        basicHttpEntity.setContentEncoding(httpURLConnection.getContentEncoding());
        basicHttpEntity.setContentType(httpURLConnection.getContentType());
        return basicHttpEntity;
    }

    static void zza(HttpURLConnection httpURLConnection, zzl<?> com_google_android_gms_internal_zzl_) throws IOException, zza {
        switch (com_google_android_gms_internal_zzl_.getMethod()) {
            case -1:
                byte[] zzj = com_google_android_gms_internal_zzl_.zzj();
                if (zzj != null) {
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.addRequestProperty("Content-Type", com_google_android_gms_internal_zzl_.zzi());
                    DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                    dataOutputStream.write(zzj);
                    dataOutputStream.close();
                    return;
                }
                return;
            case 0:
                httpURLConnection.setRequestMethod("GET");
                return;
            case 1:
                httpURLConnection.setRequestMethod("POST");
                zzb(httpURLConnection, com_google_android_gms_internal_zzl_);
                return;
            case 2:
                httpURLConnection.setRequestMethod("PUT");
                zzb(httpURLConnection, com_google_android_gms_internal_zzl_);
                return;
            case 3:
                httpURLConnection.setRequestMethod("DELETE");
                return;
            case 4:
                httpURLConnection.setRequestMethod("HEAD");
                return;
            case 5:
                httpURLConnection.setRequestMethod("OPTIONS");
                return;
            case 6:
                httpURLConnection.setRequestMethod("TRACE");
                return;
            case 7:
                httpURLConnection.setRequestMethod("PATCH");
                zzb(httpURLConnection, com_google_android_gms_internal_zzl_);
                return;
            default:
                throw new IllegalStateException("Unknown method type.");
        }
    }

    private static boolean zza(int i, int i2) {
        return (i == 4 || ((100 <= i2 && i2 < Callback.DEFAULT_DRAG_ANIMATION_DURATION) || i2 == 204 || i2 == 304)) ? false : true;
    }

    private static void zzb(HttpURLConnection httpURLConnection, zzl<?> com_google_android_gms_internal_zzl_) throws IOException, zza {
        byte[] zzm = com_google_android_gms_internal_zzl_.zzm();
        if (zzm != null) {
            httpURLConnection.setDoOutput(true);
            httpURLConnection.addRequestProperty("Content-Type", com_google_android_gms_internal_zzl_.zzl());
            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.write(zzm);
            dataOutputStream.close();
        }
    }

    protected HttpURLConnection zza(URL url) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setInstanceFollowRedirects(HttpURLConnection.getFollowRedirects());
        return httpURLConnection;
    }

    public HttpResponse zza(zzl<?> com_google_android_gms_internal_zzl_, Map<String, String> map) throws IOException, zza {
        String zzh;
        String url = com_google_android_gms_internal_zzl_.getUrl();
        HashMap hashMap = new HashMap();
        hashMap.putAll(com_google_android_gms_internal_zzl_.getHeaders());
        hashMap.putAll(map);
        if (this.zzaE != null) {
            zzh = this.zzaE.zzh(url);
            if (zzh == null) {
                throw new IOException("URL blocked by rewriter: " + url);
            }
        }
        zzh = url;
        HttpURLConnection zza = zza(new URL(zzh), (zzl) com_google_android_gms_internal_zzl_);
        for (String zzh2 : hashMap.keySet()) {
            zza.addRequestProperty(zzh2, (String) hashMap.get(zzh2));
        }
        zza(zza, (zzl) com_google_android_gms_internal_zzl_);
        ProtocolVersion protocolVersion = new ProtocolVersion("HTTP", 1, 1);
        if (zza.getResponseCode() == -1) {
            throw new IOException("Could not retrieve response code from HttpUrlConnection.");
        }
        StatusLine basicStatusLine = new BasicStatusLine(protocolVersion, zza.getResponseCode(), zza.getResponseMessage());
        HttpResponse basicHttpResponse = new BasicHttpResponse(basicStatusLine);
        if (zza(com_google_android_gms_internal_zzl_.getMethod(), basicStatusLine.getStatusCode())) {
            basicHttpResponse.setEntity(zza(zza));
        }
        for (Entry entry : zza.getHeaderFields().entrySet()) {
            if (entry.getKey() != null) {
                basicHttpResponse.addHeader(new BasicHeader((String) entry.getKey(), (String) ((List) entry.getValue()).get(0)));
            }
        }
        return basicHttpResponse;
    }
}

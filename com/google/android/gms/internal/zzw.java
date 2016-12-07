package com.google.android.gms.internal;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.volley.toolbox.HttpClientStack.HttpPatch;

public class zzw implements zzy {
    protected final HttpClient zzcd;

    public static final class zza extends HttpEntityEnclosingRequestBase {
        public zza(String str) {
            setURI(URI.create(str));
        }

        public String getMethod() {
            return HttpPatch.METHOD_NAME;
        }
    }

    public zzw(HttpClient httpClient) {
        this.zzcd = httpClient;
    }

    private static void zza(HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase, zzk<?> com_google_android_gms_internal_zzk_) throws zza {
        byte[] zzo = com_google_android_gms_internal_zzk_.zzo();
        if (zzo != null) {
            httpEntityEnclosingRequestBase.setEntity(new ByteArrayEntity(zzo));
        }
    }

    private static void zza(HttpUriRequest httpUriRequest, Map<String, String> map) {
        for (String str : map.keySet()) {
            httpUriRequest.setHeader(str, (String) map.get(str));
        }
    }

    static HttpUriRequest zzb(zzk<?> com_google_android_gms_internal_zzk_, Map<String, String> map) throws zza {
        HttpEntityEnclosingRequestBase httpPost;
        switch (com_google_android_gms_internal_zzk_.getMethod()) {
            case -1:
                byte[] zzl = com_google_android_gms_internal_zzk_.zzl();
                if (zzl == null) {
                    return new HttpGet(com_google_android_gms_internal_zzk_.getUrl());
                }
                HttpUriRequest httpPost2 = new HttpPost(com_google_android_gms_internal_zzk_.getUrl());
                httpPost2.addHeader("Content-Type", com_google_android_gms_internal_zzk_.zzk());
                httpPost2.setEntity(new ByteArrayEntity(zzl));
                return httpPost2;
            case 0:
                return new HttpGet(com_google_android_gms_internal_zzk_.getUrl());
            case 1:
                httpPost = new HttpPost(com_google_android_gms_internal_zzk_.getUrl());
                httpPost.addHeader("Content-Type", com_google_android_gms_internal_zzk_.zzn());
                zza(httpPost, (zzk) com_google_android_gms_internal_zzk_);
                return httpPost;
            case 2:
                httpPost = new HttpPut(com_google_android_gms_internal_zzk_.getUrl());
                httpPost.addHeader("Content-Type", com_google_android_gms_internal_zzk_.zzn());
                zza(httpPost, (zzk) com_google_android_gms_internal_zzk_);
                return httpPost;
            case 3:
                return new HttpDelete(com_google_android_gms_internal_zzk_.getUrl());
            case 4:
                return new HttpHead(com_google_android_gms_internal_zzk_.getUrl());
            case 5:
                return new HttpOptions(com_google_android_gms_internal_zzk_.getUrl());
            case 6:
                return new HttpTrace(com_google_android_gms_internal_zzk_.getUrl());
            case 7:
                httpPost = new zza(com_google_android_gms_internal_zzk_.getUrl());
                httpPost.addHeader("Content-Type", com_google_android_gms_internal_zzk_.zzn());
                zza(httpPost, (zzk) com_google_android_gms_internal_zzk_);
                return httpPost;
            default:
                throw new IllegalStateException("Unknown request method.");
        }
    }

    public HttpResponse zza(zzk<?> com_google_android_gms_internal_zzk_, Map<String, String> map) throws IOException, zza {
        HttpUriRequest zzb = zzb(com_google_android_gms_internal_zzk_, map);
        zza(zzb, (Map) map);
        zza(zzb, com_google_android_gms_internal_zzk_.getHeaders());
        zza(zzb);
        HttpParams params = zzb.getParams();
        int zzr = com_google_android_gms_internal_zzk_.zzr();
        HttpConnectionParams.setConnectionTimeout(params, DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
        HttpConnectionParams.setSoTimeout(params, zzr);
        return this.zzcd.execute(zzb);
    }

    protected void zza(HttpUriRequest httpUriRequest) throws IOException {
    }
}

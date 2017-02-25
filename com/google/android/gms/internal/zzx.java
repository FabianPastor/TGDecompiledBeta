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

public class zzx implements zzz {
    protected final HttpClient zzaD;

    public static final class zza extends HttpEntityEnclosingRequestBase {
        public zza(String str) {
            setURI(URI.create(str));
        }

        public String getMethod() {
            return "PATCH";
        }
    }

    public zzx(HttpClient httpClient) {
        this.zzaD = httpClient;
    }

    private static void zza(HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase, zzl<?> com_google_android_gms_internal_zzl_) throws zza {
        byte[] zzm = com_google_android_gms_internal_zzl_.zzm();
        if (zzm != null) {
            httpEntityEnclosingRequestBase.setEntity(new ByteArrayEntity(zzm));
        }
    }

    private static void zza(HttpUriRequest httpUriRequest, Map<String, String> map) {
        for (String str : map.keySet()) {
            httpUriRequest.setHeader(str, (String) map.get(str));
        }
    }

    static HttpUriRequest zzb(zzl<?> com_google_android_gms_internal_zzl_, Map<String, String> map) throws zza {
        HttpEntityEnclosingRequestBase httpPost;
        switch (com_google_android_gms_internal_zzl_.getMethod()) {
            case -1:
                byte[] zzj = com_google_android_gms_internal_zzl_.zzj();
                if (zzj == null) {
                    return new HttpGet(com_google_android_gms_internal_zzl_.getUrl());
                }
                HttpUriRequest httpPost2 = new HttpPost(com_google_android_gms_internal_zzl_.getUrl());
                httpPost2.addHeader("Content-Type", com_google_android_gms_internal_zzl_.zzi());
                httpPost2.setEntity(new ByteArrayEntity(zzj));
                return httpPost2;
            case 0:
                return new HttpGet(com_google_android_gms_internal_zzl_.getUrl());
            case 1:
                httpPost = new HttpPost(com_google_android_gms_internal_zzl_.getUrl());
                httpPost.addHeader("Content-Type", com_google_android_gms_internal_zzl_.zzl());
                zza(httpPost, (zzl) com_google_android_gms_internal_zzl_);
                return httpPost;
            case 2:
                httpPost = new HttpPut(com_google_android_gms_internal_zzl_.getUrl());
                httpPost.addHeader("Content-Type", com_google_android_gms_internal_zzl_.zzl());
                zza(httpPost, (zzl) com_google_android_gms_internal_zzl_);
                return httpPost;
            case 3:
                return new HttpDelete(com_google_android_gms_internal_zzl_.getUrl());
            case 4:
                return new HttpHead(com_google_android_gms_internal_zzl_.getUrl());
            case 5:
                return new HttpOptions(com_google_android_gms_internal_zzl_.getUrl());
            case 6:
                return new HttpTrace(com_google_android_gms_internal_zzl_.getUrl());
            case 7:
                httpPost = new zza(com_google_android_gms_internal_zzl_.getUrl());
                httpPost.addHeader("Content-Type", com_google_android_gms_internal_zzl_.zzl());
                zza(httpPost, (zzl) com_google_android_gms_internal_zzl_);
                return httpPost;
            default:
                throw new IllegalStateException("Unknown request method.");
        }
    }

    public HttpResponse zza(zzl<?> com_google_android_gms_internal_zzl_, Map<String, String> map) throws IOException, zza {
        HttpUriRequest zzb = zzb(com_google_android_gms_internal_zzl_, map);
        zza(zzb, (Map) map);
        zza(zzb, com_google_android_gms_internal_zzl_.getHeaders());
        HttpParams params = zzb.getParams();
        int zzp = com_google_android_gms_internal_zzl_.zzp();
        HttpConnectionParams.setConnectionTimeout(params, DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
        HttpConnectionParams.setSoTimeout(params, zzp);
        return this.zzaD.execute(zzb);
    }
}

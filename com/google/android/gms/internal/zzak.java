package com.google.android.gms.internal;

import java.io.IOException;
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

public final class zzak implements zzan {
    private HttpClient zzaA;

    public zzak(HttpClient httpClient) {
        this.zzaA = httpClient;
    }

    private static void zza(HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase, zzp<?> com_google_android_gms_internal_zzp_) throws zza {
        byte[] zzg = com_google_android_gms_internal_zzp_.zzg();
        if (zzg != null) {
            httpEntityEnclosingRequestBase.setEntity(new ByteArrayEntity(zzg));
        }
    }

    private static void zza(HttpUriRequest httpUriRequest, Map<String, String> map) {
        for (String str : map.keySet()) {
            httpUriRequest.setHeader(str, (String) map.get(str));
        }
    }

    public final HttpResponse zza(zzp<?> com_google_android_gms_internal_zzp_, Map<String, String> map) throws IOException, zza {
        HttpUriRequest httpGet;
        HttpEntityEnclosingRequestBase httpPost;
        switch (com_google_android_gms_internal_zzp_.getMethod()) {
            case -1:
                httpGet = new HttpGet(com_google_android_gms_internal_zzp_.getUrl());
                break;
            case 0:
                httpGet = new HttpGet(com_google_android_gms_internal_zzp_.getUrl());
                break;
            case 1:
                httpPost = new HttpPost(com_google_android_gms_internal_zzp_.getUrl());
                httpPost.addHeader("Content-Type", zzp.zzf());
                zza(httpPost, (zzp) com_google_android_gms_internal_zzp_);
                break;
            case 2:
                httpPost = new HttpPut(com_google_android_gms_internal_zzp_.getUrl());
                httpPost.addHeader("Content-Type", zzp.zzf());
                zza(httpPost, (zzp) com_google_android_gms_internal_zzp_);
                break;
            case 3:
                httpGet = new HttpDelete(com_google_android_gms_internal_zzp_.getUrl());
                break;
            case 4:
                httpGet = new HttpHead(com_google_android_gms_internal_zzp_.getUrl());
                break;
            case 5:
                httpGet = new HttpOptions(com_google_android_gms_internal_zzp_.getUrl());
                break;
            case 6:
                httpGet = new HttpTrace(com_google_android_gms_internal_zzp_.getUrl());
                break;
            case 7:
                httpPost = new zzal(com_google_android_gms_internal_zzp_.getUrl());
                httpPost.addHeader("Content-Type", zzp.zzf());
                zza(httpPost, (zzp) com_google_android_gms_internal_zzp_);
                break;
            default:
                throw new IllegalStateException("Unknown request method.");
        }
        zza(httpGet, (Map) map);
        zza(httpGet, com_google_android_gms_internal_zzp_.getHeaders());
        HttpParams params = httpGet.getParams();
        int zzi = com_google_android_gms_internal_zzp_.zzi();
        HttpConnectionParams.setConnectionTimeout(params, DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
        HttpConnectionParams.setSoTimeout(params, zzi);
        return this.zzaA.execute(httpGet);
    }
}

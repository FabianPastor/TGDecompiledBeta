package com.google.android.gms.wearable.internal;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.internal.zzj;
import com.google.android.gms.common.internal.zzr;
import com.google.android.gms.common.zzf;
import com.google.android.gms.wearable.CapabilityApi.CapabilityListener;
import com.google.android.gms.wearable.ChannelApi.ChannelListener;
import com.google.android.gms.wearable.DataApi.DataListener;
import com.google.android.gms.wearable.MessageApi.MessageListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.telegram.messenger.exoplayer2.C;

public final class zzhg extends zzab<zzep> {
    private final ExecutorService zzieo;
    private final zzer<Object> zzllh;
    private final zzer<Object> zzlli;
    private final zzer<ChannelListener> zzllj;
    private final zzer<DataListener> zzllk;
    private final zzer<MessageListener> zzlll;
    private final zzer<Object> zzllm;
    private final zzer<Object> zzlln;
    private final zzer<CapabilityListener> zzllo;
    private final zzhp zzllp;

    public zzhg(Context context, Looper looper, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener, zzr com_google_android_gms_common_internal_zzr) {
        this(context, looper, connectionCallbacks, onConnectionFailedListener, com_google_android_gms_common_internal_zzr, Executors.newCachedThreadPool(), zzhp.zzep(context));
    }

    private zzhg(Context context, Looper looper, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener, zzr com_google_android_gms_common_internal_zzr, ExecutorService executorService, zzhp com_google_android_gms_wearable_internal_zzhp) {
        super(context, looper, 14, com_google_android_gms_common_internal_zzr, connectionCallbacks, onConnectionFailedListener);
        this.zzllh = new zzer();
        this.zzlli = new zzer();
        this.zzllj = new zzer();
        this.zzllk = new zzer();
        this.zzlll = new zzer();
        this.zzllm = new zzer();
        this.zzlln = new zzer();
        this.zzllo = new zzer();
        this.zzieo = (ExecutorService) zzbq.checkNotNull(executorService);
        this.zzllp = com_google_android_gms_wearable_internal_zzhp;
    }

    protected final void zza(int i, IBinder iBinder, Bundle bundle, int i2) {
        if (Log.isLoggable("WearableClient", 2)) {
            Log.d("WearableClient", "onPostInitHandler: statusCode " + i);
        }
        if (i == 0) {
            this.zzllh.zzbr(iBinder);
            this.zzlli.zzbr(iBinder);
            this.zzllj.zzbr(iBinder);
            this.zzllk.zzbr(iBinder);
            this.zzlll.zzbr(iBinder);
            this.zzllm.zzbr(iBinder);
            this.zzlln.zzbr(iBinder);
            this.zzllo.zzbr(iBinder);
        }
        super.zza(i, iBinder, bundle, i2);
    }

    public final void zza(zzj com_google_android_gms_common_internal_zzj) {
        int i = 0;
        if (!zzagg()) {
            try {
                Bundle bundle = getContext().getPackageManager().getApplicationInfo("com.google.android.wearable.app.cn", 128).metaData;
                if (bundle != null) {
                    i = bundle.getInt("com.google.android.wearable.api.version", 0);
                }
                if (i < zzf.GOOGLE_PLAY_SERVICES_VERSION_CODE) {
                    Log.w("WearableClient", "Android Wear out of date. Requires API version " + zzf.GOOGLE_PLAY_SERVICES_VERSION_CODE + " but found " + i);
                    Context context = getContext();
                    Context context2 = getContext();
                    Intent intent = new Intent("com.google.android.wearable.app.cn.UPDATE_ANDROID_WEAR").setPackage("com.google.android.wearable.app.cn");
                    if (context2.getPackageManager().resolveActivity(intent, C.DEFAULT_BUFFER_SEGMENT_SIZE) == null) {
                        intent = new Intent("android.intent.action.VIEW", Uri.parse("market://details").buildUpon().appendQueryParameter(TtmlNode.ATTR_ID, "com.google.android.wearable.app.cn").build());
                    }
                    zza(com_google_android_gms_common_internal_zzj, 6, PendingIntent.getActivity(context, 0, intent, 0));
                    return;
                }
            } catch (NameNotFoundException e) {
                zza(com_google_android_gms_common_internal_zzj, 16, null);
                return;
            }
        }
        super.zza(com_google_android_gms_common_internal_zzj);
    }

    public final boolean zzagg() {
        return !this.zzllp.zznz("com.google.android.wearable.app.cn");
    }

    protected final String zzakh() {
        return this.zzllp.zznz("com.google.android.wearable.app.cn") ? "com.google.android.wearable.app.cn" : "com.google.android.gms";
    }

    protected final /* synthetic */ IInterface zzd(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.wearable.internal.IWearableService");
        return queryLocalInterface instanceof zzep ? (zzep) queryLocalInterface : new zzeq(iBinder);
    }

    protected final String zzhi() {
        return "com.google.android.gms.wearable.BIND";
    }

    protected final String zzhj() {
        return "com.google.android.gms.wearable.internal.IWearableService";
    }
}

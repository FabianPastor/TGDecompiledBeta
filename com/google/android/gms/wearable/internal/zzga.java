package com.google.android.gms.wearable.internal;

import android.content.IntentFilter;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.internal.zzbdw;
import com.google.android.gms.wearable.CapabilityApi.CapabilityListener;
import com.google.android.gms.wearable.ChannelApi.ChannelListener;
import com.google.android.gms.wearable.DataApi.DataListener;
import com.google.android.gms.wearable.MessageApi.MessageListener;
import com.google.android.gms.wearable.NodeApi.NodeListener;
import java.util.List;

public final class zzga<T> extends zzdl {
    private final IntentFilter[] zzbSW;
    private final String zzbTA;
    private zzbdw<Object> zzbTs;
    private zzbdw<Object> zzbTt;
    private zzbdw<DataListener> zzbTu;
    private zzbdw<MessageListener> zzbTv;
    private zzbdw<NodeListener> zzbTw;
    private zzbdw<Object> zzbTx;
    private zzbdw<ChannelListener> zzbTy;
    private zzbdw<CapabilityListener> zzbTz;

    private zzga(IntentFilter[] intentFilterArr, String str) {
        this.zzbSW = (IntentFilter[]) zzbo.zzu(intentFilterArr);
        this.zzbTA = str;
    }

    public static zzga<ChannelListener> zza(zzbdw<ChannelListener> com_google_android_gms_internal_zzbdw_com_google_android_gms_wearable_ChannelApi_ChannelListener, String str, IntentFilter[] intentFilterArr) {
        zzga<ChannelListener> com_google_android_gms_wearable_internal_zzga = new zzga(intentFilterArr, (String) zzbo.zzu(str));
        com_google_android_gms_wearable_internal_zzga.zzbTy = (zzbdw) zzbo.zzu(com_google_android_gms_internal_zzbdw_com_google_android_gms_wearable_ChannelApi_ChannelListener);
        return com_google_android_gms_wearable_internal_zzga;
    }

    public static zzga<DataListener> zza(zzbdw<DataListener> com_google_android_gms_internal_zzbdw_com_google_android_gms_wearable_DataApi_DataListener, IntentFilter[] intentFilterArr) {
        zzga<DataListener> com_google_android_gms_wearable_internal_zzga = new zzga(intentFilterArr, null);
        com_google_android_gms_wearable_internal_zzga.zzbTu = (zzbdw) zzbo.zzu(com_google_android_gms_internal_zzbdw_com_google_android_gms_wearable_DataApi_DataListener);
        return com_google_android_gms_wearable_internal_zzga;
    }

    public static zzga<MessageListener> zzb(zzbdw<MessageListener> com_google_android_gms_internal_zzbdw_com_google_android_gms_wearable_MessageApi_MessageListener, IntentFilter[] intentFilterArr) {
        zzga<MessageListener> com_google_android_gms_wearable_internal_zzga = new zzga(intentFilterArr, null);
        com_google_android_gms_wearable_internal_zzga.zzbTv = (zzbdw) zzbo.zzu(com_google_android_gms_internal_zzbdw_com_google_android_gms_wearable_MessageApi_MessageListener);
        return com_google_android_gms_wearable_internal_zzga;
    }

    public static zzga<NodeListener> zzc(zzbdw<NodeListener> com_google_android_gms_internal_zzbdw_com_google_android_gms_wearable_NodeApi_NodeListener, IntentFilter[] intentFilterArr) {
        zzga<NodeListener> com_google_android_gms_wearable_internal_zzga = new zzga(intentFilterArr, null);
        com_google_android_gms_wearable_internal_zzga.zzbTw = (zzbdw) zzbo.zzu(com_google_android_gms_internal_zzbdw_com_google_android_gms_wearable_NodeApi_NodeListener);
        return com_google_android_gms_wearable_internal_zzga;
    }

    public static zzga<ChannelListener> zzd(zzbdw<ChannelListener> com_google_android_gms_internal_zzbdw_com_google_android_gms_wearable_ChannelApi_ChannelListener, IntentFilter[] intentFilterArr) {
        zzga<ChannelListener> com_google_android_gms_wearable_internal_zzga = new zzga(intentFilterArr, null);
        com_google_android_gms_wearable_internal_zzga.zzbTy = (zzbdw) zzbo.zzu(com_google_android_gms_internal_zzbdw_com_google_android_gms_wearable_ChannelApi_ChannelListener);
        return com_google_android_gms_wearable_internal_zzga;
    }

    public static zzga<CapabilityListener> zze(zzbdw<CapabilityListener> com_google_android_gms_internal_zzbdw_com_google_android_gms_wearable_CapabilityApi_CapabilityListener, IntentFilter[] intentFilterArr) {
        zzga<CapabilityListener> com_google_android_gms_wearable_internal_zzga = new zzga(intentFilterArr, null);
        com_google_android_gms_wearable_internal_zzga.zzbTz = (zzbdw) zzbo.zzu(com_google_android_gms_internal_zzbdw_com_google_android_gms_wearable_CapabilityApi_CapabilityListener);
        return com_google_android_gms_wearable_internal_zzga;
    }

    private static void zzk(zzbdw<?> com_google_android_gms_internal_zzbdw_) {
        if (com_google_android_gms_internal_zzbdw_ != null) {
            com_google_android_gms_internal_zzbdw_.clear();
        }
    }

    public final void clear() {
        zzk(null);
        this.zzbTs = null;
        zzk(null);
        this.zzbTt = null;
        zzk(this.zzbTu);
        this.zzbTu = null;
        zzk(this.zzbTv);
        this.zzbTv = null;
        zzk(this.zzbTw);
        this.zzbTw = null;
        zzk(null);
        this.zzbTx = null;
        zzk(this.zzbTy);
        this.zzbTy = null;
        zzk(this.zzbTz);
        this.zzbTz = null;
    }

    public final void onConnectedNodes(List<zzeg> list) {
    }

    public final IntentFilter[] zzDY() {
        return this.zzbSW;
    }

    public final String zzDZ() {
        return this.zzbTA;
    }

    public final void zzS(DataHolder dataHolder) {
        if (this.zzbTu != null) {
            this.zzbTu.zza(new zzgb(dataHolder));
        } else {
            dataHolder.close();
        }
    }

    public final void zza(zzaa com_google_android_gms_wearable_internal_zzaa) {
        if (this.zzbTz != null) {
            this.zzbTz.zza(new zzgg(com_google_android_gms_wearable_internal_zzaa));
        }
    }

    public final void zza(zzai com_google_android_gms_wearable_internal_zzai) {
        if (this.zzbTy != null) {
            this.zzbTy.zza(new zzgf(com_google_android_gms_wearable_internal_zzai));
        }
    }

    public final void zza(zzdx com_google_android_gms_wearable_internal_zzdx) {
        if (this.zzbTv != null) {
            this.zzbTv.zza(new zzgc(com_google_android_gms_wearable_internal_zzdx));
        }
    }

    public final void zza(zzeg com_google_android_gms_wearable_internal_zzeg) {
        if (this.zzbTw != null) {
            this.zzbTw.zza(new zzgd(com_google_android_gms_wearable_internal_zzeg));
        }
    }

    public final void zza(zzi com_google_android_gms_wearable_internal_zzi) {
    }

    public final void zza(zzl com_google_android_gms_wearable_internal_zzl) {
    }

    public final void zzb(zzeg com_google_android_gms_wearable_internal_zzeg) {
        if (this.zzbTw != null) {
            this.zzbTw.zza(new zzge(com_google_android_gms_wearable_internal_zzeg));
        }
    }
}

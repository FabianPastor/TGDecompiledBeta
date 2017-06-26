package com.google.android.gms.wearable.internal;

import android.content.IntentFilter;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.internal.zzbdv;
import com.google.android.gms.wearable.CapabilityApi.CapabilityListener;
import com.google.android.gms.wearable.ChannelApi.ChannelListener;
import com.google.android.gms.wearable.DataApi.DataListener;
import com.google.android.gms.wearable.MessageApi.MessageListener;
import com.google.android.gms.wearable.NodeApi.NodeListener;
import java.util.List;

public final class zzga<T> extends zzdl {
    private final IntentFilter[] zzbSU;
    private zzbdv<Object> zzbTq;
    private zzbdv<Object> zzbTr;
    private zzbdv<DataListener> zzbTs;
    private zzbdv<MessageListener> zzbTt;
    private zzbdv<NodeListener> zzbTu;
    private zzbdv<Object> zzbTv;
    private zzbdv<ChannelListener> zzbTw;
    private zzbdv<CapabilityListener> zzbTx;
    private final String zzbTy;

    private zzga(IntentFilter[] intentFilterArr, String str) {
        this.zzbSU = (IntentFilter[]) zzbo.zzu(intentFilterArr);
        this.zzbTy = str;
    }

    public static zzga<ChannelListener> zza(zzbdv<ChannelListener> com_google_android_gms_internal_zzbdv_com_google_android_gms_wearable_ChannelApi_ChannelListener, String str, IntentFilter[] intentFilterArr) {
        zzga<ChannelListener> com_google_android_gms_wearable_internal_zzga = new zzga(intentFilterArr, (String) zzbo.zzu(str));
        com_google_android_gms_wearable_internal_zzga.zzbTw = (zzbdv) zzbo.zzu(com_google_android_gms_internal_zzbdv_com_google_android_gms_wearable_ChannelApi_ChannelListener);
        return com_google_android_gms_wearable_internal_zzga;
    }

    public static zzga<DataListener> zza(zzbdv<DataListener> com_google_android_gms_internal_zzbdv_com_google_android_gms_wearable_DataApi_DataListener, IntentFilter[] intentFilterArr) {
        zzga<DataListener> com_google_android_gms_wearable_internal_zzga = new zzga(intentFilterArr, null);
        com_google_android_gms_wearable_internal_zzga.zzbTs = (zzbdv) zzbo.zzu(com_google_android_gms_internal_zzbdv_com_google_android_gms_wearable_DataApi_DataListener);
        return com_google_android_gms_wearable_internal_zzga;
    }

    public static zzga<MessageListener> zzb(zzbdv<MessageListener> com_google_android_gms_internal_zzbdv_com_google_android_gms_wearable_MessageApi_MessageListener, IntentFilter[] intentFilterArr) {
        zzga<MessageListener> com_google_android_gms_wearable_internal_zzga = new zzga(intentFilterArr, null);
        com_google_android_gms_wearable_internal_zzga.zzbTt = (zzbdv) zzbo.zzu(com_google_android_gms_internal_zzbdv_com_google_android_gms_wearable_MessageApi_MessageListener);
        return com_google_android_gms_wearable_internal_zzga;
    }

    public static zzga<NodeListener> zzc(zzbdv<NodeListener> com_google_android_gms_internal_zzbdv_com_google_android_gms_wearable_NodeApi_NodeListener, IntentFilter[] intentFilterArr) {
        zzga<NodeListener> com_google_android_gms_wearable_internal_zzga = new zzga(intentFilterArr, null);
        com_google_android_gms_wearable_internal_zzga.zzbTu = (zzbdv) zzbo.zzu(com_google_android_gms_internal_zzbdv_com_google_android_gms_wearable_NodeApi_NodeListener);
        return com_google_android_gms_wearable_internal_zzga;
    }

    public static zzga<ChannelListener> zzd(zzbdv<ChannelListener> com_google_android_gms_internal_zzbdv_com_google_android_gms_wearable_ChannelApi_ChannelListener, IntentFilter[] intentFilterArr) {
        zzga<ChannelListener> com_google_android_gms_wearable_internal_zzga = new zzga(intentFilterArr, null);
        com_google_android_gms_wearable_internal_zzga.zzbTw = (zzbdv) zzbo.zzu(com_google_android_gms_internal_zzbdv_com_google_android_gms_wearable_ChannelApi_ChannelListener);
        return com_google_android_gms_wearable_internal_zzga;
    }

    public static zzga<CapabilityListener> zze(zzbdv<CapabilityListener> com_google_android_gms_internal_zzbdv_com_google_android_gms_wearable_CapabilityApi_CapabilityListener, IntentFilter[] intentFilterArr) {
        zzga<CapabilityListener> com_google_android_gms_wearable_internal_zzga = new zzga(intentFilterArr, null);
        com_google_android_gms_wearable_internal_zzga.zzbTx = (zzbdv) zzbo.zzu(com_google_android_gms_internal_zzbdv_com_google_android_gms_wearable_CapabilityApi_CapabilityListener);
        return com_google_android_gms_wearable_internal_zzga;
    }

    private static void zzk(zzbdv<?> com_google_android_gms_internal_zzbdv_) {
        if (com_google_android_gms_internal_zzbdv_ != null) {
            com_google_android_gms_internal_zzbdv_.clear();
        }
    }

    public final void clear() {
        zzk(null);
        this.zzbTq = null;
        zzk(null);
        this.zzbTr = null;
        zzk(this.zzbTs);
        this.zzbTs = null;
        zzk(this.zzbTt);
        this.zzbTt = null;
        zzk(this.zzbTu);
        this.zzbTu = null;
        zzk(null);
        this.zzbTv = null;
        zzk(this.zzbTw);
        this.zzbTw = null;
        zzk(this.zzbTx);
        this.zzbTx = null;
    }

    public final void onConnectedNodes(List<zzeg> list) {
    }

    public final IntentFilter[] zzDX() {
        return this.zzbSU;
    }

    public final String zzDY() {
        return this.zzbTy;
    }

    public final void zzS(DataHolder dataHolder) {
        if (this.zzbTs != null) {
            this.zzbTs.zza(new zzgb(dataHolder));
        } else {
            dataHolder.close();
        }
    }

    public final void zza(zzaa com_google_android_gms_wearable_internal_zzaa) {
        if (this.zzbTx != null) {
            this.zzbTx.zza(new zzgg(com_google_android_gms_wearable_internal_zzaa));
        }
    }

    public final void zza(zzai com_google_android_gms_wearable_internal_zzai) {
        if (this.zzbTw != null) {
            this.zzbTw.zza(new zzgf(com_google_android_gms_wearable_internal_zzai));
        }
    }

    public final void zza(zzdx com_google_android_gms_wearable_internal_zzdx) {
        if (this.zzbTt != null) {
            this.zzbTt.zza(new zzgc(com_google_android_gms_wearable_internal_zzdx));
        }
    }

    public final void zza(zzeg com_google_android_gms_wearable_internal_zzeg) {
        if (this.zzbTu != null) {
            this.zzbTu.zza(new zzgd(com_google_android_gms_wearable_internal_zzeg));
        }
    }

    public final void zza(zzi com_google_android_gms_wearable_internal_zzi) {
    }

    public final void zza(zzl com_google_android_gms_wearable_internal_zzl) {
    }

    public final void zzb(zzeg com_google_android_gms_wearable_internal_zzeg) {
        if (this.zzbTu != null) {
            this.zzbTu.zza(new zzge(com_google_android_gms_wearable_internal_zzeg));
        }
    }
}

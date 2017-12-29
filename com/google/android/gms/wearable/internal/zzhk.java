package com.google.android.gms.wearable.internal;

import android.content.IntentFilter;
import com.google.android.gms.common.api.internal.zzci;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.wearable.CapabilityApi.CapabilityListener;
import com.google.android.gms.wearable.ChannelApi.ChannelListener;
import com.google.android.gms.wearable.DataApi.DataListener;
import com.google.android.gms.wearable.MessageApi.MessageListener;
import java.util.List;

public final class zzhk<T> extends zzen {
    private final IntentFilter[] zzlkt;
    private zzci<DataListener> zzllv;
    private zzci<MessageListener> zzllw;
    private zzci<ChannelListener> zzllz;
    private zzci<CapabilityListener> zzlma;
    private final String zzlmb;

    public final void onConnectedNodes(List<zzfo> list) {
    }

    public final void zza(zzah com_google_android_gms_wearable_internal_zzah) {
        if (this.zzlma != null) {
            this.zzlma.zza(new zzho(com_google_android_gms_wearable_internal_zzah));
        }
    }

    public final void zza(zzaw com_google_android_gms_wearable_internal_zzaw) {
        if (this.zzllz != null) {
            this.zzllz.zza(new zzhn(com_google_android_gms_wearable_internal_zzaw));
        }
    }

    public final void zza(zzfe com_google_android_gms_wearable_internal_zzfe) {
        if (this.zzllw != null) {
            this.zzllw.zza(new zzhm(com_google_android_gms_wearable_internal_zzfe));
        }
    }

    public final void zza(zzfo com_google_android_gms_wearable_internal_zzfo) {
    }

    public final void zza(zzi com_google_android_gms_wearable_internal_zzi) {
    }

    public final void zza(zzl com_google_android_gms_wearable_internal_zzl) {
    }

    public final void zzas(DataHolder dataHolder) {
        if (this.zzllv != null) {
            this.zzllv.zza(new zzhl(dataHolder));
        } else {
            dataHolder.close();
        }
    }

    public final void zzb(zzfo com_google_android_gms_wearable_internal_zzfo) {
    }

    public final IntentFilter[] zzbkg() {
        return this.zzlkt;
    }

    public final String zzbkh() {
        return this.zzlmb;
    }
}

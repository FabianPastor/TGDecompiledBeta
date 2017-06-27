package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.Log;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.wearable.ChannelApi.ChannelListener;

public final class zzai extends zza {
    public static final Creator<zzai> CREATOR = new zzaj();
    private int type;
    private int zzbSf;
    private int zzbSg;
    private zzak zzbSh;

    public zzai(zzak com_google_android_gms_wearable_internal_zzak, int i, int i2, int i3) {
        this.zzbSh = com_google_android_gms_wearable_internal_zzak;
        this.type = i;
        this.zzbSf = i2;
        this.zzbSg = i3;
    }

    public final String toString() {
        Object obj;
        String valueOf = String.valueOf(this.zzbSh);
        int i = this.type;
        switch (i) {
            case 1:
                obj = "CHANNEL_OPENED";
                break;
            case 2:
                obj = "CHANNEL_CLOSED";
                break;
            case 3:
                obj = "INPUT_CLOSED";
                break;
            case 4:
                obj = "OUTPUT_CLOSED";
                break;
            default:
                obj = Integer.toString(i);
                break;
        }
        String valueOf2 = String.valueOf(obj);
        i = this.zzbSf;
        switch (i) {
            case 0:
                obj = "CLOSE_REASON_NORMAL";
                break;
            case 1:
                obj = "CLOSE_REASON_DISCONNECTED";
                break;
            case 2:
                obj = "CLOSE_REASON_REMOTE_CLOSE";
                break;
            case 3:
                obj = "CLOSE_REASON_LOCAL_CLOSE";
                break;
            default:
                obj = Integer.toString(i);
                break;
        }
        String valueOf3 = String.valueOf(obj);
        return new StringBuilder(((String.valueOf(valueOf).length() + 81) + String.valueOf(valueOf2).length()) + String.valueOf(valueOf3).length()).append("ChannelEventParcelable[, channel=").append(valueOf).append(", type=").append(valueOf2).append(", closeReason=").append(valueOf3).append(", appErrorCode=").append(this.zzbSg).append("]").toString();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbSh, i, false);
        zzd.zzc(parcel, 3, this.type);
        zzd.zzc(parcel, 4, this.zzbSf);
        zzd.zzc(parcel, 5, this.zzbSg);
        zzd.zzI(parcel, zze);
    }

    public final void zza(ChannelListener channelListener) {
        switch (this.type) {
            case 1:
                channelListener.onChannelOpened(this.zzbSh);
                return;
            case 2:
                channelListener.onChannelClosed(this.zzbSh, this.zzbSf, this.zzbSg);
                return;
            case 3:
                channelListener.onInputClosed(this.zzbSh, this.zzbSf, this.zzbSg);
                return;
            case 4:
                channelListener.onOutputClosed(this.zzbSh, this.zzbSf, this.zzbSg);
                return;
            default:
                Log.w("ChannelEventParcelable", "Unknown type: " + this.type);
                return;
        }
    }
}
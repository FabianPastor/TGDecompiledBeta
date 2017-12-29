package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.Log;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;
import com.google.android.gms.wearable.ChannelApi.ChannelListener;

public final class zzaw extends zzbfm {
    public static final Creator<zzaw> CREATOR = new zzax();
    private int type;
    private int zzljc;
    private int zzljd;
    private zzay zzlje;

    public zzaw(zzay com_google_android_gms_wearable_internal_zzay, int i, int i2, int i3) {
        this.zzlje = com_google_android_gms_wearable_internal_zzay;
        this.type = i;
        this.zzljc = i2;
        this.zzljd = i3;
    }

    public final String toString() {
        String str;
        String str2;
        String valueOf = String.valueOf(this.zzlje);
        int i = this.type;
        switch (i) {
            case 1:
                str = "CHANNEL_OPENED";
                break;
            case 2:
                str = "CHANNEL_CLOSED";
                break;
            case 3:
                str = "INPUT_CLOSED";
                break;
            case 4:
                str = "OUTPUT_CLOSED";
                break;
            default:
                str = Integer.toString(i);
                break;
        }
        int i2 = this.zzljc;
        switch (i2) {
            case 0:
                str2 = "CLOSE_REASON_NORMAL";
                break;
            case 1:
                str2 = "CLOSE_REASON_DISCONNECTED";
                break;
            case 2:
                str2 = "CLOSE_REASON_REMOTE_CLOSE";
                break;
            case 3:
                str2 = "CLOSE_REASON_LOCAL_CLOSE";
                break;
            default:
                str2 = Integer.toString(i2);
                break;
        }
        return new StringBuilder(((String.valueOf(valueOf).length() + 81) + String.valueOf(str).length()) + String.valueOf(str2).length()).append("ChannelEventParcelable[, channel=").append(valueOf).append(", type=").append(str).append(", closeReason=").append(str2).append(", appErrorCode=").append(this.zzljd).append("]").toString();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.zzlje, i, false);
        zzbfp.zzc(parcel, 3, this.type);
        zzbfp.zzc(parcel, 4, this.zzljc);
        zzbfp.zzc(parcel, 5, this.zzljd);
        zzbfp.zzai(parcel, zze);
    }

    public final void zza(ChannelListener channelListener) {
        switch (this.type) {
            case 1:
                channelListener.onChannelOpened(this.zzlje);
                return;
            case 2:
                channelListener.onChannelClosed(this.zzlje, this.zzljc, this.zzljd);
                return;
            case 3:
                channelListener.onInputClosed(this.zzlje, this.zzljc, this.zzljd);
                return;
            case 4:
                channelListener.onOutputClosed(this.zzlje, this.zzljc, this.zzljd);
                return;
            default:
                Log.w("ChannelEventParcelable", "Unknown type: " + this.type);
                return;
        }
    }
}

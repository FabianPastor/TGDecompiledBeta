package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbg;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.Channel.GetOutputStreamResult;
import com.google.android.gms.wearable.ChannelClient;

public final class zzay extends zzbfm implements Channel, ChannelClient.Channel {
    public static final Creator<zzay> CREATOR = new zzbi();
    private final String mPath;
    private final String zzecl;
    private final String zzlgr;

    public zzay(String str, String str2, String str3) {
        this.zzecl = (String) zzbq.checkNotNull(str);
        this.zzlgr = (String) zzbq.checkNotNull(str2);
        this.mPath = (String) zzbq.checkNotNull(str3);
    }

    public final PendingResult<Status> close(GoogleApiClient googleApiClient) {
        return googleApiClient.zzd(new zzaz(this, googleApiClient));
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzay)) {
            return false;
        }
        zzay com_google_android_gms_wearable_internal_zzay = (zzay) obj;
        return this.zzecl.equals(com_google_android_gms_wearable_internal_zzay.zzecl) && zzbg.equal(com_google_android_gms_wearable_internal_zzay.zzlgr, this.zzlgr) && zzbg.equal(com_google_android_gms_wearable_internal_zzay.mPath, this.mPath);
    }

    public final String getNodeId() {
        return this.zzlgr;
    }

    public final PendingResult<GetOutputStreamResult> getOutputStream(GoogleApiClient googleApiClient) {
        return googleApiClient.zzd(new zzbc(this, googleApiClient));
    }

    public final String getPath() {
        return this.mPath;
    }

    public final int hashCode() {
        return this.zzecl.hashCode();
    }

    public final String toString() {
        String substring;
        int i = 0;
        for (char c : this.zzecl.toCharArray()) {
            i += c;
        }
        String trim = this.zzecl.trim();
        int length = trim.length();
        if (length > 25) {
            substring = trim.substring(0, 10);
            trim = trim.substring(length - 10, length);
            trim = new StringBuilder((String.valueOf(substring).length() + 16) + String.valueOf(trim).length()).append(substring).append("...").append(trim).append("::").append(i).toString();
        }
        substring = this.zzlgr;
        String str = this.mPath;
        return new StringBuilder(((String.valueOf(trim).length() + 31) + String.valueOf(substring).length()) + String.valueOf(str).length()).append("Channel{token=").append(trim).append(", nodeId=").append(substring).append(", path=").append(str).append("}").toString();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.zzecl, false);
        zzbfp.zza(parcel, 3, getNodeId(), false);
        zzbfp.zza(parcel, 4, getPath(), false);
        zzbfp.zzai(parcel, zze);
    }
}

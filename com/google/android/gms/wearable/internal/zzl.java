package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzl extends zza {
    public static final Creator<zzl> CREATOR = new zzm();
    private final String mAppId;
    private int mId;
    private final String mPackageName;
    private final String zzalP;
    private final String zzaoy;
    private final String zzapS;
    private final String zzbRP;
    private final String zzbRQ;
    private final byte zzbRR;
    private final byte zzbRS;
    private final byte zzbRT;
    private final byte zzbRU;

    public zzl(int i, String str, String str2, String str3, String str4, String str5, String str6, byte b, byte b2, byte b3, byte b4, String str7) {
        this.mId = i;
        this.mAppId = str;
        this.zzbRP = str2;
        this.zzapS = str3;
        this.zzaoy = str4;
        this.zzbRQ = str5;
        this.zzalP = str6;
        this.zzbRR = b;
        this.zzbRS = b2;
        this.zzbRT = b3;
        this.zzbRU = b4;
        this.mPackageName = str7;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        zzl com_google_android_gms_wearable_internal_zzl = (zzl) obj;
        return this.mId != com_google_android_gms_wearable_internal_zzl.mId ? false : this.zzbRR != com_google_android_gms_wearable_internal_zzl.zzbRR ? false : this.zzbRS != com_google_android_gms_wearable_internal_zzl.zzbRS ? false : this.zzbRT != com_google_android_gms_wearable_internal_zzl.zzbRT ? false : this.zzbRU != com_google_android_gms_wearable_internal_zzl.zzbRU ? false : !this.mAppId.equals(com_google_android_gms_wearable_internal_zzl.mAppId) ? false : (this.zzbRP == null ? com_google_android_gms_wearable_internal_zzl.zzbRP != null : !this.zzbRP.equals(com_google_android_gms_wearable_internal_zzl.zzbRP)) ? false : !this.zzapS.equals(com_google_android_gms_wearable_internal_zzl.zzapS) ? false : !this.zzaoy.equals(com_google_android_gms_wearable_internal_zzl.zzaoy) ? false : !this.zzbRQ.equals(com_google_android_gms_wearable_internal_zzl.zzbRQ) ? false : (this.zzalP == null ? com_google_android_gms_wearable_internal_zzl.zzalP != null : !this.zzalP.equals(com_google_android_gms_wearable_internal_zzl.zzalP)) ? false : this.mPackageName != null ? this.mPackageName.equals(com_google_android_gms_wearable_internal_zzl.mPackageName) : com_google_android_gms_wearable_internal_zzl.mPackageName == null;
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((((((((((this.zzalP != null ? this.zzalP.hashCode() : 0) + (((((((((this.zzbRP != null ? this.zzbRP.hashCode() : 0) + ((((this.mId + 31) * 31) + this.mAppId.hashCode()) * 31)) * 31) + this.zzapS.hashCode()) * 31) + this.zzaoy.hashCode()) * 31) + this.zzbRQ.hashCode()) * 31)) * 31) + this.zzbRR) * 31) + this.zzbRS) * 31) + this.zzbRT) * 31) + this.zzbRU) * 31;
        if (this.mPackageName != null) {
            i = this.mPackageName.hashCode();
        }
        return hashCode + i;
    }

    public final String toString() {
        int i = this.mId;
        String str = this.mAppId;
        String str2 = this.zzbRP;
        String str3 = this.zzapS;
        String str4 = this.zzaoy;
        String str5 = this.zzbRQ;
        String str6 = this.zzalP;
        byte b = this.zzbRR;
        byte b2 = this.zzbRS;
        byte b3 = this.zzbRT;
        byte b4 = this.zzbRU;
        String str7 = this.mPackageName;
        return new StringBuilder(((((((String.valueOf(str).length() + 211) + String.valueOf(str2).length()) + String.valueOf(str3).length()) + String.valueOf(str4).length()) + String.valueOf(str5).length()) + String.valueOf(str6).length()) + String.valueOf(str7).length()).append("AncsNotificationParcelable{, id=").append(i).append(", appId='").append(str).append("', dateTime='").append(str2).append("', notificationText='").append(str3).append("', title='").append(str4).append("', subtitle='").append(str5).append("', displayName='").append(str6).append("', eventId=").append(b).append(", eventFlags=").append(b2).append(", categoryId=").append(b3).append(", categoryCount=").append(b4).append(", packageName='").append(str7).append("'}").toString();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 2, this.mId);
        zzd.zza(parcel, 3, this.mAppId, false);
        zzd.zza(parcel, 4, this.zzbRP, false);
        zzd.zza(parcel, 5, this.zzapS, false);
        zzd.zza(parcel, 6, this.zzaoy, false);
        zzd.zza(parcel, 7, this.zzbRQ, false);
        zzd.zza(parcel, 8, this.zzalP == null ? this.mAppId : this.zzalP, false);
        zzd.zza(parcel, 9, this.zzbRR);
        zzd.zza(parcel, 10, this.zzbRS);
        zzd.zza(parcel, 11, this.zzbRT);
        zzd.zza(parcel, 12, this.zzbRU);
        zzd.zza(parcel, 13, this.mPackageName, false);
        zzd.zzI(parcel, zze);
    }
}

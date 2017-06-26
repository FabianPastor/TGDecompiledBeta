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
    private final String zzbRN;
    private final String zzbRO;
    private final byte zzbRP;
    private final byte zzbRQ;
    private final byte zzbRR;
    private final byte zzbRS;

    public zzl(int i, String str, String str2, String str3, String str4, String str5, String str6, byte b, byte b2, byte b3, byte b4, String str7) {
        this.mId = i;
        this.mAppId = str;
        this.zzbRN = str2;
        this.zzapS = str3;
        this.zzaoy = str4;
        this.zzbRO = str5;
        this.zzalP = str6;
        this.zzbRP = b;
        this.zzbRQ = b2;
        this.zzbRR = b3;
        this.zzbRS = b4;
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
        return this.mId != com_google_android_gms_wearable_internal_zzl.mId ? false : this.zzbRP != com_google_android_gms_wearable_internal_zzl.zzbRP ? false : this.zzbRQ != com_google_android_gms_wearable_internal_zzl.zzbRQ ? false : this.zzbRR != com_google_android_gms_wearable_internal_zzl.zzbRR ? false : this.zzbRS != com_google_android_gms_wearable_internal_zzl.zzbRS ? false : !this.mAppId.equals(com_google_android_gms_wearable_internal_zzl.mAppId) ? false : (this.zzbRN == null ? com_google_android_gms_wearable_internal_zzl.zzbRN != null : !this.zzbRN.equals(com_google_android_gms_wearable_internal_zzl.zzbRN)) ? false : !this.zzapS.equals(com_google_android_gms_wearable_internal_zzl.zzapS) ? false : !this.zzaoy.equals(com_google_android_gms_wearable_internal_zzl.zzaoy) ? false : !this.zzbRO.equals(com_google_android_gms_wearable_internal_zzl.zzbRO) ? false : (this.zzalP == null ? com_google_android_gms_wearable_internal_zzl.zzalP != null : !this.zzalP.equals(com_google_android_gms_wearable_internal_zzl.zzalP)) ? false : this.mPackageName != null ? this.mPackageName.equals(com_google_android_gms_wearable_internal_zzl.mPackageName) : com_google_android_gms_wearable_internal_zzl.mPackageName == null;
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((((((((((this.zzalP != null ? this.zzalP.hashCode() : 0) + (((((((((this.zzbRN != null ? this.zzbRN.hashCode() : 0) + ((((this.mId + 31) * 31) + this.mAppId.hashCode()) * 31)) * 31) + this.zzapS.hashCode()) * 31) + this.zzaoy.hashCode()) * 31) + this.zzbRO.hashCode()) * 31)) * 31) + this.zzbRP) * 31) + this.zzbRQ) * 31) + this.zzbRR) * 31) + this.zzbRS) * 31;
        if (this.mPackageName != null) {
            i = this.mPackageName.hashCode();
        }
        return hashCode + i;
    }

    public final String toString() {
        int i = this.mId;
        String str = this.mAppId;
        String str2 = this.zzbRN;
        String str3 = this.zzapS;
        String str4 = this.zzaoy;
        String str5 = this.zzbRO;
        String str6 = this.zzalP;
        byte b = this.zzbRP;
        byte b2 = this.zzbRQ;
        byte b3 = this.zzbRR;
        byte b4 = this.zzbRS;
        String str7 = this.mPackageName;
        return new StringBuilder(((((((String.valueOf(str).length() + 211) + String.valueOf(str2).length()) + String.valueOf(str3).length()) + String.valueOf(str4).length()) + String.valueOf(str5).length()) + String.valueOf(str6).length()) + String.valueOf(str7).length()).append("AncsNotificationParcelable{, id=").append(i).append(", appId='").append(str).append("', dateTime='").append(str2).append("', notificationText='").append(str3).append("', title='").append(str4).append("', subtitle='").append(str5).append("', displayName='").append(str6).append("', eventId=").append(b).append(", eventFlags=").append(b2).append(", categoryId=").append(b3).append(", categoryCount=").append(b4).append(", packageName='").append(str7).append("'}").toString();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 2, this.mId);
        zzd.zza(parcel, 3, this.mAppId, false);
        zzd.zza(parcel, 4, this.zzbRN, false);
        zzd.zza(parcel, 5, this.zzapS, false);
        zzd.zza(parcel, 6, this.zzaoy, false);
        zzd.zza(parcel, 7, this.zzbRO, false);
        zzd.zza(parcel, 8, this.zzalP == null ? this.mAppId : this.zzalP, false);
        zzd.zza(parcel, 9, this.zzbRP);
        zzd.zza(parcel, 10, this.zzbRQ);
        zzd.zza(parcel, 11, this.zzbRR);
        zzd.zza(parcel, 12, this.zzbRS);
        zzd.zza(parcel, 13, this.mPackageName, false);
        zzd.zzI(parcel, zze);
    }
}

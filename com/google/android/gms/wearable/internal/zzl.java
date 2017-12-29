package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class zzl extends zzbfm {
    public static final Creator<zzl> CREATOR = new zzm();
    private final String mAppId;
    private int mId;
    private final String mPackageName;
    private final String zzegt;
    private final String zzemt;
    private final String zzepx;
    private final String zzlid;
    private final String zzlie;
    private final byte zzlif;
    private final byte zzlig;
    private final byte zzlih;
    private final byte zzlii;

    public zzl(int i, String str, String str2, String str3, String str4, String str5, String str6, byte b, byte b2, byte b3, byte b4, String str7) {
        this.mId = i;
        this.mAppId = str;
        this.zzlid = str2;
        this.zzepx = str3;
        this.zzemt = str4;
        this.zzlie = str5;
        this.zzegt = str6;
        this.zzlif = b;
        this.zzlig = b2;
        this.zzlih = b3;
        this.zzlii = b4;
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
        return this.mId != com_google_android_gms_wearable_internal_zzl.mId ? false : this.zzlif != com_google_android_gms_wearable_internal_zzl.zzlif ? false : this.zzlig != com_google_android_gms_wearable_internal_zzl.zzlig ? false : this.zzlih != com_google_android_gms_wearable_internal_zzl.zzlih ? false : this.zzlii != com_google_android_gms_wearable_internal_zzl.zzlii ? false : !this.mAppId.equals(com_google_android_gms_wearable_internal_zzl.mAppId) ? false : (this.zzlid == null ? com_google_android_gms_wearable_internal_zzl.zzlid != null : !this.zzlid.equals(com_google_android_gms_wearable_internal_zzl.zzlid)) ? false : !this.zzepx.equals(com_google_android_gms_wearable_internal_zzl.zzepx) ? false : !this.zzemt.equals(com_google_android_gms_wearable_internal_zzl.zzemt) ? false : !this.zzlie.equals(com_google_android_gms_wearable_internal_zzl.zzlie) ? false : (this.zzegt == null ? com_google_android_gms_wearable_internal_zzl.zzegt != null : !this.zzegt.equals(com_google_android_gms_wearable_internal_zzl.zzegt)) ? false : this.mPackageName != null ? this.mPackageName.equals(com_google_android_gms_wearable_internal_zzl.mPackageName) : com_google_android_gms_wearable_internal_zzl.mPackageName == null;
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((((((((((this.zzegt != null ? this.zzegt.hashCode() : 0) + (((((((((this.zzlid != null ? this.zzlid.hashCode() : 0) + ((((this.mId + 31) * 31) + this.mAppId.hashCode()) * 31)) * 31) + this.zzepx.hashCode()) * 31) + this.zzemt.hashCode()) * 31) + this.zzlie.hashCode()) * 31)) * 31) + this.zzlif) * 31) + this.zzlig) * 31) + this.zzlih) * 31) + this.zzlii) * 31;
        if (this.mPackageName != null) {
            i = this.mPackageName.hashCode();
        }
        return hashCode + i;
    }

    public final String toString() {
        int i = this.mId;
        String str = this.mAppId;
        String str2 = this.zzlid;
        String str3 = this.zzepx;
        String str4 = this.zzemt;
        String str5 = this.zzlie;
        String str6 = this.zzegt;
        byte b = this.zzlif;
        byte b2 = this.zzlig;
        byte b3 = this.zzlih;
        byte b4 = this.zzlii;
        String str7 = this.mPackageName;
        return new StringBuilder(((((((String.valueOf(str).length() + 211) + String.valueOf(str2).length()) + String.valueOf(str3).length()) + String.valueOf(str4).length()) + String.valueOf(str5).length()) + String.valueOf(str6).length()) + String.valueOf(str7).length()).append("AncsNotificationParcelable{, id=").append(i).append(", appId='").append(str).append("', dateTime='").append(str2).append("', notificationText='").append(str3).append("', title='").append(str4).append("', subtitle='").append(str5).append("', displayName='").append(str6).append("', eventId=").append(b).append(", eventFlags=").append(b2).append(", categoryId=").append(b3).append(", categoryCount=").append(b4).append(", packageName='").append(str7).append("'}").toString();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 2, this.mId);
        zzbfp.zza(parcel, 3, this.mAppId, false);
        zzbfp.zza(parcel, 4, this.zzlid, false);
        zzbfp.zza(parcel, 5, this.zzepx, false);
        zzbfp.zza(parcel, 6, this.zzemt, false);
        zzbfp.zza(parcel, 7, this.zzlie, false);
        zzbfp.zza(parcel, 8, this.zzegt == null ? this.mAppId : this.zzegt, false);
        zzbfp.zza(parcel, 9, this.zzlif);
        zzbfp.zza(parcel, 10, this.zzlig);
        zzbfp.zza(parcel, 11, this.zzlih);
        zzbfp.zza(parcel, 12, this.zzlii);
        zzbfp.zza(parcel, 13, this.mPackageName, false);
        zzbfp.zzai(parcel, zze);
    }
}

package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.zzc;
import com.google.android.gms.common.zze;

public final class zzx extends zza {
    public static final Creator<zzx> CREATOR = new zzy();
    private int version;
    Account zzaHA;
    zzc[] zzaHB;
    private int zzaHu;
    private int zzaHv;
    String zzaHw;
    IBinder zzaHx;
    Scope[] zzaHy;
    Bundle zzaHz;

    public zzx(int i) {
        this.version = 3;
        this.zzaHv = zze.GOOGLE_PLAY_SERVICES_VERSION_CODE;
        this.zzaHu = i;
    }

    zzx(int i, int i2, int i3, String str, IBinder iBinder, Scope[] scopeArr, Bundle bundle, Account account, zzc[] com_google_android_gms_common_zzcArr) {
        Account account2 = null;
        this.version = i;
        this.zzaHu = i2;
        this.zzaHv = i3;
        if ("com.google.android.gms".equals(str)) {
            this.zzaHw = "com.google.android.gms";
        } else {
            this.zzaHw = str;
        }
        if (i < 2) {
            if (iBinder != null) {
                zzal com_google_android_gms_common_internal_zzan;
                if (iBinder != null) {
                    IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.common.internal.IAccountAccessor");
                    com_google_android_gms_common_internal_zzan = queryLocalInterface instanceof zzal ? (zzal) queryLocalInterface : new zzan(iBinder);
                }
                account2 = zza.zza(com_google_android_gms_common_internal_zzan);
            }
            this.zzaHA = account2;
        } else {
            this.zzaHx = iBinder;
            this.zzaHA = account;
        }
        this.zzaHy = scopeArr;
        this.zzaHz = bundle;
        this.zzaHB = com_google_android_gms_common_zzcArr;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.version);
        zzd.zzc(parcel, 2, this.zzaHu);
        zzd.zzc(parcel, 3, this.zzaHv);
        zzd.zza(parcel, 4, this.zzaHw, false);
        zzd.zza(parcel, 5, this.zzaHx, false);
        zzd.zza(parcel, 6, this.zzaHy, i, false);
        zzd.zza(parcel, 7, this.zzaHz, false);
        zzd.zza(parcel, 8, this.zzaHA, i, false);
        zzd.zza(parcel, 10, this.zzaHB, i, false);
        zzd.zzI(parcel, zze);
    }
}

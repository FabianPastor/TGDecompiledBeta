package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.zzc;
import com.google.android.gms.common.zzf;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class zzz extends zzbfm {
    public static final Creator<zzz> CREATOR = new zzaa();
    private int version;
    private int zzfzr;
    private int zzfzs;
    String zzfzt;
    IBinder zzfzu;
    Scope[] zzfzv;
    Bundle zzfzw;
    Account zzfzx;
    zzc[] zzfzy;

    public zzz(int i) {
        this.version = 3;
        this.zzfzs = zzf.GOOGLE_PLAY_SERVICES_VERSION_CODE;
        this.zzfzr = i;
    }

    zzz(int i, int i2, int i3, String str, IBinder iBinder, Scope[] scopeArr, Bundle bundle, Account account, zzc[] com_google_android_gms_common_zzcArr) {
        Account account2 = null;
        this.version = i;
        this.zzfzr = i2;
        this.zzfzs = i3;
        if ("com.google.android.gms".equals(str)) {
            this.zzfzt = "com.google.android.gms";
        } else {
            this.zzfzt = str;
        }
        if (i < 2) {
            if (iBinder != null) {
                zzan com_google_android_gms_common_internal_zzap;
                if (iBinder != null) {
                    IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.common.internal.IAccountAccessor");
                    com_google_android_gms_common_internal_zzap = queryLocalInterface instanceof zzan ? (zzan) queryLocalInterface : new zzap(iBinder);
                }
                account2 = zza.zza(com_google_android_gms_common_internal_zzap);
            }
            this.zzfzx = account2;
        } else {
            this.zzfzu = iBinder;
            this.zzfzx = account;
        }
        this.zzfzv = scopeArr;
        this.zzfzw = bundle;
        this.zzfzy = com_google_android_gms_common_zzcArr;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 1, this.version);
        zzbfp.zzc(parcel, 2, this.zzfzr);
        zzbfp.zzc(parcel, 3, this.zzfzs);
        zzbfp.zza(parcel, 4, this.zzfzt, false);
        zzbfp.zza(parcel, 5, this.zzfzu, false);
        zzbfp.zza(parcel, 6, this.zzfzv, i, false);
        zzbfp.zza(parcel, 7, this.zzfzw, false);
        zzbfp.zza(parcel, 8, this.zzfzx, i, false);
        zzbfp.zza(parcel, 10, this.zzfzy, i, false);
        zzbfp.zzai(parcel, zze);
    }
}

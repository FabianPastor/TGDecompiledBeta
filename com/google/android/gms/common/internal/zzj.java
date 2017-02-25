package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.zzc;
import com.google.android.gms.common.zze;
import java.util.Collection;

public class zzj extends zza {
    public static final Creator<zzj> CREATOR = new zzk();
    final int version;
    final int zzaFK;
    int zzaFL;
    String zzaFM;
    IBinder zzaFN;
    Scope[] zzaFO;
    Bundle zzaFP;
    Account zzaFQ;
    zzc[] zzaFR;

    public zzj(int i) {
        this.version = 3;
        this.zzaFL = zze.GOOGLE_PLAY_SERVICES_VERSION_CODE;
        this.zzaFK = i;
    }

    zzj(int i, int i2, int i3, String str, IBinder iBinder, Scope[] scopeArr, Bundle bundle, Account account, zzc[] com_google_android_gms_common_zzcArr) {
        this.version = i;
        this.zzaFK = i2;
        this.zzaFL = i3;
        if ("com.google.android.gms".equals(str)) {
            this.zzaFM = "com.google.android.gms";
        } else {
            this.zzaFM = str;
        }
        if (i < 2) {
            this.zzaFQ = zzbq(iBinder);
        } else {
            this.zzaFN = iBinder;
            this.zzaFQ = account;
        }
        this.zzaFO = scopeArr;
        this.zzaFP = bundle;
        this.zzaFR = com_google_android_gms_common_zzcArr;
    }

    private Account zzbq(IBinder iBinder) {
        return iBinder != null ? zza.zza(zzr.zza.zzbr(iBinder)) : null;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzk.zza(this, parcel, i);
    }

    public zzj zza(zzc[] com_google_android_gms_common_zzcArr) {
        this.zzaFR = com_google_android_gms_common_zzcArr;
        return this;
    }

    public zzj zzb(zzr com_google_android_gms_common_internal_zzr) {
        if (com_google_android_gms_common_internal_zzr != null) {
            this.zzaFN = com_google_android_gms_common_internal_zzr.asBinder();
        }
        return this;
    }

    public zzj zzdm(String str) {
        this.zzaFM = str;
        return this;
    }

    public zzj zzf(Account account) {
        this.zzaFQ = account;
        return this;
    }

    public zzj zzf(Collection<Scope> collection) {
        this.zzaFO = (Scope[]) collection.toArray(new Scope[collection.size()]);
        return this;
    }

    public zzj zzp(Bundle bundle) {
        this.zzaFP = bundle;
        return this;
    }
}

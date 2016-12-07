package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.zzc;
import java.util.Collection;

public class zzj extends zza {
    public static final Creator<zzj> CREATOR = new zzk();
    final int version;
    final int zzaEm;
    int zzaEn;
    String zzaEo;
    IBinder zzaEp;
    Scope[] zzaEq;
    Bundle zzaEr;
    Account zzaEs;
    long zzaEt;

    public zzj(int i) {
        this.version = 3;
        this.zzaEn = zzc.GOOGLE_PLAY_SERVICES_VERSION_CODE;
        this.zzaEm = i;
    }

    zzj(int i, int i2, int i3, String str, IBinder iBinder, Scope[] scopeArr, Bundle bundle, Account account, long j) {
        this.version = i;
        this.zzaEm = i2;
        this.zzaEn = i3;
        if ("com.google.android.gms".equals(str)) {
            this.zzaEo = "com.google.android.gms";
        } else {
            this.zzaEo = str;
        }
        if (i < 2) {
            this.zzaEs = zzbq(iBinder);
        } else {
            this.zzaEp = iBinder;
            this.zzaEs = account;
        }
        this.zzaEq = scopeArr;
        this.zzaEr = bundle;
        this.zzaEt = j;
    }

    private Account zzbq(IBinder iBinder) {
        return iBinder != null ? zza.zza(zzr.zza.zzbr(iBinder)) : null;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzk.zza(this, parcel, i);
    }

    public zzj zzb(zzr com_google_android_gms_common_internal_zzr) {
        if (com_google_android_gms_common_internal_zzr != null) {
            this.zzaEp = com_google_android_gms_common_internal_zzr.asBinder();
        }
        return this;
    }

    public zzj zzdq(String str) {
        this.zzaEo = str;
        return this;
    }

    public zzj zze(Account account) {
        this.zzaEs = account;
        return this;
    }

    public zzj zzf(Collection<Scope> collection) {
        this.zzaEq = (Scope[]) collection.toArray(new Scope[collection.size()]);
        return this;
    }

    public zzj zzp(Bundle bundle) {
        this.zzaEr = bundle;
        return this;
    }
}

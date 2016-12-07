package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzr.zza;
import com.google.android.gms.common.zzc;
import java.util.Collection;

public class GetServiceRequest extends AbstractSafeParcelable {
    public static final Creator<GetServiceRequest> CREATOR = new zzk();
    final int Ci;
    int Cj;
    String Ck;
    IBinder Cl;
    Scope[] Cm;
    Bundle Cn;
    Account Co;
    long Cp;
    final int version;

    public GetServiceRequest(int i) {
        this.version = 3;
        this.Cj = zzc.GOOGLE_PLAY_SERVICES_VERSION_CODE;
        this.Ci = i;
    }

    GetServiceRequest(int i, int i2, int i3, String str, IBinder iBinder, Scope[] scopeArr, Bundle bundle, Account account, long j) {
        this.version = i;
        this.Ci = i2;
        this.Cj = i3;
        this.Ck = str;
        if (i < 2) {
            this.Co = zzdq(iBinder);
        } else {
            this.Cl = iBinder;
            this.Co = account;
        }
        this.Cm = scopeArr;
        this.Cn = bundle;
        this.Cp = j;
    }

    private Account zzdq(IBinder iBinder) {
        return iBinder != null ? zza.zza(zza.zzdr(iBinder)) : null;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzk.zza(this, parcel, i);
    }

    public GetServiceRequest zzb(zzr com_google_android_gms_common_internal_zzr) {
        if (com_google_android_gms_common_internal_zzr != null) {
            this.Cl = com_google_android_gms_common_internal_zzr.asBinder();
        }
        return this;
    }

    public GetServiceRequest zzd(Account account) {
        this.Co = account;
        return this;
    }

    public GetServiceRequest zzf(Collection<Scope> collection) {
        this.Cm = (Scope[]) collection.toArray(new Scope[collection.size()]);
        return this;
    }

    public GetServiceRequest zzht(String str) {
        this.Ck = str;
        return this;
    }

    public GetServiceRequest zzo(Bundle bundle) {
        this.Cn = bundle;
        return this;
    }
}

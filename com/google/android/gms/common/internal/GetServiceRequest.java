package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzp.zza;
import com.google.android.gms.common.zzc;
import java.util.Collection;

public class GetServiceRequest extends AbstractSafeParcelable {
    public static final Creator<GetServiceRequest> CREATOR = new zzi();
    final int DU;
    int DV;
    String DW;
    IBinder DX;
    Scope[] DY;
    Bundle DZ;
    Account Ea;
    long Eb;
    final int version;

    public GetServiceRequest(int i) {
        this.version = 3;
        this.DV = zzc.GOOGLE_PLAY_SERVICES_VERSION_CODE;
        this.DU = i;
    }

    GetServiceRequest(int i, int i2, int i3, String str, IBinder iBinder, Scope[] scopeArr, Bundle bundle, Account account, long j) {
        this.version = i;
        this.DU = i2;
        this.DV = i3;
        if ("com.google.android.gms".equals(str)) {
            this.DW = "com.google.android.gms";
        } else {
            this.DW = str;
        }
        if (i < 2) {
            this.Ea = zzdq(iBinder);
        } else {
            this.DX = iBinder;
            this.Ea = account;
        }
        this.DY = scopeArr;
        this.DZ = bundle;
        this.Eb = j;
    }

    private Account zzdq(IBinder iBinder) {
        return iBinder != null ? zza.zza(zza.zzdr(iBinder)) : null;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzi.zza(this, parcel, i);
    }

    public GetServiceRequest zzb(zzp com_google_android_gms_common_internal_zzp) {
        if (com_google_android_gms_common_internal_zzp != null) {
            this.DX = com_google_android_gms_common_internal_zzp.asBinder();
        }
        return this;
    }

    public GetServiceRequest zze(Account account) {
        this.Ea = account;
        return this;
    }

    public GetServiceRequest zzf(Collection<Scope> collection) {
        this.DY = (Scope[]) collection.toArray(new Scope[collection.size()]);
        return this;
    }

    public GetServiceRequest zzhv(String str) {
        this.DW = str;
        return this;
    }

    public GetServiceRequest zzo(Bundle bundle) {
        this.DZ = bundle;
        return this;
    }
}

package com.google.android.gms.auth.api.signin.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzg extends zza {
    public static final Creator<zzg> CREATOR = new zzf();
    final int versionCode;
    private Bundle zzaic;
    private int zzakD;

    zzg(int i, int i2, Bundle bundle) {
        this.versionCode = i;
        this.zzakD = i2;
        this.zzaic = bundle;
    }

    public zzg(GoogleSignInOptionsExtension googleSignInOptionsExtension) {
        this(1, 1, googleSignInOptionsExtension.toBundle());
    }

    public Bundle getBundle() {
        return this.zzaic;
    }

    public int getType() {
        return this.zzakD;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzf.zza(this, parcel, i);
    }
}

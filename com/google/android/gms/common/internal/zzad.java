package com.google.android.gms.common.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzad implements Creator<SignInButtonConfig> {
    static void zza(SignInButtonConfig signInButtonConfig, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, signInButtonConfig.mVersionCode);
        zzb.zzc(parcel, 2, signInButtonConfig.zzawq());
        zzb.zzc(parcel, 3, signInButtonConfig.zzawr());
        zzb.zza(parcel, 4, signInButtonConfig.zzaws(), i, false);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzco(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzgs(i);
    }

    public SignInButtonConfig zzco(Parcel parcel) {
        int i = 0;
        int zzcr = zza.zzcr(parcel);
        Scope[] scopeArr = null;
        int i2 = 0;
        int i3 = 0;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i3 = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    i2 = zza.zzg(parcel, zzcq);
                    break;
                case 3:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 4:
                    scopeArr = (Scope[]) zza.zzb(parcel, zzcq, Scope.CREATOR);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new SignInButtonConfig(i3, i2, i, scopeArr);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }

    public SignInButtonConfig[] zzgs(int i) {
        return new SignInButtonConfig[i];
    }
}

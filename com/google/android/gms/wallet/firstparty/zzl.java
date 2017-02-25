package com.google.android.gms.wallet.firstparty;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzl implements Creator<InitializeBuyFlowRequest> {
    static void zza(InitializeBuyFlowRequest initializeBuyFlowRequest, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, initializeBuyFlowRequest.getVersionCode());
        zzc.zza(parcel, 2, initializeBuyFlowRequest.zzbRN, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzkw(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzoS(i);
    }

    public InitializeBuyFlowRequest zzkw(Parcel parcel) {
        int zzaY = zzb.zzaY(parcel);
        int i = 0;
        byte[][] bArr = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    bArr = zzb.zzu(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new InitializeBuyFlowRequest(i, bArr);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public InitializeBuyFlowRequest[] zzoS(int i) {
        return new InitializeBuyFlowRequest[i];
    }
}

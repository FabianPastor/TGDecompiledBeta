package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import java.util.ArrayList;

public final class IsReadyToPayRequest extends zza {
    public static final Creator<IsReadyToPayRequest> CREATOR = new zzl();
    ArrayList<Integer> zzbOL;
    private String zzbOM;
    private String zzbON;

    public final class Builder {
        private /* synthetic */ IsReadyToPayRequest zzbOO;

        private Builder(IsReadyToPayRequest isReadyToPayRequest) {
            this.zzbOO = isReadyToPayRequest;
        }

        public final Builder addAllowedCardNetwork(int i) {
            if (this.zzbOO.zzbOL == null) {
                this.zzbOO.zzbOL = new ArrayList();
            }
            this.zzbOO.zzbOL.add(Integer.valueOf(i));
            return this;
        }

        public final IsReadyToPayRequest build() {
            return this.zzbOO;
        }
    }

    IsReadyToPayRequest() {
    }

    IsReadyToPayRequest(ArrayList<Integer> arrayList, String str, String str2) {
        this.zzbOL = arrayList;
        this.zzbOM = str;
        this.zzbON = str2;
    }

    public static Builder newBuilder() {
        IsReadyToPayRequest isReadyToPayRequest = new IsReadyToPayRequest();
        isReadyToPayRequest.getClass();
        return new Builder();
    }

    public final ArrayList<Integer> getAllowedCardNetworks() {
        return this.zzbOL;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbOL, false);
        zzd.zza(parcel, 4, this.zzbOM, false);
        zzd.zza(parcel, 5, this.zzbON, false);
        zzd.zzI(parcel, zze);
    }
}

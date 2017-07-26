package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import java.util.ArrayList;

public final class IsReadyToPayRequest extends zza {
    public static final Creator<IsReadyToPayRequest> CREATOR = new zzl();
    ArrayList<Integer> zzbON;
    private String zzbOO;
    private String zzbOP;

    public final class Builder {
        private /* synthetic */ IsReadyToPayRequest zzbOQ;

        private Builder(IsReadyToPayRequest isReadyToPayRequest) {
            this.zzbOQ = isReadyToPayRequest;
        }

        public final Builder addAllowedCardNetwork(int i) {
            if (this.zzbOQ.zzbON == null) {
                this.zzbOQ.zzbON = new ArrayList();
            }
            this.zzbOQ.zzbON.add(Integer.valueOf(i));
            return this;
        }

        public final IsReadyToPayRequest build() {
            return this.zzbOQ;
        }
    }

    IsReadyToPayRequest() {
    }

    IsReadyToPayRequest(ArrayList<Integer> arrayList, String str, String str2) {
        this.zzbON = arrayList;
        this.zzbOO = str;
        this.zzbOP = str2;
    }

    public static Builder newBuilder() {
        IsReadyToPayRequest isReadyToPayRequest = new IsReadyToPayRequest();
        isReadyToPayRequest.getClass();
        return new Builder();
    }

    public final ArrayList<Integer> getAllowedCardNetworks() {
        return this.zzbON;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbON, false);
        zzd.zza(parcel, 4, this.zzbOO, false);
        zzd.zza(parcel, 5, this.zzbOP, false);
        zzd.zzI(parcel, zze);
    }
}

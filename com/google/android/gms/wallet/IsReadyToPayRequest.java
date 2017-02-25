package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import java.util.ArrayList;

public final class IsReadyToPayRequest extends zza {
    public static final Creator<IsReadyToPayRequest> CREATOR = new zzk();
    ArrayList<Integer> zzbQs;
    String zzbQt;
    String zzbQu;

    public final class Builder {
        final /* synthetic */ IsReadyToPayRequest zzbQv;

        private Builder(IsReadyToPayRequest isReadyToPayRequest) {
            this.zzbQv = isReadyToPayRequest;
        }

        public Builder addAllowedCardNetwork(int i) {
            if (this.zzbQv.zzbQs == null) {
                this.zzbQv.zzbQs = new ArrayList();
            }
            this.zzbQv.zzbQs.add(Integer.valueOf(i));
            return this;
        }

        public IsReadyToPayRequest build() {
            return this.zzbQv;
        }
    }

    IsReadyToPayRequest() {
    }

    IsReadyToPayRequest(ArrayList<Integer> arrayList, String str, String str2) {
        this.zzbQs = arrayList;
    }

    public static Builder newBuilder() {
        IsReadyToPayRequest isReadyToPayRequest = new IsReadyToPayRequest();
        isReadyToPayRequest.getClass();
        return new Builder();
    }

    public ArrayList<Integer> getAllowedCardNetworks() {
        return this.zzbQs;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzk.zza(this, parcel, i);
    }
}

package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbo;

@Deprecated
public final class NotifyTransactionStatusRequest extends zza {
    public static final Creator<NotifyTransactionStatusRequest> CREATOR = new zzu();
    int status;
    String zzbOq;
    String zzbPF;

    public final class Builder {
        private /* synthetic */ NotifyTransactionStatusRequest zzbPG;

        private Builder(NotifyTransactionStatusRequest notifyTransactionStatusRequest) {
            this.zzbPG = notifyTransactionStatusRequest;
        }

        public final NotifyTransactionStatusRequest build() {
            boolean z = true;
            zzbo.zzb(!TextUtils.isEmpty(this.zzbPG.zzbOq), (Object) "googleTransactionId is required");
            if (this.zzbPG.status <= 0 || this.zzbPG.status > 8) {
                z = false;
            }
            zzbo.zzb(z, (Object) "status is an unrecognized value");
            return this.zzbPG;
        }

        public final Builder setDetailedReason(String str) {
            this.zzbPG.zzbPF = str;
            return this;
        }

        public final Builder setGoogleTransactionId(String str) {
            this.zzbPG.zzbOq = str;
            return this;
        }

        public final Builder setStatus(int i) {
            this.zzbPG.status = i;
            return this;
        }
    }

    public interface Status {
        public static final int SUCCESS = 1;

        public interface Error {
            public static final int AVS_DECLINE = 7;
            public static final int BAD_CARD = 4;
            public static final int BAD_CVC = 3;
            public static final int DECLINED = 5;
            public static final int FRAUD_DECLINE = 8;
            public static final int OTHER = 6;
            public static final int UNKNOWN = 2;
        }
    }

    NotifyTransactionStatusRequest() {
    }

    NotifyTransactionStatusRequest(String str, int i, String str2) {
        this.zzbOq = str;
        this.status = i;
        this.zzbPF = str2;
    }

    public static Builder newBuilder() {
        NotifyTransactionStatusRequest notifyTransactionStatusRequest = new NotifyTransactionStatusRequest();
        notifyTransactionStatusRequest.getClass();
        return new Builder();
    }

    public final String getDetailedReason() {
        return this.zzbPF;
    }

    public final String getGoogleTransactionId() {
        return this.zzbOq;
    }

    public final int getStatus() {
        return this.status;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbOq, false);
        zzd.zzc(parcel, 3, this.status);
        zzd.zza(parcel, 4, this.zzbPF, false);
        zzd.zzI(parcel, zze);
    }
}

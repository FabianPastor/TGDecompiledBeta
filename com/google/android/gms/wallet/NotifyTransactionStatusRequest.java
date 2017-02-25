package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;

@Deprecated
public final class NotifyTransactionStatusRequest extends zza {
    public static final Creator<NotifyTransactionStatusRequest> CREATOR = new zzp();
    int status;
    String zzbPW;
    String zzbRk;

    public final class Builder {
        final /* synthetic */ NotifyTransactionStatusRequest zzbRl;

        private Builder(NotifyTransactionStatusRequest notifyTransactionStatusRequest) {
            this.zzbRl = notifyTransactionStatusRequest;
        }

        public NotifyTransactionStatusRequest build() {
            boolean z = true;
            zzac.zzb(!TextUtils.isEmpty(this.zzbRl.zzbPW), (Object) "googleTransactionId is required");
            if (this.zzbRl.status < 1 || this.zzbRl.status > 8) {
                z = false;
            }
            zzac.zzb(z, (Object) "status is an unrecognized value");
            return this.zzbRl;
        }

        public Builder setDetailedReason(String str) {
            this.zzbRl.zzbRk = str;
            return this;
        }

        public Builder setGoogleTransactionId(String str) {
            this.zzbRl.zzbPW = str;
            return this;
        }

        public Builder setStatus(int i) {
            this.zzbRl.status = i;
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
        this.zzbPW = str;
        this.status = i;
        this.zzbRk = str2;
    }

    public static Builder newBuilder() {
        NotifyTransactionStatusRequest notifyTransactionStatusRequest = new NotifyTransactionStatusRequest();
        notifyTransactionStatusRequest.getClass();
        return new Builder();
    }

    public String getDetailedReason() {
        return this.zzbRk;
    }

    public String getGoogleTransactionId() {
        return this.zzbPW;
    }

    public int getStatus() {
        return this.status;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzp.zza(this, parcel, i);
    }
}

package com.google.android.gms.common.api;

import android.os.Looper;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.internal.zzbbe;
import com.google.android.gms.internal.zzbec;
import com.google.android.gms.internal.zzben;

public final class PendingResults {

    static final class zza<R extends Result> extends zzbbe<R> {
        private final R zzaBi;

        public zza(R r) {
            super(Looper.getMainLooper());
            this.zzaBi = r;
        }

        protected final R zzb(Status status) {
            if (status.getStatusCode() == this.zzaBi.getStatus().getStatusCode()) {
                return this.zzaBi;
            }
            throw new UnsupportedOperationException("Creating failed results is not supported");
        }
    }

    static final class zzb<R extends Result> extends zzbbe<R> {
        private final R zzaBj;

        public zzb(GoogleApiClient googleApiClient, R r) {
            super(googleApiClient);
            this.zzaBj = r;
        }

        protected final R zzb(Status status) {
            return this.zzaBj;
        }
    }

    static final class zzc<R extends Result> extends zzbbe<R> {
        public zzc(GoogleApiClient googleApiClient) {
            super(googleApiClient);
        }

        protected final R zzb(Status status) {
            throw new UnsupportedOperationException("Creating failed results is not supported");
        }
    }

    private PendingResults() {
    }

    public static PendingResult<Status> canceledPendingResult() {
        PendingResult com_google_android_gms_internal_zzben = new zzben(Looper.getMainLooper());
        com_google_android_gms_internal_zzben.cancel();
        return com_google_android_gms_internal_zzben;
    }

    public static <R extends Result> PendingResult<R> canceledPendingResult(R r) {
        zzbo.zzb((Object) r, (Object) "Result must not be null");
        zzbo.zzb(r.getStatus().getStatusCode() == 16, (Object) "Status code must be CommonStatusCodes.CANCELED");
        PendingResult com_google_android_gms_common_api_PendingResults_zza = new zza(r);
        com_google_android_gms_common_api_PendingResults_zza.cancel();
        return com_google_android_gms_common_api_PendingResults_zza;
    }

    public static <R extends Result> OptionalPendingResult<R> immediatePendingResult(R r) {
        zzbo.zzb((Object) r, (Object) "Result must not be null");
        PendingResult com_google_android_gms_common_api_PendingResults_zzc = new zzc(null);
        com_google_android_gms_common_api_PendingResults_zzc.setResult(r);
        return new zzbec(com_google_android_gms_common_api_PendingResults_zzc);
    }

    public static PendingResult<Status> immediatePendingResult(Status status) {
        zzbo.zzb((Object) status, (Object) "Result must not be null");
        PendingResult com_google_android_gms_internal_zzben = new zzben(Looper.getMainLooper());
        com_google_android_gms_internal_zzben.setResult(status);
        return com_google_android_gms_internal_zzben;
    }

    public static <R extends Result> PendingResult<R> zza(R r, GoogleApiClient googleApiClient) {
        zzbo.zzb((Object) r, (Object) "Result must not be null");
        zzbo.zzb(!r.getStatus().isSuccess(), (Object) "Status code must not be SUCCESS");
        PendingResult com_google_android_gms_common_api_PendingResults_zzb = new zzb(googleApiClient, r);
        com_google_android_gms_common_api_PendingResults_zzb.setResult(r);
        return com_google_android_gms_common_api_PendingResults_zzb;
    }

    public static PendingResult<Status> zza(Status status, GoogleApiClient googleApiClient) {
        zzbo.zzb((Object) status, (Object) "Result must not be null");
        PendingResult com_google_android_gms_internal_zzben = new zzben(googleApiClient);
        com_google_android_gms_internal_zzben.setResult(status);
        return com_google_android_gms_internal_zzben;
    }

    public static <R extends Result> OptionalPendingResult<R> zzb(R r, GoogleApiClient googleApiClient) {
        zzbo.zzb((Object) r, (Object) "Result must not be null");
        PendingResult com_google_android_gms_common_api_PendingResults_zzc = new zzc(googleApiClient);
        com_google_android_gms_common_api_PendingResults_zzc.setResult(r);
        return new zzbec(com_google_android_gms_common_api_PendingResults_zzc);
    }
}

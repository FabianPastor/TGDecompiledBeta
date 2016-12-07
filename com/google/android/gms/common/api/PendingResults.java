package com.google.android.gms.common.api;

import android.os.Looper;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.internal.zzqq;
import com.google.android.gms.internal.zzru;
import com.google.android.gms.internal.zzsc;

public final class PendingResults {

    private static final class zza<R extends Result> extends zzqq<R> {
        private final R xU;

        public zza(R r) {
            super(Looper.getMainLooper());
            this.xU = r;
        }

        protected R zzc(Status status) {
            if (status.getStatusCode() == this.xU.getStatus().getStatusCode()) {
                return this.xU;
            }
            throw new UnsupportedOperationException("Creating failed results is not supported");
        }
    }

    private static final class zzb<R extends Result> extends zzqq<R> {
        private final R xV;

        public zzb(GoogleApiClient googleApiClient, R r) {
            super(googleApiClient);
            this.xV = r;
        }

        protected R zzc(Status status) {
            return this.xV;
        }
    }

    private static final class zzc<R extends Result> extends zzqq<R> {
        public zzc(GoogleApiClient googleApiClient) {
            super(googleApiClient);
        }

        protected R zzc(Status status) {
            throw new UnsupportedOperationException("Creating failed results is not supported");
        }
    }

    private PendingResults() {
    }

    public static PendingResult<Status> canceledPendingResult() {
        PendingResult com_google_android_gms_internal_zzsc = new zzsc(Looper.getMainLooper());
        com_google_android_gms_internal_zzsc.cancel();
        return com_google_android_gms_internal_zzsc;
    }

    public static <R extends Result> PendingResult<R> canceledPendingResult(R r) {
        zzaa.zzb((Object) r, (Object) "Result must not be null");
        zzaa.zzb(r.getStatus().getStatusCode() == 16, (Object) "Status code must be CommonStatusCodes.CANCELED");
        PendingResult com_google_android_gms_common_api_PendingResults_zza = new zza(r);
        com_google_android_gms_common_api_PendingResults_zza.cancel();
        return com_google_android_gms_common_api_PendingResults_zza;
    }

    public static <R extends Result> OptionalPendingResult<R> immediatePendingResult(R r) {
        zzaa.zzb((Object) r, (Object) "Result must not be null");
        PendingResult com_google_android_gms_common_api_PendingResults_zzc = new zzc(null);
        com_google_android_gms_common_api_PendingResults_zzc.zzc((Result) r);
        return new zzru(com_google_android_gms_common_api_PendingResults_zzc);
    }

    public static PendingResult<Status> immediatePendingResult(Status status) {
        zzaa.zzb((Object) status, (Object) "Result must not be null");
        PendingResult com_google_android_gms_internal_zzsc = new zzsc(Looper.getMainLooper());
        com_google_android_gms_internal_zzsc.zzc((Result) status);
        return com_google_android_gms_internal_zzsc;
    }

    public static <R extends Result> PendingResult<R> zza(R r, GoogleApiClient googleApiClient) {
        zzaa.zzb((Object) r, (Object) "Result must not be null");
        zzaa.zzb(!r.getStatus().isSuccess(), (Object) "Status code must not be SUCCESS");
        PendingResult com_google_android_gms_common_api_PendingResults_zzb = new zzb(googleApiClient, r);
        com_google_android_gms_common_api_PendingResults_zzb.zzc((Result) r);
        return com_google_android_gms_common_api_PendingResults_zzb;
    }

    public static PendingResult<Status> zza(Status status, GoogleApiClient googleApiClient) {
        zzaa.zzb((Object) status, (Object) "Result must not be null");
        PendingResult com_google_android_gms_internal_zzsc = new zzsc(googleApiClient);
        com_google_android_gms_internal_zzsc.zzc((Result) status);
        return com_google_android_gms_internal_zzsc;
    }

    public static <R extends Result> OptionalPendingResult<R> zzb(R r, GoogleApiClient googleApiClient) {
        zzaa.zzb((Object) r, (Object) "Result must not be null");
        PendingResult com_google_android_gms_common_api_PendingResults_zzc = new zzc(googleApiClient);
        com_google_android_gms_common_api_PendingResults_zzc.zzc((Result) r);
        return new zzru(com_google_android_gms_common_api_PendingResults_zzc);
    }
}

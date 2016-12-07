package com.google.android.gms.common.api;

import android.os.Looper;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzqe;
import com.google.android.gms.internal.zzrg;
import com.google.android.gms.internal.zzrm;

public final class PendingResults {

    private static final class zza<R extends Result> extends zzqe<R> {
        private final R vT;

        public zza(R r) {
            super(Looper.getMainLooper());
            this.vT = r;
        }

        protected R zzc(Status status) {
            if (status.getStatusCode() == this.vT.getStatus().getStatusCode()) {
                return this.vT;
            }
            throw new UnsupportedOperationException("Creating failed results is not supported");
        }
    }

    private static final class zzb<R extends Result> extends zzqe<R> {
        private final R vU;

        public zzb(GoogleApiClient googleApiClient, R r) {
            super(googleApiClient);
            this.vU = r;
        }

        protected R zzc(Status status) {
            return this.vU;
        }
    }

    private static final class zzc<R extends Result> extends zzqe<R> {
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
        PendingResult com_google_android_gms_internal_zzrm = new zzrm(Looper.getMainLooper());
        com_google_android_gms_internal_zzrm.cancel();
        return com_google_android_gms_internal_zzrm;
    }

    public static <R extends Result> PendingResult<R> canceledPendingResult(R r) {
        zzac.zzb((Object) r, (Object) "Result must not be null");
        zzac.zzb(r.getStatus().getStatusCode() == 16, (Object) "Status code must be CommonStatusCodes.CANCELED");
        PendingResult com_google_android_gms_common_api_PendingResults_zza = new zza(r);
        com_google_android_gms_common_api_PendingResults_zza.cancel();
        return com_google_android_gms_common_api_PendingResults_zza;
    }

    public static <R extends Result> OptionalPendingResult<R> immediatePendingResult(R r) {
        zzac.zzb((Object) r, (Object) "Result must not be null");
        PendingResult com_google_android_gms_common_api_PendingResults_zzc = new zzc(null);
        com_google_android_gms_common_api_PendingResults_zzc.zzc((Result) r);
        return new zzrg(com_google_android_gms_common_api_PendingResults_zzc);
    }

    public static PendingResult<Status> immediatePendingResult(Status status) {
        zzac.zzb((Object) status, (Object) "Result must not be null");
        PendingResult com_google_android_gms_internal_zzrm = new zzrm(Looper.getMainLooper());
        com_google_android_gms_internal_zzrm.zzc((Result) status);
        return com_google_android_gms_internal_zzrm;
    }

    public static <R extends Result> PendingResult<R> zza(R r, GoogleApiClient googleApiClient) {
        zzac.zzb((Object) r, (Object) "Result must not be null");
        zzac.zzb(!r.getStatus().isSuccess(), (Object) "Status code must not be SUCCESS");
        PendingResult com_google_android_gms_common_api_PendingResults_zzb = new zzb(googleApiClient, r);
        com_google_android_gms_common_api_PendingResults_zzb.zzc((Result) r);
        return com_google_android_gms_common_api_PendingResults_zzb;
    }

    public static PendingResult<Status> zza(Status status, GoogleApiClient googleApiClient) {
        zzac.zzb((Object) status, (Object) "Result must not be null");
        PendingResult com_google_android_gms_internal_zzrm = new zzrm(googleApiClient);
        com_google_android_gms_internal_zzrm.zzc((Result) status);
        return com_google_android_gms_internal_zzrm;
    }

    public static <R extends Result> OptionalPendingResult<R> zzb(R r, GoogleApiClient googleApiClient) {
        zzac.zzb((Object) r, (Object) "Result must not be null");
        PendingResult com_google_android_gms_common_api_PendingResults_zzc = new zzc(googleApiClient);
        com_google_android_gms_common_api_PendingResults_zzc.zzc((Result) r);
        return new zzrg(com_google_android_gms_common_api_PendingResults_zzc);
    }
}

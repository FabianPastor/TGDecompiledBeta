package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzaa;

public final class zzrr<L> {
    private final zza Bl;
    private final zzb<L> Bm;
    private volatile L mListener;

    private final class zza extends Handler {
        final /* synthetic */ zzrr Bn;

        public zza(zzrr com_google_android_gms_internal_zzrr, Looper looper) {
            this.Bn = com_google_android_gms_internal_zzrr;
            super(looper);
        }

        public void handleMessage(Message message) {
            boolean z = true;
            if (message.what != 1) {
                z = false;
            }
            zzaa.zzbt(z);
            this.Bn.zzb((zzc) message.obj);
        }
    }

    public static final class zzb<L> {
        private final String Bo;
        private final L mListener;

        private zzb(L l, String str) {
            this.mListener = l;
            this.Bo = str;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof zzb)) {
                return false;
            }
            zzb com_google_android_gms_internal_zzrr_zzb = (zzb) obj;
            return this.mListener == com_google_android_gms_internal_zzrr_zzb.mListener && this.Bo.equals(com_google_android_gms_internal_zzrr_zzb.Bo);
        }

        public int hashCode() {
            return (System.identityHashCode(this.mListener) * 31) + this.Bo.hashCode();
        }
    }

    public interface zzc<L> {
        void zzasm();

        void zzt(L l);
    }

    zzrr(@NonNull Looper looper, @NonNull L l, @NonNull String str) {
        this.Bl = new zza(this, looper);
        this.mListener = zzaa.zzb((Object) l, (Object) "Listener must not be null");
        this.Bm = new zzb(l, zzaa.zzib(str));
    }

    public void clear() {
        this.mListener = null;
    }

    public void zza(zzc<? super L> com_google_android_gms_internal_zzrr_zzc__super_L) {
        zzaa.zzb((Object) com_google_android_gms_internal_zzrr_zzc__super_L, (Object) "Notifier must not be null");
        this.Bl.sendMessage(this.Bl.obtainMessage(1, com_google_android_gms_internal_zzrr_zzc__super_L));
    }

    @NonNull
    public zzb<L> zzatz() {
        return this.Bm;
    }

    void zzb(zzc<? super L> com_google_android_gms_internal_zzrr_zzc__super_L) {
        Object obj = this.mListener;
        if (obj == null) {
            com_google_android_gms_internal_zzrr_zzc__super_L.zzasm();
            return;
        }
        try {
            com_google_android_gms_internal_zzrr_zzc__super_L.zzt(obj);
        } catch (RuntimeException e) {
            com_google_android_gms_internal_zzrr_zzc__super_L.zzasm();
            throw e;
        }
    }
}

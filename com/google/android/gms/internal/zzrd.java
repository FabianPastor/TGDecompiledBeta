package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzac;

public final class zzrd<L> {
    private volatile L mListener;
    private final zza ze;
    private final zzb<L> zf;

    private final class zza extends Handler {
        final /* synthetic */ zzrd zg;

        public zza(zzrd com_google_android_gms_internal_zzrd, Looper looper) {
            this.zg = com_google_android_gms_internal_zzrd;
            super(looper);
        }

        public void handleMessage(Message message) {
            boolean z = true;
            if (message.what != 1) {
                z = false;
            }
            zzac.zzbs(z);
            this.zg.zzb((zzc) message.obj);
        }
    }

    public static final class zzb<L> {
        private final L mListener;
        private final String zh;

        private zzb(L l, String str) {
            this.mListener = l;
            this.zh = str;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof zzb)) {
                return false;
            }
            zzb com_google_android_gms_internal_zzrd_zzb = (zzb) obj;
            return this.mListener == com_google_android_gms_internal_zzrd_zzb.mListener && this.zh.equals(com_google_android_gms_internal_zzrd_zzb.zh);
        }

        public int hashCode() {
            return (System.identityHashCode(this.mListener) * 31) + this.zh.hashCode();
        }
    }

    public interface zzc<L> {
        void zzarg();

        void zzt(L l);
    }

    zzrd(@NonNull Looper looper, @NonNull L l, @NonNull String str) {
        this.ze = new zza(this, looper);
        this.mListener = zzac.zzb((Object) l, (Object) "Listener must not be null");
        this.zf = new zzb(l, zzac.zzhz(str));
    }

    public void clear() {
        this.mListener = null;
    }

    public void zza(zzc<? super L> com_google_android_gms_internal_zzrd_zzc__super_L) {
        zzac.zzb((Object) com_google_android_gms_internal_zzrd_zzc__super_L, (Object) "Notifier must not be null");
        this.ze.sendMessage(this.ze.obtainMessage(1, com_google_android_gms_internal_zzrd_zzc__super_L));
    }

    @NonNull
    public zzb<L> zzasr() {
        return this.zf;
    }

    void zzb(zzc<? super L> com_google_android_gms_internal_zzrd_zzc__super_L) {
        Object obj = this.mListener;
        if (obj == null) {
            com_google_android_gms_internal_zzrd_zzc__super_L.zzarg();
            return;
        }
        try {
            com_google_android_gms_internal_zzrd_zzc__super_L.zzt(obj);
        } catch (RuntimeException e) {
            com_google_android_gms_internal_zzrd_zzc__super_L.zzarg();
            throw e;
        }
    }
}

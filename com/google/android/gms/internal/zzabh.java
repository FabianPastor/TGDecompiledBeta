package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzac;

public final class zzabh<L> {
    private volatile L mListener;
    private final zza zzaCX;
    private final zzb<L> zzaCY;

    private final class zza extends Handler {
        final /* synthetic */ zzabh zzaCZ;

        public zza(zzabh com_google_android_gms_internal_zzabh, Looper looper) {
            this.zzaCZ = com_google_android_gms_internal_zzabh;
            super(looper);
        }

        public void handleMessage(Message message) {
            boolean z = true;
            if (message.what != 1) {
                z = false;
            }
            zzac.zzax(z);
            this.zzaCZ.zzb((zzc) message.obj);
        }
    }

    public static final class zzb<L> {
        private final L mListener;
        private final String zzaDa;

        zzb(L l, String str) {
            this.mListener = l;
            this.zzaDa = str;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof zzb)) {
                return false;
            }
            zzb com_google_android_gms_internal_zzabh_zzb = (zzb) obj;
            return this.mListener == com_google_android_gms_internal_zzabh_zzb.mListener && this.zzaDa.equals(com_google_android_gms_internal_zzabh_zzb.zzaDa);
        }

        public int hashCode() {
            return (System.identityHashCode(this.mListener) * 31) + this.zzaDa.hashCode();
        }
    }

    public interface zzc<L> {
        void zzs(L l);

        void zzwc();
    }

    zzabh(@NonNull Looper looper, @NonNull L l, @NonNull String str) {
        this.zzaCX = new zza(this, looper);
        this.mListener = zzac.zzb((Object) l, (Object) "Listener must not be null");
        this.zzaCY = new zzb(l, zzac.zzdr(str));
    }

    public void clear() {
        this.mListener = null;
    }

    public void zza(zzc<? super L> com_google_android_gms_internal_zzabh_zzc__super_L) {
        zzac.zzb((Object) com_google_android_gms_internal_zzabh_zzc__super_L, (Object) "Notifier must not be null");
        this.zzaCX.sendMessage(this.zzaCX.obtainMessage(1, com_google_android_gms_internal_zzabh_zzc__super_L));
    }

    void zzb(zzc<? super L> com_google_android_gms_internal_zzabh_zzc__super_L) {
        Object obj = this.mListener;
        if (obj == null) {
            com_google_android_gms_internal_zzabh_zzc__super_L.zzwc();
            return;
        }
        try {
            com_google_android_gms_internal_zzabh_zzc__super_L.zzs(obj);
        } catch (RuntimeException e) {
            com_google_android_gms_internal_zzabh_zzc__super_L.zzwc();
            throw e;
        }
    }

    public boolean zztK() {
        return this.mListener != null;
    }

    @NonNull
    public zzb<L> zzwW() {
        return this.zzaCY;
    }
}

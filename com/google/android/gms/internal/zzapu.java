package com.google.android.gms.internal;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public final class zzapu extends zzaot<Time> {
    public static final zzaou bmp = new zzaou() {
        public <T> zzaot<T> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T) {
            return com_google_android_gms_internal_zzapx_T.by() == Time.class ? new zzapu() : null;
        }
    };
    private final DateFormat bmP = new SimpleDateFormat("hh:mm:ss a");

    public synchronized void zza(zzaqa com_google_android_gms_internal_zzaqa, Time time) throws IOException {
        com_google_android_gms_internal_zzaqa.zzut(time == null ? null : this.bmP.format(time));
    }

    public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
        return zzn(com_google_android_gms_internal_zzapy);
    }

    public synchronized Time zzn(zzapy com_google_android_gms_internal_zzapy) throws IOException {
        Time time;
        if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
            com_google_android_gms_internal_zzapy.nextNull();
            time = null;
        } else {
            try {
                time = new Time(this.bmP.parse(com_google_android_gms_internal_zzapy.nextString()).getTime());
            } catch (Throwable e) {
                throw new zzaoq(e);
            }
        }
        return time;
    }
}

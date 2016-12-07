package com.google.android.gms.internal;

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public final class zzapt extends zzaot<Date> {
    public static final zzaou bmp = new zzaou() {
        public <T> zzaot<T> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T) {
            return com_google_android_gms_internal_zzapx_T.by() == Date.class ? new zzapt() : null;
        }
    };
    private final DateFormat bmP = new SimpleDateFormat("MMM d, yyyy");

    public synchronized void zza(zzaqa com_google_android_gms_internal_zzaqa, Date date) throws IOException {
        com_google_android_gms_internal_zzaqa.zzut(date == null ? null : this.bmP.format(date));
    }

    public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
        return zzm(com_google_android_gms_internal_zzapy);
    }

    public synchronized Date zzm(zzapy com_google_android_gms_internal_zzapy) throws IOException {
        Date date;
        if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
            com_google_android_gms_internal_zzapy.nextNull();
            date = null;
        } else {
            try {
                date = new Date(this.bmP.parse(com_google_android_gms_internal_zzapy.nextString()).getTime());
            } catch (Throwable e) {
                throw new zzaoq(e);
            }
        }
        return date;
    }
}

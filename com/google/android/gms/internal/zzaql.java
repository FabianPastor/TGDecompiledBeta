package com.google.android.gms.internal;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public final class zzaql extends zzapk<Time> {
    public static final zzapl bpG = new zzapl() {
        public <T> zzapk<T> zza(zzaos com_google_android_gms_internal_zzaos, zzaqo<T> com_google_android_gms_internal_zzaqo_T) {
            return com_google_android_gms_internal_zzaqo_T.bB() == Time.class ? new zzaql() : null;
        }
    };
    private final DateFormat bqg = new SimpleDateFormat("hh:mm:ss a");

    public synchronized void zza(zzaqr com_google_android_gms_internal_zzaqr, Time time) throws IOException {
        com_google_android_gms_internal_zzaqr.zzut(time == null ? null : this.bqg.format(time));
    }

    public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
        return zzn(com_google_android_gms_internal_zzaqp);
    }

    public synchronized Time zzn(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
        Time time;
        if (com_google_android_gms_internal_zzaqp.bq() == zzaqq.NULL) {
            com_google_android_gms_internal_zzaqp.nextNull();
            time = null;
        } else {
            try {
                time = new Time(this.bqg.parse(com_google_android_gms_internal_zzaqp.nextString()).getTime());
            } catch (Throwable e) {
                throw new zzaph(e);
            }
        }
        return time;
    }
}

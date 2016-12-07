package com.google.android.gms.internal;

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public final class zzaqk extends zzapk<Date> {
    public static final zzapl bpG = new zzapl() {
        public <T> zzapk<T> zza(zzaos com_google_android_gms_internal_zzaos, zzaqo<T> com_google_android_gms_internal_zzaqo_T) {
            return com_google_android_gms_internal_zzaqo_T.bB() == Date.class ? new zzaqk() : null;
        }
    };
    private final DateFormat bqg = new SimpleDateFormat("MMM d, yyyy");

    public synchronized void zza(zzaqr com_google_android_gms_internal_zzaqr, Date date) throws IOException {
        com_google_android_gms_internal_zzaqr.zzut(date == null ? null : this.bqg.format(date));
    }

    public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
        return zzm(com_google_android_gms_internal_zzaqp);
    }

    public synchronized Date zzm(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
        Date date;
        if (com_google_android_gms_internal_zzaqp.bq() == zzaqq.NULL) {
            com_google_android_gms_internal_zzaqp.nextNull();
            date = null;
        } else {
            try {
                date = new Date(this.bqg.parse(com_google_android_gms_internal_zzaqp.nextString()).getTime());
            } catch (Throwable e) {
                throw new zzaph(e);
            }
        }
        return date;
    }
}

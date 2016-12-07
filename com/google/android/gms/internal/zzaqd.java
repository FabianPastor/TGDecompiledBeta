package com.google.android.gms.internal;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class zzaqd extends zzapk<Date> {
    public static final zzapl bpG = new zzapl() {
        public <T> zzapk<T> zza(zzaos com_google_android_gms_internal_zzaos, zzaqo<T> com_google_android_gms_internal_zzaqo_T) {
            return com_google_android_gms_internal_zzaqo_T.bB() == Date.class ? new zzaqd() : null;
        }
    };
    private final DateFormat bnQ = DateFormat.getDateTimeInstance(2, 2, Locale.US);
    private final DateFormat bnR = DateFormat.getDateTimeInstance(2, 2);
    private final DateFormat bnS = bp();

    private static DateFormat bp() {
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat;
    }

    private synchronized Date zzur(String str) {
        Date parse;
        try {
            parse = this.bnR.parse(str);
        } catch (ParseException e) {
            try {
                parse = this.bnQ.parse(str);
            } catch (ParseException e2) {
                try {
                    parse = this.bnS.parse(str);
                } catch (Throwable e3) {
                    throw new zzaph(str, e3);
                }
            }
        }
        return parse;
    }

    public synchronized void zza(zzaqr com_google_android_gms_internal_zzaqr, Date date) throws IOException {
        if (date == null) {
            com_google_android_gms_internal_zzaqr.bA();
        } else {
            com_google_android_gms_internal_zzaqr.zzut(this.bnQ.format(date));
        }
    }

    public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
        return zzk(com_google_android_gms_internal_zzaqp);
    }

    public Date zzk(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
        if (com_google_android_gms_internal_zzaqp.bq() != zzaqq.NULL) {
            return zzur(com_google_android_gms_internal_zzaqp.nextString());
        }
        com_google_android_gms_internal_zzaqp.nextNull();
        return null;
    }
}

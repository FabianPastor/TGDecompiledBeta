package com.google.android.gms.internal;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class zzapm extends zzaot<Date> {
    public static final zzaou bmp = new zzaou() {
        public <T> zzaot<T> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T) {
            return com_google_android_gms_internal_zzapx_T.by() == Date.class ? new zzapm() : null;
        }
    };
    private final DateFormat bkA = DateFormat.getDateTimeInstance(2, 2);
    private final DateFormat bkB = bm();
    private final DateFormat bkz = DateFormat.getDateTimeInstance(2, 2, Locale.US);

    private static DateFormat bm() {
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat;
    }

    private synchronized Date zzur(String str) {
        Date parse;
        try {
            parse = this.bkA.parse(str);
        } catch (ParseException e) {
            try {
                parse = this.bkz.parse(str);
            } catch (ParseException e2) {
                try {
                    parse = this.bkB.parse(str);
                } catch (Throwable e3) {
                    throw new zzaoq(str, e3);
                }
            }
        }
        return parse;
    }

    public synchronized void zza(zzaqa com_google_android_gms_internal_zzaqa, Date date) throws IOException {
        if (date == null) {
            com_google_android_gms_internal_zzaqa.bx();
        } else {
            com_google_android_gms_internal_zzaqa.zzut(this.bkz.format(date));
        }
    }

    public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
        return zzk(com_google_android_gms_internal_zzapy);
    }

    public Date zzk(zzapy com_google_android_gms_internal_zzapy) throws IOException {
        if (com_google_android_gms_internal_zzapy.bn() != zzapz.NULL) {
            return zzur(com_google_android_gms_internal_zzapy.nextString());
        }
        com_google_android_gms_internal_zzapy.nextNull();
        return null;
    }
}

package com.google.android.gms.internal;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

final class zzanw implements zzaog<Date>, zzaop<Date> {
    private final DateFormat bkA;
    private final DateFormat bkB;
    private final DateFormat bkz;

    zzanw() {
        this(DateFormat.getDateTimeInstance(2, 2, Locale.US), DateFormat.getDateTimeInstance(2, 2));
    }

    public zzanw(int i, int i2) {
        this(DateFormat.getDateTimeInstance(i, i2, Locale.US), DateFormat.getDateTimeInstance(i, i2));
    }

    zzanw(String str) {
        this(new SimpleDateFormat(str, Locale.US), new SimpleDateFormat(str));
    }

    zzanw(DateFormat dateFormat, DateFormat dateFormat2) {
        this.bkz = dateFormat;
        this.bkA = dateFormat2;
        this.bkB = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        this.bkB.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private Date zza(zzaoh com_google_android_gms_internal_zzaoh) {
        Date parse;
        synchronized (this.bkA) {
            try {
                parse = this.bkA.parse(com_google_android_gms_internal_zzaoh.aR());
            } catch (ParseException e) {
                try {
                    parse = this.bkz.parse(com_google_android_gms_internal_zzaoh.aR());
                } catch (ParseException e2) {
                    try {
                        parse = this.bkB.parse(com_google_android_gms_internal_zzaoh.aR());
                    } catch (Throwable e3) {
                        throw new zzaoq(com_google_android_gms_internal_zzaoh.aR(), e3);
                    }
                }
            }
        }
        return parse;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(zzanw.class.getSimpleName());
        stringBuilder.append('(').append(this.bkA.getClass().getSimpleName()).append(')');
        return stringBuilder.toString();
    }

    public zzaoh zza(Date date, Type type, zzaoo com_google_android_gms_internal_zzaoo) {
        zzaoh com_google_android_gms_internal_zzaon;
        synchronized (this.bkA) {
            com_google_android_gms_internal_zzaon = new zzaon(this.bkz.format(date));
        }
        return com_google_android_gms_internal_zzaon;
    }

    public Date zza(zzaoh com_google_android_gms_internal_zzaoh, Type type, zzaof com_google_android_gms_internal_zzaof) throws zzaol {
        if (com_google_android_gms_internal_zzaoh instanceof zzaon) {
            Date zza = zza(com_google_android_gms_internal_zzaoh);
            if (type == Date.class) {
                return zza;
            }
            if (type == Timestamp.class) {
                return new Timestamp(zza.getTime());
            }
            if (type == java.sql.Date.class) {
                return new java.sql.Date(zza.getTime());
            }
            String valueOf = String.valueOf(getClass());
            String valueOf2 = String.valueOf(type);
            throw new IllegalArgumentException(new StringBuilder((String.valueOf(valueOf).length() + 23) + String.valueOf(valueOf2).length()).append(valueOf).append(" cannot deserialize to ").append(valueOf2).toString());
        }
        throw new zzaol("The date should be a string value");
    }

    public /* synthetic */ Object zzb(zzaoh com_google_android_gms_internal_zzaoh, Type type, zzaof com_google_android_gms_internal_zzaof) throws zzaol {
        return zza(com_google_android_gms_internal_zzaoh, type, com_google_android_gms_internal_zzaof);
    }
}

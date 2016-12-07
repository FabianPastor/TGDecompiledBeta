package com.google.android.gms.internal;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

final class zzaon implements zzaox<Date>, zzapg<Date> {
    private final DateFormat bnQ;
    private final DateFormat bnR;
    private final DateFormat bnS;

    zzaon() {
        this(DateFormat.getDateTimeInstance(2, 2, Locale.US), DateFormat.getDateTimeInstance(2, 2));
    }

    public zzaon(int i, int i2) {
        this(DateFormat.getDateTimeInstance(i, i2, Locale.US), DateFormat.getDateTimeInstance(i, i2));
    }

    zzaon(String str) {
        this(new SimpleDateFormat(str, Locale.US), new SimpleDateFormat(str));
    }

    zzaon(DateFormat dateFormat, DateFormat dateFormat2) {
        this.bnQ = dateFormat;
        this.bnR = dateFormat2;
        this.bnS = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        this.bnS.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private Date zza(zzaoy com_google_android_gms_internal_zzaoy) {
        Date parse;
        synchronized (this.bnR) {
            try {
                parse = this.bnR.parse(com_google_android_gms_internal_zzaoy.aU());
            } catch (ParseException e) {
                try {
                    parse = this.bnQ.parse(com_google_android_gms_internal_zzaoy.aU());
                } catch (ParseException e2) {
                    try {
                        parse = this.bnS.parse(com_google_android_gms_internal_zzaoy.aU());
                    } catch (Throwable e3) {
                        throw new zzaph(com_google_android_gms_internal_zzaoy.aU(), e3);
                    }
                }
            }
        }
        return parse;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(zzaon.class.getSimpleName());
        stringBuilder.append('(').append(this.bnR.getClass().getSimpleName()).append(')');
        return stringBuilder.toString();
    }

    public zzaoy zza(Date date, Type type, zzapf com_google_android_gms_internal_zzapf) {
        zzaoy com_google_android_gms_internal_zzape;
        synchronized (this.bnR) {
            com_google_android_gms_internal_zzape = new zzape(this.bnQ.format(date));
        }
        return com_google_android_gms_internal_zzape;
    }

    public Date zza(zzaoy com_google_android_gms_internal_zzaoy, Type type, zzaow com_google_android_gms_internal_zzaow) throws zzapc {
        if (com_google_android_gms_internal_zzaoy instanceof zzape) {
            Date zza = zza(com_google_android_gms_internal_zzaoy);
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
        throw new zzapc("The date should be a string value");
    }

    public /* synthetic */ Object zzb(zzaoy com_google_android_gms_internal_zzaoy, Type type, zzaow com_google_android_gms_internal_zzaow) throws zzapc {
        return zza(com_google_android_gms_internal_zzaoy, type, com_google_android_gms_internal_zzaow);
    }
}

package com.google.android.gms.common.internal;

import java.util.Iterator;

public class zzx {
    private final String separator;

    private zzx(String str) {
        this.separator = str;
    }

    public static zzx zzia(String str) {
        return new zzx(str);
    }

    public final StringBuilder zza(StringBuilder stringBuilder, Iterable<?> iterable) {
        Iterator it = iterable.iterator();
        if (it.hasNext()) {
            stringBuilder.append(zzw(it.next()));
            while (it.hasNext()) {
                stringBuilder.append(this.separator);
                stringBuilder.append(zzw(it.next()));
            }
        }
        return stringBuilder;
    }

    public final String zzb(Iterable<?> iterable) {
        return zza(new StringBuilder(), iterable).toString();
    }

    CharSequence zzw(Object obj) {
        return obj instanceof CharSequence ? (CharSequence) obj : obj.toString();
    }
}

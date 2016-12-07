package com.google.android.gms.common.internal;

import java.util.Iterator;

public class zzz {
    private final String separator;

    private zzz(String str) {
        this.separator = str;
    }

    public static zzz zzhy(String str) {
        return new zzz(str);
    }

    public final String zza(Iterable<?> iterable) {
        return zza(new StringBuilder(), iterable).toString();
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

    CharSequence zzw(Object obj) {
        return obj instanceof CharSequence ? (CharSequence) obj : obj.toString();
    }
}

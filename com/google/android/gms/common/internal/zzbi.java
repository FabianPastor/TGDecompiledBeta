package com.google.android.gms.common.internal;

import java.util.ArrayList;
import java.util.List;

public final class zzbi {
    private final Object zzddc;
    private final List<String> zzgbe;

    private zzbi(Object obj) {
        this.zzddc = zzbq.checkNotNull(obj);
        this.zzgbe = new ArrayList();
    }

    public final String toString() {
        StringBuilder append = new StringBuilder(100).append(this.zzddc.getClass().getSimpleName()).append('{');
        int size = this.zzgbe.size();
        for (int i = 0; i < size; i++) {
            append.append((String) this.zzgbe.get(i));
            if (i < size - 1) {
                append.append(", ");
            }
        }
        return append.append('}').toString();
    }

    public final zzbi zzg(String str, Object obj) {
        List list = this.zzgbe;
        String str2 = (String) zzbq.checkNotNull(str);
        String valueOf = String.valueOf(obj);
        list.add(new StringBuilder((String.valueOf(str2).length() + 1) + String.valueOf(valueOf).length()).append(str2).append("=").append(valueOf).toString());
        return this;
    }
}

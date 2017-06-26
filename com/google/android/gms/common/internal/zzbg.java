package com.google.android.gms.common.internal;

import java.util.ArrayList;
import java.util.List;

public final class zzbg {
    private final List<String> zzaIh;
    private final Object zzaaw;

    private zzbg(Object obj) {
        this.zzaaw = zzbo.zzu(obj);
        this.zzaIh = new ArrayList();
    }

    public final String toString() {
        StringBuilder append = new StringBuilder(100).append(this.zzaaw.getClass().getSimpleName()).append('{');
        int size = this.zzaIh.size();
        for (int i = 0; i < size; i++) {
            append.append((String) this.zzaIh.get(i));
            if (i < size - 1) {
                append.append(", ");
            }
        }
        return append.append('}').toString();
    }

    public final zzbg zzg(String str, Object obj) {
        List list = this.zzaIh;
        String str2 = (String) zzbo.zzu(str);
        String valueOf = String.valueOf(String.valueOf(obj));
        list.add(new StringBuilder((String.valueOf(str2).length() + 1) + String.valueOf(valueOf).length()).append(str2).append("=").append(valueOf).toString());
        return this;
    }
}

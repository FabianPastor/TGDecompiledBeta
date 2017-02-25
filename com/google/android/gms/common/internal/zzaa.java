package com.google.android.gms.common.internal;

import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class zzaa {

    public static final class zza {
        private final Object zzYG;
        private final List<String> zzaGv;

        private zza(Object obj) {
            this.zzYG = zzac.zzw(obj);
            this.zzaGv = new ArrayList();
        }

        public String toString() {
            StringBuilder append = new StringBuilder(100).append(this.zzYG.getClass().getSimpleName()).append('{');
            int size = this.zzaGv.size();
            for (int i = 0; i < size; i++) {
                append.append((String) this.zzaGv.get(i));
                if (i < size - 1) {
                    append.append(", ");
                }
            }
            return append.append('}').toString();
        }

        public zza zzg(String str, Object obj) {
            List list = this.zzaGv;
            String str2 = (String) zzac.zzw(str);
            String valueOf = String.valueOf(String.valueOf(obj));
            list.add(new StringBuilder((String.valueOf(str2).length() + 1) + String.valueOf(valueOf).length()).append(str2).append("=").append(valueOf).toString());
            return this;
        }
    }

    public static boolean equal(@Nullable Object obj, @Nullable Object obj2) {
        return obj == obj2 || (obj != null && obj.equals(obj2));
    }

    public static int hashCode(Object... objArr) {
        return Arrays.hashCode(objArr);
    }

    public static zza zzv(Object obj) {
        return new zza(obj);
    }
}

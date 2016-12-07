package com.google.android.gms.common.internal;

import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class zzz {

    public static final class zza {
        private final List<String> EG;
        private final Object zzcxk;

        private zza(Object obj) {
            this.zzcxk = zzaa.zzy(obj);
            this.EG = new ArrayList();
        }

        public String toString() {
            StringBuilder append = new StringBuilder(100).append(this.zzcxk.getClass().getSimpleName()).append('{');
            int size = this.EG.size();
            for (int i = 0; i < size; i++) {
                append.append((String) this.EG.get(i));
                if (i < size - 1) {
                    append.append(", ");
                }
            }
            return append.append('}').toString();
        }

        public zza zzg(String str, Object obj) {
            List list = this.EG;
            String str2 = (String) zzaa.zzy(str);
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

    public static zza zzx(Object obj) {
        return new zza(obj);
    }
}

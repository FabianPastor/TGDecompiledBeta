package com.google.android.gms.common.util;

import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.zzbo;
import java.util.Set;

public final class zzs {
    public static String[] zzc(Set<Scope> set) {
        zzbo.zzb((Object) set, (Object) "scopes can't be null.");
        Object obj = (Scope[]) set.toArray(new Scope[set.size()]);
        zzbo.zzb(obj, (Object) "scopes can't be null.");
        String[] strArr = new String[obj.length];
        for (int i = 0; i < obj.length; i++) {
            strArr[i] = obj[i].zzpp();
        }
        return strArr;
    }
}

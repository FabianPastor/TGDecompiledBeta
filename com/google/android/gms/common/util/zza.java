package com.google.android.gms.common.util;

import com.google.android.gms.common.internal.zzbg;
import java.lang.reflect.Array;
import java.util.Arrays;

public final class zza {
    public static <T> T[] zza(T[] tArr, T... tArr2) {
        if (tArr == null) {
            return null;
        }
        if (tArr2.length == 0) {
            return Arrays.copyOf(tArr, tArr.length);
        }
        int i;
        Object[] objArr = (Object[]) Array.newInstance(tArr2.getClass().getComponentType(), tArr.length);
        int length;
        int i2;
        if (tArr2.length == 1) {
            length = tArr.length;
            int i3 = 0;
            i = 0;
            while (i3 < length) {
                Object obj = tArr[i3];
                if (zzbg.equal(tArr2[0], obj)) {
                    i2 = i;
                } else {
                    i2 = i + 1;
                    objArr[i] = obj;
                }
                i3++;
                i = i2;
            }
        } else {
            int length2 = tArr.length;
            length = 0;
            i = 0;
            while (length < length2) {
                Object obj2 = tArr[length];
                int length3 = tArr2.length;
                i2 = 0;
                while (i2 < length3) {
                    if (zzbg.equal(tArr2[i2], obj2)) {
                        break;
                    }
                    i2++;
                }
                i2 = -1;
                if ((i2 >= 0 ? 1 : 0) == 0) {
                    i2 = i + 1;
                    objArr[i] = obj2;
                } else {
                    i2 = i;
                }
                length++;
                i = i2;
            }
        }
        return objArr == null ? null : i != objArr.length ? Arrays.copyOf(objArr, i) : objArr;
    }
}

package com.google.android.gms.vision;

import android.util.SparseArray;

public final class zzc {
    private static int zzbMW = 0;
    private static final Object zzuH = new Object();
    private SparseArray<Integer> zzbMX = new SparseArray();
    private SparseArray<Integer> zzbMY = new SparseArray();

    public final int zzbL(int i) {
        int intValue;
        synchronized (zzuH) {
            Integer num = (Integer) this.zzbMX.get(i);
            if (num != null) {
                intValue = num.intValue();
            } else {
                intValue = zzbMW;
                zzbMW++;
                this.zzbMX.append(i, Integer.valueOf(intValue));
                this.zzbMY.append(intValue, Integer.valueOf(i));
            }
        }
        return intValue;
    }

    public final int zzbM(int i) {
        int intValue;
        synchronized (zzuH) {
            intValue = ((Integer) this.zzbMY.get(i)).intValue();
        }
        return intValue;
    }
}

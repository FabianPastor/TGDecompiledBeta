package com.google.android.gms.vision;

import android.util.SparseArray;

public final class zzc {
    private static int zzbMY = 0;
    private static final Object zzuF = new Object();
    private SparseArray<Integer> zzbMZ = new SparseArray();
    private SparseArray<Integer> zzbNa = new SparseArray();

    public final int zzbL(int i) {
        int intValue;
        synchronized (zzuF) {
            Integer num = (Integer) this.zzbMZ.get(i);
            if (num != null) {
                intValue = num.intValue();
            } else {
                intValue = zzbMY;
                zzbMY++;
                this.zzbMZ.append(i, Integer.valueOf(intValue));
                this.zzbNa.append(intValue, Integer.valueOf(i));
            }
        }
        return intValue;
    }

    public final int zzbM(int i) {
        int intValue;
        synchronized (zzuF) {
            intValue = ((Integer) this.zzbNa.get(i)).intValue();
        }
        return intValue;
    }
}

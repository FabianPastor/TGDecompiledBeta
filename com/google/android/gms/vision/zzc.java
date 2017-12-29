package com.google.android.gms.vision;

import android.util.SparseArray;

public final class zzc {
    private static final Object sLock = new Object();
    private static int zzkwg = 0;
    private SparseArray<Integer> zzkwh = new SparseArray();
    private SparseArray<Integer> zzkwi = new SparseArray();

    public final int zzex(int i) {
        int intValue;
        synchronized (sLock) {
            Integer num = (Integer) this.zzkwh.get(i);
            if (num != null) {
                intValue = num.intValue();
            } else {
                intValue = zzkwg;
                zzkwg++;
                this.zzkwh.append(i, Integer.valueOf(intValue));
                this.zzkwi.append(intValue, Integer.valueOf(i));
            }
        }
        return intValue;
    }
}

package com.google.android.gms.vision;

import android.util.SparseArray;

public class zza {
    private static int zzbOA = 0;
    private static final Object zztX = new Object();
    private SparseArray<Integer> zzbOB = new SparseArray();
    private SparseArray<Integer> zzbOC = new SparseArray();

    public int zznR(int i) {
        int intValue;
        synchronized (zztX) {
            Integer num = (Integer) this.zzbOB.get(i);
            if (num != null) {
                intValue = num.intValue();
            } else {
                intValue = zzbOA;
                zzbOA++;
                this.zzbOB.append(i, Integer.valueOf(intValue));
                this.zzbOC.append(intValue, Integer.valueOf(i));
            }
        }
        return intValue;
    }

    public int zznS(int i) {
        int intValue;
        synchronized (zztX) {
            intValue = ((Integer) this.zzbOC.get(i)).intValue();
        }
        return intValue;
    }
}

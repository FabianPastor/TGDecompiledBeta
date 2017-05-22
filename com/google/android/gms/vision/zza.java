package com.google.android.gms.vision;

import android.util.SparseArray;

public class zza {
    private static int zzbOz = 0;
    private static final Object zztX = new Object();
    private SparseArray<Integer> zzbOA = new SparseArray();
    private SparseArray<Integer> zzbOB = new SparseArray();

    public int zznR(int i) {
        int intValue;
        synchronized (zztX) {
            Integer num = (Integer) this.zzbOA.get(i);
            if (num != null) {
                intValue = num.intValue();
            } else {
                intValue = zzbOz;
                zzbOz++;
                this.zzbOA.append(i, Integer.valueOf(intValue));
                this.zzbOB.append(intValue, Integer.valueOf(i));
            }
        }
        return intValue;
    }

    public int zznS(int i) {
        int intValue;
        synchronized (zztX) {
            intValue = ((Integer) this.zzbOB.get(i)).intValue();
        }
        return intValue;
    }
}

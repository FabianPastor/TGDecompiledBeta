package com.google.android.gms.vision;

import android.util.SparseArray;

public class zza {
    private static int zzbOw = 0;
    private static final Object zztX = new Object();
    private SparseArray<Integer> zzbOx = new SparseArray();
    private SparseArray<Integer> zzbOy = new SparseArray();

    public int zznR(int i) {
        int intValue;
        synchronized (zztX) {
            Integer num = (Integer) this.zzbOx.get(i);
            if (num != null) {
                intValue = num.intValue();
            } else {
                intValue = zzbOw;
                zzbOw++;
                this.zzbOx.append(i, Integer.valueOf(intValue));
                this.zzbOy.append(intValue, Integer.valueOf(i));
            }
        }
        return intValue;
    }

    public int zznS(int i) {
        int intValue;
        synchronized (zztX) {
            intValue = ((Integer) this.zzbOy.get(i)).intValue();
        }
        return intValue;
    }
}

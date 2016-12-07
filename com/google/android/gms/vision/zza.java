package com.google.android.gms.vision;

import android.util.SparseArray;

public class zza {
    private static int zzbMB = 0;
    private static final Object zztU = new Object();
    private SparseArray<Integer> zzbMC = new SparseArray();
    private SparseArray<Integer> zzbMD = new SparseArray();

    public int zzng(int i) {
        int intValue;
        synchronized (zztU) {
            Integer num = (Integer) this.zzbMC.get(i);
            if (num != null) {
                intValue = num.intValue();
            } else {
                intValue = zzbMB;
                zzbMB++;
                this.zzbMC.append(i, Integer.valueOf(intValue));
                this.zzbMD.append(intValue, Integer.valueOf(i));
            }
        }
        return intValue;
    }

    public int zznh(int i) {
        int intValue;
        synchronized (zztU) {
            intValue = ((Integer) this.zzbMD.get(i)).intValue();
        }
        return intValue;
    }
}

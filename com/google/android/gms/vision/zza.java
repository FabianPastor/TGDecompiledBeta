package com.google.android.gms.vision;

import android.util.SparseArray;

public class zza {
    private static int aKD = 0;
    private static final Object zzaok = new Object();
    private SparseArray<Integer> aKE = new SparseArray();
    private SparseArray<Integer> aKF = new SparseArray();

    public int zzabb(int i) {
        int intValue;
        synchronized (zzaok) {
            Integer num = (Integer) this.aKE.get(i);
            if (num != null) {
                intValue = num.intValue();
            } else {
                intValue = aKD;
                aKD++;
                this.aKE.append(i, Integer.valueOf(intValue));
                this.aKF.append(intValue, Integer.valueOf(i));
            }
        }
        return intValue;
    }

    public int zzabc(int i) {
        int intValue;
        synchronized (zzaok) {
            intValue = ((Integer) this.aKF.get(i)).intValue();
        }
        return intValue;
    }
}

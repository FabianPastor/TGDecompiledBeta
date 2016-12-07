package com.google.android.gms.vision;

import android.util.SparseArray;

public class zza {
    private static int aNO = 0;
    private static final Object zzaox = new Object();
    private SparseArray<Integer> aNP = new SparseArray();
    private SparseArray<Integer> aNQ = new SparseArray();

    public int zzaar(int i) {
        int intValue;
        synchronized (zzaox) {
            Integer num = (Integer) this.aNP.get(i);
            if (num != null) {
                intValue = num.intValue();
            } else {
                intValue = aNO;
                aNO++;
                this.aNP.append(i, Integer.valueOf(intValue));
                this.aNQ.append(intValue, Integer.valueOf(i));
            }
        }
        return intValue;
    }

    public int zzaas(int i) {
        int intValue;
        synchronized (zzaox) {
            intValue = ((Integer) this.aNQ.get(i)).intValue();
        }
        return intValue;
    }
}

package com.google.android.gms.common.data;

import java.util.ArrayList;

public abstract class zzg<T> extends AbstractDataBuffer<T> {
    private boolean zzaFO = false;
    private ArrayList<Integer> zzaFP;

    protected zzg(DataHolder dataHolder) {
        super(dataHolder);
    }

    private final int zzaw(int i) {
        if (i >= 0 && i < this.zzaFP.size()) {
            return ((Integer) this.zzaFP.get(i)).intValue();
        }
        throw new IllegalArgumentException("Position " + i + " is out of bounds for this buffer");
    }

    private final void zzqT() {
        synchronized (this) {
            if (!this.zzaFO) {
                int i = this.zzaCX.zzaFG;
                this.zzaFP = new ArrayList();
                if (i > 0) {
                    this.zzaFP.add(Integer.valueOf(0));
                    String zzqS = zzqS();
                    String zzd = this.zzaCX.zzd(zzqS, 0, this.zzaCX.zzat(0));
                    int i2 = 1;
                    while (i2 < i) {
                        int zzat = this.zzaCX.zzat(i2);
                        String zzd2 = this.zzaCX.zzd(zzqS, i2, zzat);
                        if (zzd2 == null) {
                            throw new NullPointerException(new StringBuilder(String.valueOf(zzqS).length() + 78).append("Missing value for markerColumn: ").append(zzqS).append(", at row: ").append(i2).append(", for window: ").append(zzat).toString());
                        }
                        if (zzd2.equals(zzd)) {
                            zzd2 = zzd;
                        } else {
                            this.zzaFP.add(Integer.valueOf(i2));
                        }
                        i2++;
                        zzd = zzd2;
                    }
                }
                this.zzaFO = true;
            }
        }
    }

    public final T get(int i) {
        int i2;
        zzqT();
        int zzaw = zzaw(i);
        if (i < 0 || i == this.zzaFP.size()) {
            i2 = 0;
        } else {
            i2 = i == this.zzaFP.size() + -1 ? this.zzaCX.zzaFG - ((Integer) this.zzaFP.get(i)).intValue() : ((Integer) this.zzaFP.get(i + 1)).intValue() - ((Integer) this.zzaFP.get(i)).intValue();
            if (i2 == 1) {
                this.zzaCX.zzat(zzaw(i));
            }
        }
        return zzi(zzaw, i2);
    }

    public int getCount() {
        zzqT();
        return this.zzaFP.size();
    }

    protected abstract T zzi(int i, int i2);

    protected abstract String zzqS();
}

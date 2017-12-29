package com.google.android.gms.common.data;

import java.util.ArrayList;

public abstract class zzg<T> extends AbstractDataBuffer<T> {
    private boolean zzfwo = false;
    private ArrayList<Integer> zzfwp;

    protected zzg(DataHolder dataHolder) {
        super(dataHolder);
    }

    private final void zzakb() {
        synchronized (this) {
            if (!this.zzfwo) {
                int i = this.zzfqt.zzfwg;
                this.zzfwp = new ArrayList();
                if (i > 0) {
                    this.zzfwp.add(Integer.valueOf(0));
                    String zzaka = zzaka();
                    String zzd = this.zzfqt.zzd(zzaka, 0, this.zzfqt.zzbz(0));
                    int i2 = 1;
                    while (i2 < i) {
                        int zzbz = this.zzfqt.zzbz(i2);
                        String zzd2 = this.zzfqt.zzd(zzaka, i2, zzbz);
                        if (zzd2 == null) {
                            throw new NullPointerException(new StringBuilder(String.valueOf(zzaka).length() + 78).append("Missing value for markerColumn: ").append(zzaka).append(", at row: ").append(i2).append(", for window: ").append(zzbz).toString());
                        }
                        if (zzd2.equals(zzd)) {
                            zzd2 = zzd;
                        } else {
                            this.zzfwp.add(Integer.valueOf(i2));
                        }
                        i2++;
                        zzd = zzd2;
                    }
                }
                this.zzfwo = true;
            }
        }
    }

    private final int zzcc(int i) {
        if (i >= 0 && i < this.zzfwp.size()) {
            return ((Integer) this.zzfwp.get(i)).intValue();
        }
        throw new IllegalArgumentException("Position " + i + " is out of bounds for this buffer");
    }

    public final T get(int i) {
        int i2;
        zzakb();
        int zzcc = zzcc(i);
        if (i < 0 || i == this.zzfwp.size()) {
            i2 = 0;
        } else {
            i2 = i == this.zzfwp.size() + -1 ? this.zzfqt.zzfwg - ((Integer) this.zzfwp.get(i)).intValue() : ((Integer) this.zzfwp.get(i + 1)).intValue() - ((Integer) this.zzfwp.get(i)).intValue();
            if (i2 == 1) {
                this.zzfqt.zzbz(zzcc(i));
            }
        }
        return zzl(zzcc, i2);
    }

    public int getCount() {
        zzakb();
        return this.zzfwp.size();
    }

    protected abstract String zzaka();

    protected abstract T zzl(int i, int i2);
}

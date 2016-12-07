package com.google.android.gms.common.data;

import java.util.ArrayList;

public abstract class zzf<T> extends AbstractDataBuffer<T> {
    private boolean Ab = false;
    private ArrayList<Integer> Ac;

    protected zzf(DataHolder dataHolder) {
        super(dataHolder);
    }

    private void zzati() {
        synchronized (this) {
            if (!this.Ab) {
                int count = this.xi.getCount();
                this.Ac = new ArrayList();
                if (count > 0) {
                    this.Ac.add(Integer.valueOf(0));
                    String zzath = zzath();
                    String zzd = this.xi.zzd(zzath, 0, this.xi.zzgb(0));
                    int i = 1;
                    while (i < count) {
                        int zzgb = this.xi.zzgb(i);
                        String zzd2 = this.xi.zzd(zzath, i, zzgb);
                        if (zzd2 == null) {
                            throw new NullPointerException(new StringBuilder(String.valueOf(zzath).length() + 78).append("Missing value for markerColumn: ").append(zzath).append(", at row: ").append(i).append(", for window: ").append(zzgb).toString());
                        }
                        if (zzd2.equals(zzd)) {
                            zzd2 = zzd;
                        } else {
                            this.Ac.add(Integer.valueOf(i));
                        }
                        i++;
                        zzd = zzd2;
                    }
                }
                this.Ab = true;
            }
        }
    }

    public final T get(int i) {
        zzati();
        return zzl(zzgf(i), zzgg(i));
    }

    public int getCount() {
        zzati();
        return this.Ac.size();
    }

    protected abstract String zzath();

    protected String zzatj() {
        return null;
    }

    int zzgf(int i) {
        if (i >= 0 && i < this.Ac.size()) {
            return ((Integer) this.Ac.get(i)).intValue();
        }
        throw new IllegalArgumentException("Position " + i + " is out of bounds for this buffer");
    }

    protected int zzgg(int i) {
        if (i < 0 || i == this.Ac.size()) {
            return 0;
        }
        int count = i == this.Ac.size() + -1 ? this.xi.getCount() - ((Integer) this.Ac.get(i)).intValue() : ((Integer) this.Ac.get(i + 1)).intValue() - ((Integer) this.Ac.get(i)).intValue();
        if (count != 1) {
            return count;
        }
        int zzgf = zzgf(i);
        int zzgb = this.xi.zzgb(zzgf);
        String zzatj = zzatj();
        return (zzatj == null || this.xi.zzd(zzatj, zzgf, zzgb) != null) ? count : 0;
    }

    protected abstract T zzl(int i, int i2);
}

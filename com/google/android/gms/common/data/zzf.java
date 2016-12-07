package com.google.android.gms.common.data;

import java.util.ArrayList;

public abstract class zzf<T> extends AbstractDataBuffer<T> {
    private boolean Cl = false;
    private ArrayList<Integer> Cm;

    protected zzf(DataHolder dataHolder) {
        super(dataHolder);
    }

    private void zzaur() {
        synchronized (this) {
            if (!this.Cl) {
                int count = this.zy.getCount();
                this.Cm = new ArrayList();
                if (count > 0) {
                    this.Cm.add(Integer.valueOf(0));
                    String zzauq = zzauq();
                    String zzd = this.zy.zzd(zzauq, 0, this.zy.zzga(0));
                    int i = 1;
                    while (i < count) {
                        int zzga = this.zy.zzga(i);
                        String zzd2 = this.zy.zzd(zzauq, i, zzga);
                        if (zzd2 == null) {
                            throw new NullPointerException(new StringBuilder(String.valueOf(zzauq).length() + 78).append("Missing value for markerColumn: ").append(zzauq).append(", at row: ").append(i).append(", for window: ").append(zzga).toString());
                        }
                        if (zzd2.equals(zzd)) {
                            zzd2 = zzd;
                        } else {
                            this.Cm.add(Integer.valueOf(i));
                        }
                        i++;
                        zzd = zzd2;
                    }
                }
                this.Cl = true;
            }
        }
    }

    public final T get(int i) {
        zzaur();
        return zzn(zzge(i), zzgf(i));
    }

    public int getCount() {
        zzaur();
        return this.Cm.size();
    }

    protected abstract String zzauq();

    protected String zzaus() {
        return null;
    }

    int zzge(int i) {
        if (i >= 0 && i < this.Cm.size()) {
            return ((Integer) this.Cm.get(i)).intValue();
        }
        throw new IllegalArgumentException("Position " + i + " is out of bounds for this buffer");
    }

    protected int zzgf(int i) {
        if (i < 0 || i == this.Cm.size()) {
            return 0;
        }
        int count = i == this.Cm.size() + -1 ? this.zy.getCount() - ((Integer) this.Cm.get(i)).intValue() : ((Integer) this.Cm.get(i + 1)).intValue() - ((Integer) this.Cm.get(i)).intValue();
        if (count != 1) {
            return count;
        }
        int zzge = zzge(i);
        int zzga = this.zy.zzga(zzge);
        String zzaus = zzaus();
        return (zzaus == null || this.zy.zzd(zzaus, zzge, zzga) != null) ? count : 0;
    }

    protected abstract T zzn(int i, int i2);
}

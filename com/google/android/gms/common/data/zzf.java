package com.google.android.gms.common.data;

import java.util.ArrayList;

public abstract class zzf<T> extends AbstractDataBuffer<T> {
    private boolean zzaEc = false;
    private ArrayList<Integer> zzaEd;

    protected zzf(DataHolder dataHolder) {
        super(dataHolder);
    }

    private void zzxo() {
        synchronized (this) {
            if (!this.zzaEc) {
                int count = this.zzaBi.getCount();
                this.zzaEd = new ArrayList();
                if (count > 0) {
                    this.zzaEd.add(Integer.valueOf(0));
                    String zzxn = zzxn();
                    String zzd = this.zzaBi.zzd(zzxn, 0, this.zzaBi.zzcI(0));
                    int i = 1;
                    while (i < count) {
                        int zzcI = this.zzaBi.zzcI(i);
                        String zzd2 = this.zzaBi.zzd(zzxn, i, zzcI);
                        if (zzd2 == null) {
                            throw new NullPointerException(new StringBuilder(String.valueOf(zzxn).length() + 78).append("Missing value for markerColumn: ").append(zzxn).append(", at row: ").append(i).append(", for window: ").append(zzcI).toString());
                        }
                        if (zzd2.equals(zzd)) {
                            zzd2 = zzd;
                        } else {
                            this.zzaEd.add(Integer.valueOf(i));
                        }
                        i++;
                        zzd = zzd2;
                    }
                }
                this.zzaEc = true;
            }
        }
    }

    public final T get(int i) {
        zzxo();
        return zzo(zzcM(i), zzcN(i));
    }

    public int getCount() {
        zzxo();
        return this.zzaEd.size();
    }

    int zzcM(int i) {
        if (i >= 0 && i < this.zzaEd.size()) {
            return ((Integer) this.zzaEd.get(i)).intValue();
        }
        throw new IllegalArgumentException("Position " + i + " is out of bounds for this buffer");
    }

    protected int zzcN(int i) {
        if (i < 0 || i == this.zzaEd.size()) {
            return 0;
        }
        int count = i == this.zzaEd.size() + -1 ? this.zzaBi.getCount() - ((Integer) this.zzaEd.get(i)).intValue() : ((Integer) this.zzaEd.get(i + 1)).intValue() - ((Integer) this.zzaEd.get(i)).intValue();
        if (count != 1) {
            return count;
        }
        int zzcM = zzcM(i);
        int zzcI = this.zzaBi.zzcI(zzcM);
        String zzxp = zzxp();
        return (zzxp == null || this.zzaBi.zzd(zzxp, zzcM, zzcI) != null) ? count : 0;
    }

    protected abstract T zzo(int i, int i2);

    protected abstract String zzxn();

    protected String zzxp() {
        return null;
    }
}

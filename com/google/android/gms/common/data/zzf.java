package com.google.android.gms.common.data;

import java.util.ArrayList;

public abstract class zzf<T> extends AbstractDataBuffer<T> {
    private boolean zzaCD = false;
    private ArrayList<Integer> zzaCE;

    protected zzf(DataHolder dataHolder) {
        super(dataHolder);
    }

    private void zzwH() {
        synchronized (this) {
            if (!this.zzaCD) {
                int count = this.zzazI.getCount();
                this.zzaCE = new ArrayList();
                if (count > 0) {
                    this.zzaCE.add(Integer.valueOf(0));
                    String zzwG = zzwG();
                    String zzd = this.zzazI.zzd(zzwG, 0, this.zzazI.zzcC(0));
                    int i = 1;
                    while (i < count) {
                        int zzcC = this.zzazI.zzcC(i);
                        String zzd2 = this.zzazI.zzd(zzwG, i, zzcC);
                        if (zzd2 == null) {
                            throw new NullPointerException(new StringBuilder(String.valueOf(zzwG).length() + 78).append("Missing value for markerColumn: ").append(zzwG).append(", at row: ").append(i).append(", for window: ").append(zzcC).toString());
                        }
                        if (zzd2.equals(zzd)) {
                            zzd2 = zzd;
                        } else {
                            this.zzaCE.add(Integer.valueOf(i));
                        }
                        i++;
                        zzd = zzd2;
                    }
                }
                this.zzaCD = true;
            }
        }
    }

    public final T get(int i) {
        zzwH();
        return zzn(zzcG(i), zzcH(i));
    }

    public int getCount() {
        zzwH();
        return this.zzaCE.size();
    }

    int zzcG(int i) {
        if (i >= 0 && i < this.zzaCE.size()) {
            return ((Integer) this.zzaCE.get(i)).intValue();
        }
        throw new IllegalArgumentException("Position " + i + " is out of bounds for this buffer");
    }

    protected int zzcH(int i) {
        if (i < 0 || i == this.zzaCE.size()) {
            return 0;
        }
        int count = i == this.zzaCE.size() + -1 ? this.zzazI.getCount() - ((Integer) this.zzaCE.get(i)).intValue() : ((Integer) this.zzaCE.get(i + 1)).intValue() - ((Integer) this.zzaCE.get(i)).intValue();
        if (count != 1) {
            return count;
        }
        int zzcG = zzcG(i);
        int zzcC = this.zzazI.zzcC(zzcG);
        String zzwI = zzwI();
        return (zzwI == null || this.zzazI.zzd(zzwI, zzcG, zzcC) != null) ? count : 0;
    }

    protected abstract T zzn(int i, int i2);

    protected abstract String zzwG();

    protected String zzwI() {
        return null;
    }
}

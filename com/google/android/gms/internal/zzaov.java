package com.google.android.gms.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class zzaov extends zzaoy implements Iterable<zzaoy> {
    private final List<zzaoy> aOH = new ArrayList();

    public Number aT() {
        if (this.aOH.size() == 1) {
            return ((zzaoy) this.aOH.get(0)).aT();
        }
        throw new IllegalStateException();
    }

    public String aU() {
        if (this.aOH.size() == 1) {
            return ((zzaoy) this.aOH.get(0)).aU();
        }
        throw new IllegalStateException();
    }

    public boolean equals(Object obj) {
        return obj == this || ((obj instanceof zzaov) && ((zzaov) obj).aOH.equals(this.aOH));
    }

    public boolean getAsBoolean() {
        if (this.aOH.size() == 1) {
            return ((zzaoy) this.aOH.get(0)).getAsBoolean();
        }
        throw new IllegalStateException();
    }

    public double getAsDouble() {
        if (this.aOH.size() == 1) {
            return ((zzaoy) this.aOH.get(0)).getAsDouble();
        }
        throw new IllegalStateException();
    }

    public int getAsInt() {
        if (this.aOH.size() == 1) {
            return ((zzaoy) this.aOH.get(0)).getAsInt();
        }
        throw new IllegalStateException();
    }

    public long getAsLong() {
        if (this.aOH.size() == 1) {
            return ((zzaoy) this.aOH.get(0)).getAsLong();
        }
        throw new IllegalStateException();
    }

    public int hashCode() {
        return this.aOH.hashCode();
    }

    public Iterator<zzaoy> iterator() {
        return this.aOH.iterator();
    }

    public int size() {
        return this.aOH.size();
    }

    public zzaoy zzagm(int i) {
        return (zzaoy) this.aOH.get(i);
    }

    public void zzc(zzaoy com_google_android_gms_internal_zzaoy) {
        Object obj;
        if (com_google_android_gms_internal_zzaoy == null) {
            obj = zzapa.bou;
        }
        this.aOH.add(obj);
    }
}

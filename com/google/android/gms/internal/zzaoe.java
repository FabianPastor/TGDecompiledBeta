package com.google.android.gms.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class zzaoe extends zzaoh implements Iterable<zzaoh> {
    private final List<zzaoh> aLw = new ArrayList();

    public Number aQ() {
        if (this.aLw.size() == 1) {
            return ((zzaoh) this.aLw.get(0)).aQ();
        }
        throw new IllegalStateException();
    }

    public String aR() {
        if (this.aLw.size() == 1) {
            return ((zzaoh) this.aLw.get(0)).aR();
        }
        throw new IllegalStateException();
    }

    public boolean equals(Object obj) {
        return obj == this || ((obj instanceof zzaoe) && ((zzaoe) obj).aLw.equals(this.aLw));
    }

    public boolean getAsBoolean() {
        if (this.aLw.size() == 1) {
            return ((zzaoh) this.aLw.get(0)).getAsBoolean();
        }
        throw new IllegalStateException();
    }

    public double getAsDouble() {
        if (this.aLw.size() == 1) {
            return ((zzaoh) this.aLw.get(0)).getAsDouble();
        }
        throw new IllegalStateException();
    }

    public int getAsInt() {
        if (this.aLw.size() == 1) {
            return ((zzaoh) this.aLw.get(0)).getAsInt();
        }
        throw new IllegalStateException();
    }

    public long getAsLong() {
        if (this.aLw.size() == 1) {
            return ((zzaoh) this.aLw.get(0)).getAsLong();
        }
        throw new IllegalStateException();
    }

    public int hashCode() {
        return this.aLw.hashCode();
    }

    public Iterator<zzaoh> iterator() {
        return this.aLw.iterator();
    }

    public int size() {
        return this.aLw.size();
    }

    public zzaoh zzagv(int i) {
        return (zzaoh) this.aLw.get(i);
    }

    public void zzc(zzaoh com_google_android_gms_internal_zzaoh) {
        Object obj;
        if (com_google_android_gms_internal_zzaoh == null) {
            obj = zzaoj.bld;
        }
        this.aLw.add(obj);
    }
}

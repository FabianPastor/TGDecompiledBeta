package com.google.android.gms.internal;

import java.io.IOException;

public final class aek extends adj<aek> implements Cloneable {
    private static volatile aek[] zzctY;
    private String key;
    private String value;

    public aek() {
        this.key = "";
        this.value = "";
        this.zzcsd = null;
        this.zzcsm = -1;
    }

    public static aek[] zzMf() {
        if (zzctY == null) {
            synchronized (adn.zzcsl) {
                if (zzctY == null) {
                    zzctY = new aek[0];
                }
            }
        }
        return zzctY;
    }

    private aek zzMg() {
        try {
            return (aek) super.zzLO();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public final /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzMg();
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof aek)) {
            return false;
        }
        aek com_google_android_gms_internal_aek = (aek) obj;
        if (this.key == null) {
            if (com_google_android_gms_internal_aek.key != null) {
                return false;
            }
        } else if (!this.key.equals(com_google_android_gms_internal_aek.key)) {
            return false;
        }
        if (this.value == null) {
            if (com_google_android_gms_internal_aek.value != null) {
                return false;
            }
        } else if (!this.value.equals(com_google_android_gms_internal_aek.value)) {
            return false;
        }
        return (this.zzcsd == null || this.zzcsd.isEmpty()) ? com_google_android_gms_internal_aek.zzcsd == null || com_google_android_gms_internal_aek.zzcsd.isEmpty() : this.zzcsd.equals(com_google_android_gms_internal_aek.zzcsd);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.value == null ? 0 : this.value.hashCode()) + (((this.key == null ? 0 : this.key.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31;
        if (!(this.zzcsd == null || this.zzcsd.isEmpty())) {
            i = this.zzcsd.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ adj zzLO() throws CloneNotSupportedException {
        return (aek) clone();
    }

    public final /* synthetic */ adp zzLP() throws CloneNotSupportedException {
        return (aek) clone();
    }

    public final /* synthetic */ adp zza(adg com_google_android_gms_internal_adg) throws IOException {
        while (true) {
            int zzLB = com_google_android_gms_internal_adg.zzLB();
            switch (zzLB) {
                case 0:
                    break;
                case 10:
                    this.key = com_google_android_gms_internal_adg.readString();
                    continue;
                case 18:
                    this.value = com_google_android_gms_internal_adg.readString();
                    continue;
                default:
                    if (!super.zza(com_google_android_gms_internal_adg, zzLB)) {
                        break;
                    }
                    continue;
            }
            return this;
        }
    }

    public final void zza(adh com_google_android_gms_internal_adh) throws IOException {
        if (!(this.key == null || this.key.equals(""))) {
            com_google_android_gms_internal_adh.zzl(1, this.key);
        }
        if (!(this.value == null || this.value.equals(""))) {
            com_google_android_gms_internal_adh.zzl(2, this.value);
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (!(this.key == null || this.key.equals(""))) {
            zzn += adh.zzm(1, this.key);
        }
        return (this.value == null || this.value.equals("")) ? zzn : zzn + adh.zzm(2, this.value);
    }
}

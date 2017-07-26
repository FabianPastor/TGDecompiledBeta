package com.google.android.gms.internal;

import java.io.IOException;

public final class ael extends adj<ael> implements Cloneable {
    private int zzctZ;
    private int zzcua;

    public ael() {
        this.zzctZ = -1;
        this.zzcua = 0;
        this.zzcsd = null;
        this.zzcsm = -1;
    }

    private ael zzMh() {
        try {
            return (ael) super.zzLO();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public final /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzMh();
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ael)) {
            return false;
        }
        ael com_google_android_gms_internal_ael = (ael) obj;
        return this.zzctZ != com_google_android_gms_internal_ael.zzctZ ? false : this.zzcua != com_google_android_gms_internal_ael.zzcua ? false : (this.zzcsd == null || this.zzcsd.isEmpty()) ? com_google_android_gms_internal_ael.zzcsd == null || com_google_android_gms_internal_ael.zzcsd.isEmpty() : this.zzcsd.equals(com_google_android_gms_internal_ael.zzcsd);
    }

    public final int hashCode() {
        int hashCode = (((((getClass().getName().hashCode() + 527) * 31) + this.zzctZ) * 31) + this.zzcua) * 31;
        int hashCode2 = (this.zzcsd == null || this.zzcsd.isEmpty()) ? 0 : this.zzcsd.hashCode();
        return hashCode2 + hashCode;
    }

    public final /* synthetic */ adj zzLO() throws CloneNotSupportedException {
        return (ael) clone();
    }

    public final /* synthetic */ adp zzLP() throws CloneNotSupportedException {
        return (ael) clone();
    }

    public final /* synthetic */ adp zza(adg com_google_android_gms_internal_adg) throws IOException {
        while (true) {
            int zzLB = com_google_android_gms_internal_adg.zzLB();
            int position;
            int zzLD;
            switch (zzLB) {
                case 0:
                    break;
                case 8:
                    position = com_google_android_gms_internal_adg.getPosition();
                    zzLD = com_google_android_gms_internal_adg.zzLD();
                    switch (zzLD) {
                        case -1:
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                        case 11:
                        case 12:
                        case 13:
                        case 14:
                        case 15:
                        case 16:
                        case 17:
                            this.zzctZ = zzLD;
                            break;
                        default:
                            com_google_android_gms_internal_adg.zzcp(position);
                            zza(com_google_android_gms_internal_adg, zzLB);
                            continue;
                    }
                case 16:
                    position = com_google_android_gms_internal_adg.getPosition();
                    zzLD = com_google_android_gms_internal_adg.zzLD();
                    switch (zzLD) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                        case 11:
                        case 12:
                        case 13:
                        case 14:
                        case 15:
                        case 16:
                        case 100:
                            this.zzcua = zzLD;
                            break;
                        default:
                            com_google_android_gms_internal_adg.zzcp(position);
                            zza(com_google_android_gms_internal_adg, zzLB);
                            continue;
                    }
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
        if (this.zzctZ != -1) {
            com_google_android_gms_internal_adh.zzr(1, this.zzctZ);
        }
        if (this.zzcua != 0) {
            com_google_android_gms_internal_adh.zzr(2, this.zzcua);
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.zzctZ != -1) {
            zzn += adh.zzs(1, this.zzctZ);
        }
        return this.zzcua != 0 ? zzn + adh.zzs(2, this.zzcua) : zzn;
    }
}

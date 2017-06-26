package com.google.android.gms.internal;

import java.io.IOException;

public abstract class ada<M extends ada<M>> extends adg {
    protected adc zzcrZ;

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzLL();
    }

    public M zzLL() throws CloneNotSupportedException {
        ada com_google_android_gms_internal_ada = (ada) super.zzLM();
        ade.zza(this, com_google_android_gms_internal_ada);
        return com_google_android_gms_internal_ada;
    }

    public /* synthetic */ adg zzLM() throws CloneNotSupportedException {
        return (ada) clone();
    }

    public final <T> T zza(adb<M, T> com_google_android_gms_internal_adb_M__T) {
        if (this.zzcrZ == null) {
            return null;
        }
        add zzcx = this.zzcrZ.zzcx(com_google_android_gms_internal_adb_M__T.tag >>> 3);
        return zzcx != null ? zzcx.zzb(com_google_android_gms_internal_adb_M__T) : null;
    }

    public void zza(acy com_google_android_gms_internal_acy) throws IOException {
        if (this.zzcrZ != null) {
            for (int i = 0; i < this.zzcrZ.size(); i++) {
                this.zzcrZ.zzcy(i).zza(com_google_android_gms_internal_acy);
            }
        }
    }

    protected final boolean zza(acx com_google_android_gms_internal_acx, int i) throws IOException {
        int position = com_google_android_gms_internal_acx.getPosition();
        if (!com_google_android_gms_internal_acx.zzcm(i)) {
            return false;
        }
        int i2 = i >>> 3;
        adi com_google_android_gms_internal_adi = new adi(i, com_google_android_gms_internal_acx.zzp(position, com_google_android_gms_internal_acx.getPosition() - position));
        add com_google_android_gms_internal_add = null;
        if (this.zzcrZ == null) {
            this.zzcrZ = new adc();
        } else {
            com_google_android_gms_internal_add = this.zzcrZ.zzcx(i2);
        }
        if (com_google_android_gms_internal_add == null) {
            com_google_android_gms_internal_add = new add();
            this.zzcrZ.zza(i2, com_google_android_gms_internal_add);
        }
        com_google_android_gms_internal_add.zza(com_google_android_gms_internal_adi);
        return true;
    }

    protected int zzn() {
        int i = 0;
        if (this.zzcrZ == null) {
            return 0;
        }
        int i2 = 0;
        while (i < this.zzcrZ.size()) {
            i2 += this.zzcrZ.zzcy(i).zzn();
            i++;
        }
        return i2;
    }
}

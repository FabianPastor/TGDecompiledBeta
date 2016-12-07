package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzaru<M extends zzaru<M>> extends zzasa {
    protected zzarw btG;

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return cn();
    }

    public M cn() throws CloneNotSupportedException {
        zzaru com_google_android_gms_internal_zzaru = (zzaru) super.co();
        zzary.zza(this, com_google_android_gms_internal_zzaru);
        return com_google_android_gms_internal_zzaru;
    }

    public /* synthetic */ zzasa co() throws CloneNotSupportedException {
        return (zzaru) clone();
    }

    public final <T> T zza(zzarv<M, T> com_google_android_gms_internal_zzarv_M__T) {
        if (this.btG == null) {
            return null;
        }
        zzarx zzahh = this.btG.zzahh(zzasd.zzahl(com_google_android_gms_internal_zzarv_M__T.tag));
        return zzahh != null ? zzahh.zzb(com_google_android_gms_internal_zzarv_M__T) : null;
    }

    public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
        if (this.btG != null) {
            for (int i = 0; i < this.btG.size(); i++) {
                this.btG.zzahi(i).zza(com_google_android_gms_internal_zzart);
            }
        }
    }

    protected final boolean zza(zzars com_google_android_gms_internal_zzars, int i) throws IOException {
        int position = com_google_android_gms_internal_zzars.getPosition();
        if (!com_google_android_gms_internal_zzars.zzagr(i)) {
            return false;
        }
        int zzahl = zzasd.zzahl(i);
        zzasc com_google_android_gms_internal_zzasc = new zzasc(i, com_google_android_gms_internal_zzars.zzae(position, com_google_android_gms_internal_zzars.getPosition() - position));
        zzarx com_google_android_gms_internal_zzarx = null;
        if (this.btG == null) {
            this.btG = new zzarw();
        } else {
            com_google_android_gms_internal_zzarx = this.btG.zzahh(zzahl);
        }
        if (com_google_android_gms_internal_zzarx == null) {
            com_google_android_gms_internal_zzarx = new zzarx();
            this.btG.zza(zzahl, com_google_android_gms_internal_zzarx);
        }
        com_google_android_gms_internal_zzarx.zza(com_google_android_gms_internal_zzasc);
        return true;
    }

    protected int zzx() {
        int i = 0;
        if (this.btG == null) {
            return 0;
        }
        int i2 = 0;
        while (i < this.btG.size()) {
            i2 += this.btG.zzahi(i).zzx();
            i++;
        }
        return i2;
    }
}

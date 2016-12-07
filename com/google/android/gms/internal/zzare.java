package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzare<M extends zzare<M>> extends zzark {
    protected zzarg bqv;

    public M cP() throws CloneNotSupportedException {
        zzare com_google_android_gms_internal_zzare = (zzare) super.cQ();
        zzari.zza(this, com_google_android_gms_internal_zzare);
        return com_google_android_gms_internal_zzare;
    }

    public /* synthetic */ zzark cQ() throws CloneNotSupportedException {
        return (zzare) clone();
    }

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return cP();
    }

    public final <T> T zza(zzarf<M, T> com_google_android_gms_internal_zzarf_M__T) {
        if (this.bqv == null) {
            return null;
        }
        zzarh zzahq = this.bqv.zzahq(zzarn.zzahu(com_google_android_gms_internal_zzarf_M__T.tag));
        return zzahq != null ? zzahq.zzb(com_google_android_gms_internal_zzarf_M__T) : null;
    }

    public void zza(zzard com_google_android_gms_internal_zzard) throws IOException {
        if (this.bqv != null) {
            for (int i = 0; i < this.bqv.size(); i++) {
                this.bqv.zzahr(i).zza(com_google_android_gms_internal_zzard);
            }
        }
    }

    protected final boolean zza(zzarc com_google_android_gms_internal_zzarc, int i) throws IOException {
        int position = com_google_android_gms_internal_zzarc.getPosition();
        if (!com_google_android_gms_internal_zzarc.zzaha(i)) {
            return false;
        }
        int zzahu = zzarn.zzahu(i);
        zzarm com_google_android_gms_internal_zzarm = new zzarm(i, com_google_android_gms_internal_zzarc.zzad(position, com_google_android_gms_internal_zzarc.getPosition() - position));
        zzarh com_google_android_gms_internal_zzarh = null;
        if (this.bqv == null) {
            this.bqv = new zzarg();
        } else {
            com_google_android_gms_internal_zzarh = this.bqv.zzahq(zzahu);
        }
        if (com_google_android_gms_internal_zzarh == null) {
            com_google_android_gms_internal_zzarh = new zzarh();
            this.bqv.zza(zzahu, com_google_android_gms_internal_zzarh);
        }
        com_google_android_gms_internal_zzarh.zza(com_google_android_gms_internal_zzarm);
        return true;
    }

    protected int zzx() {
        int i = 0;
        if (this.bqv == null) {
            return 0;
        }
        int i2 = 0;
        while (i < this.bqv.size()) {
            i2 += this.bqv.zzahr(i).zzx();
            i++;
        }
        return i2;
    }
}

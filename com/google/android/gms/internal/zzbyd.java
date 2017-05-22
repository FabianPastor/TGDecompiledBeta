package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzbyd<M extends zzbyd<M>> extends zzbyj {
    protected zzbyf zzcwC;

    private void zza(int i, zzbyl com_google_android_gms_internal_zzbyl) {
        zzbyg com_google_android_gms_internal_zzbyg = null;
        if (this.zzcwC == null) {
            this.zzcwC = new zzbyf();
        } else {
            com_google_android_gms_internal_zzbyg = this.zzcwC.zzrt(i);
        }
        if (com_google_android_gms_internal_zzbyg == null) {
            com_google_android_gms_internal_zzbyg = new zzbyg();
            this.zzcwC.zza(i, com_google_android_gms_internal_zzbyg);
        }
        com_google_android_gms_internal_zzbyg.zza(com_google_android_gms_internal_zzbyl);
    }

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzafp();
    }

    public final <T> T zza(zzbye<M, T> com_google_android_gms_internal_zzbye_M__T) {
        if (this.zzcwC == null) {
            return null;
        }
        zzbyg zzrt = this.zzcwC.zzrt(zzbym.zzrx(com_google_android_gms_internal_zzbye_M__T.tag));
        return zzrt != null ? zzrt.zzb(com_google_android_gms_internal_zzbye_M__T) : null;
    }

    public void zza(zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
        if (this.zzcwC != null) {
            for (int i = 0; i < this.zzcwC.size(); i++) {
                this.zzcwC.zzru(i).zza(com_google_android_gms_internal_zzbyc);
            }
        }
    }

    protected final boolean zza(zzbyb com_google_android_gms_internal_zzbyb, int i) throws IOException {
        int position = com_google_android_gms_internal_zzbyb.getPosition();
        if (!com_google_android_gms_internal_zzbyb.zzrd(i)) {
            return false;
        }
        zza(zzbym.zzrx(i), new zzbyl(i, com_google_android_gms_internal_zzbyb.zzI(position, com_google_android_gms_internal_zzbyb.getPosition() - position)));
        return true;
    }

    public M zzafp() throws CloneNotSupportedException {
        zzbyd com_google_android_gms_internal_zzbyd = (zzbyd) super.zzafq();
        zzbyh.zza(this, com_google_android_gms_internal_zzbyd);
        return com_google_android_gms_internal_zzbyd;
    }

    public /* synthetic */ zzbyj zzafq() throws CloneNotSupportedException {
        return (zzbyd) clone();
    }

    protected int zzu() {
        int i = 0;
        if (this.zzcwC == null) {
            return 0;
        }
        int i2 = 0;
        while (i < this.zzcwC.size()) {
            i2 += this.zzcwC.zzru(i).zzu();
            i++;
        }
        return i2;
    }
}

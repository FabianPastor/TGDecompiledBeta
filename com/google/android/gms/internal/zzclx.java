package com.google.android.gms.internal;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.RendererCapabilities;

public final class zzclx extends zzfjm<zzclx> {
    private static volatile zzclx[] zzjks;
    public String name;
    public Boolean zzjkt;
    public Boolean zzjku;
    public Integer zzjkv;

    public zzclx() {
        this.name = null;
        this.zzjkt = null;
        this.zzjku = null;
        this.zzjkv = null;
        this.zzpnc = null;
        this.zzpfd = -1;
    }

    public static zzclx[] zzbbe() {
        if (zzjks == null) {
            synchronized (zzfjq.zzpnk) {
                if (zzjks == null) {
                    zzjks = new zzclx[0];
                }
            }
        }
        return zzjks;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzclx)) {
            return false;
        }
        zzclx com_google_android_gms_internal_zzclx = (zzclx) obj;
        if (this.name == null) {
            if (com_google_android_gms_internal_zzclx.name != null) {
                return false;
            }
        } else if (!this.name.equals(com_google_android_gms_internal_zzclx.name)) {
            return false;
        }
        if (this.zzjkt == null) {
            if (com_google_android_gms_internal_zzclx.zzjkt != null) {
                return false;
            }
        } else if (!this.zzjkt.equals(com_google_android_gms_internal_zzclx.zzjkt)) {
            return false;
        }
        if (this.zzjku == null) {
            if (com_google_android_gms_internal_zzclx.zzjku != null) {
                return false;
            }
        } else if (!this.zzjku.equals(com_google_android_gms_internal_zzclx.zzjku)) {
            return false;
        }
        if (this.zzjkv == null) {
            if (com_google_android_gms_internal_zzclx.zzjkv != null) {
                return false;
            }
        } else if (!this.zzjkv.equals(com_google_android_gms_internal_zzclx.zzjkv)) {
            return false;
        }
        return (this.zzpnc == null || this.zzpnc.isEmpty()) ? com_google_android_gms_internal_zzclx.zzpnc == null || com_google_android_gms_internal_zzclx.zzpnc.isEmpty() : this.zzpnc.equals(com_google_android_gms_internal_zzclx.zzpnc);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzjkv == null ? 0 : this.zzjkv.hashCode()) + (((this.zzjku == null ? 0 : this.zzjku.hashCode()) + (((this.zzjkt == null ? 0 : this.zzjkt.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31;
        if (!(this.zzpnc == null || this.zzpnc.isEmpty())) {
            i = this.zzpnc.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ zzfjs zza(zzfjj com_google_android_gms_internal_zzfjj) throws IOException {
        while (true) {
            int zzcvt = com_google_android_gms_internal_zzfjj.zzcvt();
            switch (zzcvt) {
                case 0:
                    break;
                case 10:
                    this.name = com_google_android_gms_internal_zzfjj.readString();
                    continue;
                case 16:
                    this.zzjkt = Boolean.valueOf(com_google_android_gms_internal_zzfjj.zzcvz());
                    continue;
                case RendererCapabilities.ADAPTIVE_SUPPORT_MASK /*24*/:
                    this.zzjku = Boolean.valueOf(com_google_android_gms_internal_zzfjj.zzcvz());
                    continue;
                case 32:
                    this.zzjkv = Integer.valueOf(com_google_android_gms_internal_zzfjj.zzcwi());
                    continue;
                default:
                    if (!super.zza(com_google_android_gms_internal_zzfjj, zzcvt)) {
                        break;
                    }
                    continue;
            }
            return this;
        }
    }

    public final void zza(zzfjk com_google_android_gms_internal_zzfjk) throws IOException {
        if (this.name != null) {
            com_google_android_gms_internal_zzfjk.zzn(1, this.name);
        }
        if (this.zzjkt != null) {
            com_google_android_gms_internal_zzfjk.zzl(2, this.zzjkt.booleanValue());
        }
        if (this.zzjku != null) {
            com_google_android_gms_internal_zzfjk.zzl(3, this.zzjku.booleanValue());
        }
        if (this.zzjkv != null) {
            com_google_android_gms_internal_zzfjk.zzaa(4, this.zzjkv.intValue());
        }
        super.zza(com_google_android_gms_internal_zzfjk);
    }

    protected final int zzq() {
        int zzq = super.zzq();
        if (this.name != null) {
            zzq += zzfjk.zzo(1, this.name);
        }
        if (this.zzjkt != null) {
            this.zzjkt.booleanValue();
            zzq += zzfjk.zzlg(2) + 1;
        }
        if (this.zzjku != null) {
            this.zzjku.booleanValue();
            zzq += zzfjk.zzlg(3) + 1;
        }
        return this.zzjkv != null ? zzq + zzfjk.zzad(4, this.zzjkv.intValue()) : zzq;
    }
}

package com.google.android.gms.internal;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.RendererCapabilities;

public final class zzcly extends zzfjm<zzcly> {
    public String zzixs;
    public Long zzjkw;
    private Integer zzjkx;
    public zzclz[] zzjky;
    public zzclx[] zzjkz;
    public zzclr[] zzjla;

    public zzcly() {
        this.zzjkw = null;
        this.zzixs = null;
        this.zzjkx = null;
        this.zzjky = zzclz.zzbbf();
        this.zzjkz = zzclx.zzbbe();
        this.zzjla = zzclr.zzbba();
        this.zzpnc = null;
        this.zzpfd = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcly)) {
            return false;
        }
        zzcly com_google_android_gms_internal_zzcly = (zzcly) obj;
        if (this.zzjkw == null) {
            if (com_google_android_gms_internal_zzcly.zzjkw != null) {
                return false;
            }
        } else if (!this.zzjkw.equals(com_google_android_gms_internal_zzcly.zzjkw)) {
            return false;
        }
        if (this.zzixs == null) {
            if (com_google_android_gms_internal_zzcly.zzixs != null) {
                return false;
            }
        } else if (!this.zzixs.equals(com_google_android_gms_internal_zzcly.zzixs)) {
            return false;
        }
        if (this.zzjkx == null) {
            if (com_google_android_gms_internal_zzcly.zzjkx != null) {
                return false;
            }
        } else if (!this.zzjkx.equals(com_google_android_gms_internal_zzcly.zzjkx)) {
            return false;
        }
        return !zzfjq.equals(this.zzjky, com_google_android_gms_internal_zzcly.zzjky) ? false : !zzfjq.equals(this.zzjkz, com_google_android_gms_internal_zzcly.zzjkz) ? false : !zzfjq.equals(this.zzjla, com_google_android_gms_internal_zzcly.zzjla) ? false : (this.zzpnc == null || this.zzpnc.isEmpty()) ? com_google_android_gms_internal_zzcly.zzpnc == null || com_google_android_gms_internal_zzcly.zzpnc.isEmpty() : this.zzpnc.equals(com_google_android_gms_internal_zzcly.zzpnc);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((((((((this.zzjkx == null ? 0 : this.zzjkx.hashCode()) + (((this.zzixs == null ? 0 : this.zzixs.hashCode()) + (((this.zzjkw == null ? 0 : this.zzjkw.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31) + zzfjq.hashCode(this.zzjky)) * 31) + zzfjq.hashCode(this.zzjkz)) * 31) + zzfjq.hashCode(this.zzjla)) * 31;
        if (!(this.zzpnc == null || this.zzpnc.isEmpty())) {
            i = this.zzpnc.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ zzfjs zza(zzfjj com_google_android_gms_internal_zzfjj) throws IOException {
        while (true) {
            int zzcvt = com_google_android_gms_internal_zzfjj.zzcvt();
            int zzb;
            Object obj;
            switch (zzcvt) {
                case 0:
                    break;
                case 8:
                    this.zzjkw = Long.valueOf(com_google_android_gms_internal_zzfjj.zzcwn());
                    continue;
                case 18:
                    this.zzixs = com_google_android_gms_internal_zzfjj.readString();
                    continue;
                case RendererCapabilities.ADAPTIVE_SUPPORT_MASK /*24*/:
                    this.zzjkx = Integer.valueOf(com_google_android_gms_internal_zzfjj.zzcwi());
                    continue;
                case 34:
                    zzb = zzfjv.zzb(com_google_android_gms_internal_zzfjj, 34);
                    zzcvt = this.zzjky == null ? 0 : this.zzjky.length;
                    obj = new zzclz[(zzb + zzcvt)];
                    if (zzcvt != 0) {
                        System.arraycopy(this.zzjky, 0, obj, 0, zzcvt);
                    }
                    while (zzcvt < obj.length - 1) {
                        obj[zzcvt] = new zzclz();
                        com_google_android_gms_internal_zzfjj.zza(obj[zzcvt]);
                        com_google_android_gms_internal_zzfjj.zzcvt();
                        zzcvt++;
                    }
                    obj[zzcvt] = new zzclz();
                    com_google_android_gms_internal_zzfjj.zza(obj[zzcvt]);
                    this.zzjky = obj;
                    continue;
                case 42:
                    zzb = zzfjv.zzb(com_google_android_gms_internal_zzfjj, 42);
                    zzcvt = this.zzjkz == null ? 0 : this.zzjkz.length;
                    obj = new zzclx[(zzb + zzcvt)];
                    if (zzcvt != 0) {
                        System.arraycopy(this.zzjkz, 0, obj, 0, zzcvt);
                    }
                    while (zzcvt < obj.length - 1) {
                        obj[zzcvt] = new zzclx();
                        com_google_android_gms_internal_zzfjj.zza(obj[zzcvt]);
                        com_google_android_gms_internal_zzfjj.zzcvt();
                        zzcvt++;
                    }
                    obj[zzcvt] = new zzclx();
                    com_google_android_gms_internal_zzfjj.zza(obj[zzcvt]);
                    this.zzjkz = obj;
                    continue;
                case 50:
                    zzb = zzfjv.zzb(com_google_android_gms_internal_zzfjj, 50);
                    zzcvt = this.zzjla == null ? 0 : this.zzjla.length;
                    obj = new zzclr[(zzb + zzcvt)];
                    if (zzcvt != 0) {
                        System.arraycopy(this.zzjla, 0, obj, 0, zzcvt);
                    }
                    while (zzcvt < obj.length - 1) {
                        obj[zzcvt] = new zzclr();
                        com_google_android_gms_internal_zzfjj.zza(obj[zzcvt]);
                        com_google_android_gms_internal_zzfjj.zzcvt();
                        zzcvt++;
                    }
                    obj[zzcvt] = new zzclr();
                    com_google_android_gms_internal_zzfjj.zza(obj[zzcvt]);
                    this.zzjla = obj;
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
        int i = 0;
        if (this.zzjkw != null) {
            com_google_android_gms_internal_zzfjk.zzf(1, this.zzjkw.longValue());
        }
        if (this.zzixs != null) {
            com_google_android_gms_internal_zzfjk.zzn(2, this.zzixs);
        }
        if (this.zzjkx != null) {
            com_google_android_gms_internal_zzfjk.zzaa(3, this.zzjkx.intValue());
        }
        if (this.zzjky != null && this.zzjky.length > 0) {
            for (zzfjs com_google_android_gms_internal_zzfjs : this.zzjky) {
                if (com_google_android_gms_internal_zzfjs != null) {
                    com_google_android_gms_internal_zzfjk.zza(4, com_google_android_gms_internal_zzfjs);
                }
            }
        }
        if (this.zzjkz != null && this.zzjkz.length > 0) {
            for (zzfjs com_google_android_gms_internal_zzfjs2 : this.zzjkz) {
                if (com_google_android_gms_internal_zzfjs2 != null) {
                    com_google_android_gms_internal_zzfjk.zza(5, com_google_android_gms_internal_zzfjs2);
                }
            }
        }
        if (this.zzjla != null && this.zzjla.length > 0) {
            while (i < this.zzjla.length) {
                zzfjs com_google_android_gms_internal_zzfjs3 = this.zzjla[i];
                if (com_google_android_gms_internal_zzfjs3 != null) {
                    com_google_android_gms_internal_zzfjk.zza(6, com_google_android_gms_internal_zzfjs3);
                }
                i++;
            }
        }
        super.zza(com_google_android_gms_internal_zzfjk);
    }

    protected final int zzq() {
        int i;
        int i2 = 0;
        int zzq = super.zzq();
        if (this.zzjkw != null) {
            zzq += zzfjk.zzc(1, this.zzjkw.longValue());
        }
        if (this.zzixs != null) {
            zzq += zzfjk.zzo(2, this.zzixs);
        }
        if (this.zzjkx != null) {
            zzq += zzfjk.zzad(3, this.zzjkx.intValue());
        }
        if (this.zzjky != null && this.zzjky.length > 0) {
            i = zzq;
            for (zzfjs com_google_android_gms_internal_zzfjs : this.zzjky) {
                if (com_google_android_gms_internal_zzfjs != null) {
                    i += zzfjk.zzb(4, com_google_android_gms_internal_zzfjs);
                }
            }
            zzq = i;
        }
        if (this.zzjkz != null && this.zzjkz.length > 0) {
            i = zzq;
            for (zzfjs com_google_android_gms_internal_zzfjs2 : this.zzjkz) {
                if (com_google_android_gms_internal_zzfjs2 != null) {
                    i += zzfjk.zzb(5, com_google_android_gms_internal_zzfjs2);
                }
            }
            zzq = i;
        }
        if (this.zzjla != null && this.zzjla.length > 0) {
            while (i2 < this.zzjla.length) {
                zzfjs com_google_android_gms_internal_zzfjs3 = this.zzjla[i2];
                if (com_google_android_gms_internal_zzfjs3 != null) {
                    zzq += zzfjk.zzb(6, com_google_android_gms_internal_zzfjs3);
                }
                i2++;
            }
        }
        return zzq;
    }
}

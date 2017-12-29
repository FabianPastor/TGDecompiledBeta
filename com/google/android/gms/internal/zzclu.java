package com.google.android.gms.internal;

import java.io.IOException;

public final class zzclu extends zzfjm<zzclu> {
    public Integer zzjkg;
    public Boolean zzjkh;
    public String zzjki;
    public String zzjkj;
    public String zzjkk;

    public zzclu() {
        this.zzjkg = null;
        this.zzjkh = null;
        this.zzjki = null;
        this.zzjkj = null;
        this.zzjkk = null;
        this.zzpnc = null;
        this.zzpfd = -1;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final zzclu zzh(zzfjj com_google_android_gms_internal_zzfjj) throws IOException {
        while (true) {
            int zzcvt = com_google_android_gms_internal_zzfjj.zzcvt();
            switch (zzcvt) {
                case 0:
                    break;
                case 8:
                    int position = com_google_android_gms_internal_zzfjj.getPosition();
                    try {
                        int zzcwi = com_google_android_gms_internal_zzfjj.zzcwi();
                        switch (zzcwi) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                                this.zzjkg = Integer.valueOf(zzcwi);
                                continue;
                            default:
                                throw new IllegalArgumentException(zzcwi + " is not a valid enum ComparisonType");
                        }
                    } catch (IllegalArgumentException e) {
                        com_google_android_gms_internal_zzfjj.zzmg(position);
                        zza(com_google_android_gms_internal_zzfjj, zzcvt);
                        break;
                    }
                case 16:
                    this.zzjkh = Boolean.valueOf(com_google_android_gms_internal_zzfjj.zzcvz());
                    continue;
                case 26:
                    this.zzjki = com_google_android_gms_internal_zzfjj.readString();
                    continue;
                case 34:
                    this.zzjkj = com_google_android_gms_internal_zzfjj.readString();
                    continue;
                case 42:
                    this.zzjkk = com_google_android_gms_internal_zzfjj.readString();
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

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzclu)) {
            return false;
        }
        zzclu com_google_android_gms_internal_zzclu = (zzclu) obj;
        if (this.zzjkg == null) {
            if (com_google_android_gms_internal_zzclu.zzjkg != null) {
                return false;
            }
        } else if (!this.zzjkg.equals(com_google_android_gms_internal_zzclu.zzjkg)) {
            return false;
        }
        if (this.zzjkh == null) {
            if (com_google_android_gms_internal_zzclu.zzjkh != null) {
                return false;
            }
        } else if (!this.zzjkh.equals(com_google_android_gms_internal_zzclu.zzjkh)) {
            return false;
        }
        if (this.zzjki == null) {
            if (com_google_android_gms_internal_zzclu.zzjki != null) {
                return false;
            }
        } else if (!this.zzjki.equals(com_google_android_gms_internal_zzclu.zzjki)) {
            return false;
        }
        if (this.zzjkj == null) {
            if (com_google_android_gms_internal_zzclu.zzjkj != null) {
                return false;
            }
        } else if (!this.zzjkj.equals(com_google_android_gms_internal_zzclu.zzjkj)) {
            return false;
        }
        if (this.zzjkk == null) {
            if (com_google_android_gms_internal_zzclu.zzjkk != null) {
                return false;
            }
        } else if (!this.zzjkk.equals(com_google_android_gms_internal_zzclu.zzjkk)) {
            return false;
        }
        return (this.zzpnc == null || this.zzpnc.isEmpty()) ? com_google_android_gms_internal_zzclu.zzpnc == null || com_google_android_gms_internal_zzclu.zzpnc.isEmpty() : this.zzpnc.equals(com_google_android_gms_internal_zzclu.zzpnc);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzjkk == null ? 0 : this.zzjkk.hashCode()) + (((this.zzjkj == null ? 0 : this.zzjkj.hashCode()) + (((this.zzjki == null ? 0 : this.zzjki.hashCode()) + (((this.zzjkh == null ? 0 : this.zzjkh.hashCode()) + (((this.zzjkg == null ? 0 : this.zzjkg.intValue()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
        if (!(this.zzpnc == null || this.zzpnc.isEmpty())) {
            i = this.zzpnc.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ zzfjs zza(zzfjj com_google_android_gms_internal_zzfjj) throws IOException {
        return zzh(com_google_android_gms_internal_zzfjj);
    }

    public final void zza(zzfjk com_google_android_gms_internal_zzfjk) throws IOException {
        if (this.zzjkg != null) {
            com_google_android_gms_internal_zzfjk.zzaa(1, this.zzjkg.intValue());
        }
        if (this.zzjkh != null) {
            com_google_android_gms_internal_zzfjk.zzl(2, this.zzjkh.booleanValue());
        }
        if (this.zzjki != null) {
            com_google_android_gms_internal_zzfjk.zzn(3, this.zzjki);
        }
        if (this.zzjkj != null) {
            com_google_android_gms_internal_zzfjk.zzn(4, this.zzjkj);
        }
        if (this.zzjkk != null) {
            com_google_android_gms_internal_zzfjk.zzn(5, this.zzjkk);
        }
        super.zza(com_google_android_gms_internal_zzfjk);
    }

    protected final int zzq() {
        int zzq = super.zzq();
        if (this.zzjkg != null) {
            zzq += zzfjk.zzad(1, this.zzjkg.intValue());
        }
        if (this.zzjkh != null) {
            this.zzjkh.booleanValue();
            zzq += zzfjk.zzlg(2) + 1;
        }
        if (this.zzjki != null) {
            zzq += zzfjk.zzo(3, this.zzjki);
        }
        if (this.zzjkj != null) {
            zzq += zzfjk.zzo(4, this.zzjkj);
        }
        return this.zzjkk != null ? zzq + zzfjk.zzo(5, this.zzjkk) : zzq;
    }
}

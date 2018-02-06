package com.google.android.gms.internal;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.RendererCapabilities;

public final class zzclw extends zzfjm<zzclw> {
    public Integer zzjko;
    public String zzjkp;
    public Boolean zzjkq;
    public String[] zzjkr;

    public zzclw() {
        this.zzjko = null;
        this.zzjkp = null;
        this.zzjkq = null;
        this.zzjkr = zzfjv.EMPTY_STRING_ARRAY;
        this.zzpnc = null;
        this.zzpfd = -1;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final zzclw zzi(zzfjj com_google_android_gms_internal_zzfjj) throws IOException {
        int position;
        while (true) {
            int zzcvt = com_google_android_gms_internal_zzfjj.zzcvt();
            switch (zzcvt) {
                case 0:
                    break;
                case 8:
                    position = com_google_android_gms_internal_zzfjj.getPosition();
                    try {
                        int zzcwi = com_google_android_gms_internal_zzfjj.zzcwi();
                        switch (zzcwi) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                                this.zzjko = Integer.valueOf(zzcwi);
                                continue;
                            default:
                                throw new IllegalArgumentException(zzcwi + " is not a valid enum MatchType");
                        }
                    } catch (IllegalArgumentException e) {
                        com_google_android_gms_internal_zzfjj.zzmg(position);
                        zza(com_google_android_gms_internal_zzfjj, zzcvt);
                        break;
                    }
                case 18:
                    this.zzjkp = com_google_android_gms_internal_zzfjj.readString();
                    continue;
                case RendererCapabilities.ADAPTIVE_SUPPORT_MASK /*24*/:
                    this.zzjkq = Boolean.valueOf(com_google_android_gms_internal_zzfjj.zzcvz());
                    continue;
                case 34:
                    position = zzfjv.zzb(com_google_android_gms_internal_zzfjj, 34);
                    zzcvt = this.zzjkr == null ? 0 : this.zzjkr.length;
                    Object obj = new String[(position + zzcvt)];
                    if (zzcvt != 0) {
                        System.arraycopy(this.zzjkr, 0, obj, 0, zzcvt);
                    }
                    while (zzcvt < obj.length - 1) {
                        obj[zzcvt] = com_google_android_gms_internal_zzfjj.readString();
                        com_google_android_gms_internal_zzfjj.zzcvt();
                        zzcvt++;
                    }
                    obj[zzcvt] = com_google_android_gms_internal_zzfjj.readString();
                    this.zzjkr = obj;
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
        if (!(obj instanceof zzclw)) {
            return false;
        }
        zzclw com_google_android_gms_internal_zzclw = (zzclw) obj;
        if (this.zzjko == null) {
            if (com_google_android_gms_internal_zzclw.zzjko != null) {
                return false;
            }
        } else if (!this.zzjko.equals(com_google_android_gms_internal_zzclw.zzjko)) {
            return false;
        }
        if (this.zzjkp == null) {
            if (com_google_android_gms_internal_zzclw.zzjkp != null) {
                return false;
            }
        } else if (!this.zzjkp.equals(com_google_android_gms_internal_zzclw.zzjkp)) {
            return false;
        }
        if (this.zzjkq == null) {
            if (com_google_android_gms_internal_zzclw.zzjkq != null) {
                return false;
            }
        } else if (!this.zzjkq.equals(com_google_android_gms_internal_zzclw.zzjkq)) {
            return false;
        }
        return !zzfjq.equals(this.zzjkr, com_google_android_gms_internal_zzclw.zzjkr) ? false : (this.zzpnc == null || this.zzpnc.isEmpty()) ? com_google_android_gms_internal_zzclw.zzpnc == null || com_google_android_gms_internal_zzclw.zzpnc.isEmpty() : this.zzpnc.equals(com_google_android_gms_internal_zzclw.zzpnc);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((((this.zzjkq == null ? 0 : this.zzjkq.hashCode()) + (((this.zzjkp == null ? 0 : this.zzjkp.hashCode()) + (((this.zzjko == null ? 0 : this.zzjko.intValue()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31) + zzfjq.hashCode(this.zzjkr)) * 31;
        if (!(this.zzpnc == null || this.zzpnc.isEmpty())) {
            i = this.zzpnc.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ zzfjs zza(zzfjj com_google_android_gms_internal_zzfjj) throws IOException {
        return zzi(com_google_android_gms_internal_zzfjj);
    }

    public final void zza(zzfjk com_google_android_gms_internal_zzfjk) throws IOException {
        if (this.zzjko != null) {
            com_google_android_gms_internal_zzfjk.zzaa(1, this.zzjko.intValue());
        }
        if (this.zzjkp != null) {
            com_google_android_gms_internal_zzfjk.zzn(2, this.zzjkp);
        }
        if (this.zzjkq != null) {
            com_google_android_gms_internal_zzfjk.zzl(3, this.zzjkq.booleanValue());
        }
        if (this.zzjkr != null && this.zzjkr.length > 0) {
            for (String str : this.zzjkr) {
                if (str != null) {
                    com_google_android_gms_internal_zzfjk.zzn(4, str);
                }
            }
        }
        super.zza(com_google_android_gms_internal_zzfjk);
    }

    protected final int zzq() {
        int i = 0;
        int zzq = super.zzq();
        if (this.zzjko != null) {
            zzq += zzfjk.zzad(1, this.zzjko.intValue());
        }
        if (this.zzjkp != null) {
            zzq += zzfjk.zzo(2, this.zzjkp);
        }
        if (this.zzjkq != null) {
            this.zzjkq.booleanValue();
            zzq += zzfjk.zzlg(3) + 1;
        }
        if (this.zzjkr == null || this.zzjkr.length <= 0) {
            return zzq;
        }
        int i2 = 0;
        int i3 = 0;
        while (i < this.zzjkr.length) {
            String str = this.zzjkr[i];
            if (str != null) {
                i3++;
                i2 += zzfjk.zztt(str);
            }
            i++;
        }
        return (zzq + i2) + (i3 * 1);
    }
}

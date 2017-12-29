package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcmf extends zzfjm<zzcmf> {
    public long[] zzjmp;
    public long[] zzjmq;

    public zzcmf() {
        this.zzjmp = zzfjv.zzpnq;
        this.zzjmq = zzfjv.zzpnq;
        this.zzpnc = null;
        this.zzpfd = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcmf)) {
            return false;
        }
        zzcmf com_google_android_gms_internal_zzcmf = (zzcmf) obj;
        return !zzfjq.equals(this.zzjmp, com_google_android_gms_internal_zzcmf.zzjmp) ? false : !zzfjq.equals(this.zzjmq, com_google_android_gms_internal_zzcmf.zzjmq) ? false : (this.zzpnc == null || this.zzpnc.isEmpty()) ? com_google_android_gms_internal_zzcmf.zzpnc == null || com_google_android_gms_internal_zzcmf.zzpnc.isEmpty() : this.zzpnc.equals(com_google_android_gms_internal_zzcmf.zzpnc);
    }

    public final int hashCode() {
        int hashCode = (((((getClass().getName().hashCode() + 527) * 31) + zzfjq.hashCode(this.zzjmp)) * 31) + zzfjq.hashCode(this.zzjmq)) * 31;
        int hashCode2 = (this.zzpnc == null || this.zzpnc.isEmpty()) ? 0 : this.zzpnc.hashCode();
        return hashCode2 + hashCode;
    }

    public final /* synthetic */ zzfjs zza(zzfjj com_google_android_gms_internal_zzfjj) throws IOException {
        while (true) {
            int zzcvt = com_google_android_gms_internal_zzfjj.zzcvt();
            int zzb;
            Object obj;
            int zzks;
            Object obj2;
            switch (zzcvt) {
                case 0:
                    break;
                case 8:
                    zzb = zzfjv.zzb(com_google_android_gms_internal_zzfjj, 8);
                    zzcvt = this.zzjmp == null ? 0 : this.zzjmp.length;
                    obj = new long[(zzb + zzcvt)];
                    if (zzcvt != 0) {
                        System.arraycopy(this.zzjmp, 0, obj, 0, zzcvt);
                    }
                    while (zzcvt < obj.length - 1) {
                        obj[zzcvt] = com_google_android_gms_internal_zzfjj.zzcwn();
                        com_google_android_gms_internal_zzfjj.zzcvt();
                        zzcvt++;
                    }
                    obj[zzcvt] = com_google_android_gms_internal_zzfjj.zzcwn();
                    this.zzjmp = obj;
                    continue;
                case 10:
                    zzks = com_google_android_gms_internal_zzfjj.zzks(com_google_android_gms_internal_zzfjj.zzcwi());
                    zzb = com_google_android_gms_internal_zzfjj.getPosition();
                    zzcvt = 0;
                    while (com_google_android_gms_internal_zzfjj.zzcwk() > 0) {
                        com_google_android_gms_internal_zzfjj.zzcwn();
                        zzcvt++;
                    }
                    com_google_android_gms_internal_zzfjj.zzmg(zzb);
                    zzb = this.zzjmp == null ? 0 : this.zzjmp.length;
                    obj2 = new long[(zzcvt + zzb)];
                    if (zzb != 0) {
                        System.arraycopy(this.zzjmp, 0, obj2, 0, zzb);
                    }
                    while (zzb < obj2.length) {
                        obj2[zzb] = com_google_android_gms_internal_zzfjj.zzcwn();
                        zzb++;
                    }
                    this.zzjmp = obj2;
                    com_google_android_gms_internal_zzfjj.zzkt(zzks);
                    continue;
                case 16:
                    zzb = zzfjv.zzb(com_google_android_gms_internal_zzfjj, 16);
                    zzcvt = this.zzjmq == null ? 0 : this.zzjmq.length;
                    obj = new long[(zzb + zzcvt)];
                    if (zzcvt != 0) {
                        System.arraycopy(this.zzjmq, 0, obj, 0, zzcvt);
                    }
                    while (zzcvt < obj.length - 1) {
                        obj[zzcvt] = com_google_android_gms_internal_zzfjj.zzcwn();
                        com_google_android_gms_internal_zzfjj.zzcvt();
                        zzcvt++;
                    }
                    obj[zzcvt] = com_google_android_gms_internal_zzfjj.zzcwn();
                    this.zzjmq = obj;
                    continue;
                case 18:
                    zzks = com_google_android_gms_internal_zzfjj.zzks(com_google_android_gms_internal_zzfjj.zzcwi());
                    zzb = com_google_android_gms_internal_zzfjj.getPosition();
                    zzcvt = 0;
                    while (com_google_android_gms_internal_zzfjj.zzcwk() > 0) {
                        com_google_android_gms_internal_zzfjj.zzcwn();
                        zzcvt++;
                    }
                    com_google_android_gms_internal_zzfjj.zzmg(zzb);
                    zzb = this.zzjmq == null ? 0 : this.zzjmq.length;
                    obj2 = new long[(zzcvt + zzb)];
                    if (zzb != 0) {
                        System.arraycopy(this.zzjmq, 0, obj2, 0, zzb);
                    }
                    while (zzb < obj2.length) {
                        obj2[zzb] = com_google_android_gms_internal_zzfjj.zzcwn();
                        zzb++;
                    }
                    this.zzjmq = obj2;
                    com_google_android_gms_internal_zzfjj.zzkt(zzks);
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
        if (this.zzjmp != null && this.zzjmp.length > 0) {
            for (long zza : this.zzjmp) {
                com_google_android_gms_internal_zzfjk.zza(1, zza);
            }
        }
        if (this.zzjmq != null && this.zzjmq.length > 0) {
            while (i < this.zzjmq.length) {
                com_google_android_gms_internal_zzfjk.zza(2, this.zzjmq[i]);
                i++;
            }
        }
        super.zza(com_google_android_gms_internal_zzfjk);
    }

    protected final int zzq() {
        int i;
        int i2;
        int i3 = 0;
        int zzq = super.zzq();
        if (this.zzjmp == null || this.zzjmp.length <= 0) {
            i = zzq;
        } else {
            i2 = 0;
            for (long zzdi : this.zzjmp) {
                i2 += zzfjk.zzdi(zzdi);
            }
            i = (zzq + i2) + (this.zzjmp.length * 1);
        }
        if (this.zzjmq == null || this.zzjmq.length <= 0) {
            return i;
        }
        i2 = 0;
        while (i3 < this.zzjmq.length) {
            i2 += zzfjk.zzdi(this.zzjmq[i3]);
            i3++;
        }
        return (i + i2) + (this.zzjmq.length * 1);
    }
}

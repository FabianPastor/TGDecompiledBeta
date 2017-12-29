package com.google.android.gms.internal;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;
import org.telegram.tgnet.TLRPC;

public final class zzcme extends zzfjm<zzcme> {
    private static volatile zzcme[] zzjln;
    public String zzcn;
    public String zzdb;
    public Long zzfkk;
    public String zzifm;
    public String zzixs;
    public String zzixt;
    public String zzixw;
    public String zziya;
    public Integer zzjlo;
    public zzcmb[] zzjlp;
    public zzcmg[] zzjlq;
    public Long zzjlr;
    public Long zzjls;
    public Long zzjlt;
    public Long zzjlu;
    public Long zzjlv;
    public String zzjlw;
    public String zzjlx;
    public String zzjly;
    public Integer zzjlz;
    public Long zzjma;
    public Long zzjmb;
    public String zzjmc;
    public Boolean zzjmd;
    public String zzjme;
    public Long zzjmf;
    public Integer zzjmg;
    public Boolean zzjmh;
    public zzcma[] zzjmi;
    public Integer zzjmj;
    private Integer zzjmk;
    private Integer zzjml;
    public String zzjmm;
    public Long zzjmn;
    public String zzjmo;

    public zzcme() {
        this.zzjlo = null;
        this.zzjlp = zzcmb.zzbbh();
        this.zzjlq = zzcmg.zzbbk();
        this.zzjlr = null;
        this.zzjls = null;
        this.zzjlt = null;
        this.zzjlu = null;
        this.zzjlv = null;
        this.zzjlw = null;
        this.zzdb = null;
        this.zzjlx = null;
        this.zzjly = null;
        this.zzjlz = null;
        this.zzixt = null;
        this.zzcn = null;
        this.zzifm = null;
        this.zzjma = null;
        this.zzjmb = null;
        this.zzjmc = null;
        this.zzjmd = null;
        this.zzjme = null;
        this.zzjmf = null;
        this.zzjmg = null;
        this.zzixw = null;
        this.zzixs = null;
        this.zzjmh = null;
        this.zzjmi = zzcma.zzbbg();
        this.zziya = null;
        this.zzjmj = null;
        this.zzjmk = null;
        this.zzjml = null;
        this.zzjmm = null;
        this.zzjmn = null;
        this.zzfkk = null;
        this.zzjmo = null;
        this.zzpnc = null;
        this.zzpfd = -1;
    }

    public static zzcme[] zzbbj() {
        if (zzjln == null) {
            synchronized (zzfjq.zzpnk) {
                if (zzjln == null) {
                    zzjln = new zzcme[0];
                }
            }
        }
        return zzjln;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcme)) {
            return false;
        }
        zzcme com_google_android_gms_internal_zzcme = (zzcme) obj;
        if (this.zzjlo == null) {
            if (com_google_android_gms_internal_zzcme.zzjlo != null) {
                return false;
            }
        } else if (!this.zzjlo.equals(com_google_android_gms_internal_zzcme.zzjlo)) {
            return false;
        }
        if (!zzfjq.equals(this.zzjlp, com_google_android_gms_internal_zzcme.zzjlp)) {
            return false;
        }
        if (!zzfjq.equals(this.zzjlq, com_google_android_gms_internal_zzcme.zzjlq)) {
            return false;
        }
        if (this.zzjlr == null) {
            if (com_google_android_gms_internal_zzcme.zzjlr != null) {
                return false;
            }
        } else if (!this.zzjlr.equals(com_google_android_gms_internal_zzcme.zzjlr)) {
            return false;
        }
        if (this.zzjls == null) {
            if (com_google_android_gms_internal_zzcme.zzjls != null) {
                return false;
            }
        } else if (!this.zzjls.equals(com_google_android_gms_internal_zzcme.zzjls)) {
            return false;
        }
        if (this.zzjlt == null) {
            if (com_google_android_gms_internal_zzcme.zzjlt != null) {
                return false;
            }
        } else if (!this.zzjlt.equals(com_google_android_gms_internal_zzcme.zzjlt)) {
            return false;
        }
        if (this.zzjlu == null) {
            if (com_google_android_gms_internal_zzcme.zzjlu != null) {
                return false;
            }
        } else if (!this.zzjlu.equals(com_google_android_gms_internal_zzcme.zzjlu)) {
            return false;
        }
        if (this.zzjlv == null) {
            if (com_google_android_gms_internal_zzcme.zzjlv != null) {
                return false;
            }
        } else if (!this.zzjlv.equals(com_google_android_gms_internal_zzcme.zzjlv)) {
            return false;
        }
        if (this.zzjlw == null) {
            if (com_google_android_gms_internal_zzcme.zzjlw != null) {
                return false;
            }
        } else if (!this.zzjlw.equals(com_google_android_gms_internal_zzcme.zzjlw)) {
            return false;
        }
        if (this.zzdb == null) {
            if (com_google_android_gms_internal_zzcme.zzdb != null) {
                return false;
            }
        } else if (!this.zzdb.equals(com_google_android_gms_internal_zzcme.zzdb)) {
            return false;
        }
        if (this.zzjlx == null) {
            if (com_google_android_gms_internal_zzcme.zzjlx != null) {
                return false;
            }
        } else if (!this.zzjlx.equals(com_google_android_gms_internal_zzcme.zzjlx)) {
            return false;
        }
        if (this.zzjly == null) {
            if (com_google_android_gms_internal_zzcme.zzjly != null) {
                return false;
            }
        } else if (!this.zzjly.equals(com_google_android_gms_internal_zzcme.zzjly)) {
            return false;
        }
        if (this.zzjlz == null) {
            if (com_google_android_gms_internal_zzcme.zzjlz != null) {
                return false;
            }
        } else if (!this.zzjlz.equals(com_google_android_gms_internal_zzcme.zzjlz)) {
            return false;
        }
        if (this.zzixt == null) {
            if (com_google_android_gms_internal_zzcme.zzixt != null) {
                return false;
            }
        } else if (!this.zzixt.equals(com_google_android_gms_internal_zzcme.zzixt)) {
            return false;
        }
        if (this.zzcn == null) {
            if (com_google_android_gms_internal_zzcme.zzcn != null) {
                return false;
            }
        } else if (!this.zzcn.equals(com_google_android_gms_internal_zzcme.zzcn)) {
            return false;
        }
        if (this.zzifm == null) {
            if (com_google_android_gms_internal_zzcme.zzifm != null) {
                return false;
            }
        } else if (!this.zzifm.equals(com_google_android_gms_internal_zzcme.zzifm)) {
            return false;
        }
        if (this.zzjma == null) {
            if (com_google_android_gms_internal_zzcme.zzjma != null) {
                return false;
            }
        } else if (!this.zzjma.equals(com_google_android_gms_internal_zzcme.zzjma)) {
            return false;
        }
        if (this.zzjmb == null) {
            if (com_google_android_gms_internal_zzcme.zzjmb != null) {
                return false;
            }
        } else if (!this.zzjmb.equals(com_google_android_gms_internal_zzcme.zzjmb)) {
            return false;
        }
        if (this.zzjmc == null) {
            if (com_google_android_gms_internal_zzcme.zzjmc != null) {
                return false;
            }
        } else if (!this.zzjmc.equals(com_google_android_gms_internal_zzcme.zzjmc)) {
            return false;
        }
        if (this.zzjmd == null) {
            if (com_google_android_gms_internal_zzcme.zzjmd != null) {
                return false;
            }
        } else if (!this.zzjmd.equals(com_google_android_gms_internal_zzcme.zzjmd)) {
            return false;
        }
        if (this.zzjme == null) {
            if (com_google_android_gms_internal_zzcme.zzjme != null) {
                return false;
            }
        } else if (!this.zzjme.equals(com_google_android_gms_internal_zzcme.zzjme)) {
            return false;
        }
        if (this.zzjmf == null) {
            if (com_google_android_gms_internal_zzcme.zzjmf != null) {
                return false;
            }
        } else if (!this.zzjmf.equals(com_google_android_gms_internal_zzcme.zzjmf)) {
            return false;
        }
        if (this.zzjmg == null) {
            if (com_google_android_gms_internal_zzcme.zzjmg != null) {
                return false;
            }
        } else if (!this.zzjmg.equals(com_google_android_gms_internal_zzcme.zzjmg)) {
            return false;
        }
        if (this.zzixw == null) {
            if (com_google_android_gms_internal_zzcme.zzixw != null) {
                return false;
            }
        } else if (!this.zzixw.equals(com_google_android_gms_internal_zzcme.zzixw)) {
            return false;
        }
        if (this.zzixs == null) {
            if (com_google_android_gms_internal_zzcme.zzixs != null) {
                return false;
            }
        } else if (!this.zzixs.equals(com_google_android_gms_internal_zzcme.zzixs)) {
            return false;
        }
        if (this.zzjmh == null) {
            if (com_google_android_gms_internal_zzcme.zzjmh != null) {
                return false;
            }
        } else if (!this.zzjmh.equals(com_google_android_gms_internal_zzcme.zzjmh)) {
            return false;
        }
        if (!zzfjq.equals(this.zzjmi, com_google_android_gms_internal_zzcme.zzjmi)) {
            return false;
        }
        if (this.zziya == null) {
            if (com_google_android_gms_internal_zzcme.zziya != null) {
                return false;
            }
        } else if (!this.zziya.equals(com_google_android_gms_internal_zzcme.zziya)) {
            return false;
        }
        if (this.zzjmj == null) {
            if (com_google_android_gms_internal_zzcme.zzjmj != null) {
                return false;
            }
        } else if (!this.zzjmj.equals(com_google_android_gms_internal_zzcme.zzjmj)) {
            return false;
        }
        if (this.zzjmk == null) {
            if (com_google_android_gms_internal_zzcme.zzjmk != null) {
                return false;
            }
        } else if (!this.zzjmk.equals(com_google_android_gms_internal_zzcme.zzjmk)) {
            return false;
        }
        if (this.zzjml == null) {
            if (com_google_android_gms_internal_zzcme.zzjml != null) {
                return false;
            }
        } else if (!this.zzjml.equals(com_google_android_gms_internal_zzcme.zzjml)) {
            return false;
        }
        if (this.zzjmm == null) {
            if (com_google_android_gms_internal_zzcme.zzjmm != null) {
                return false;
            }
        } else if (!this.zzjmm.equals(com_google_android_gms_internal_zzcme.zzjmm)) {
            return false;
        }
        if (this.zzjmn == null) {
            if (com_google_android_gms_internal_zzcme.zzjmn != null) {
                return false;
            }
        } else if (!this.zzjmn.equals(com_google_android_gms_internal_zzcme.zzjmn)) {
            return false;
        }
        if (this.zzfkk == null) {
            if (com_google_android_gms_internal_zzcme.zzfkk != null) {
                return false;
            }
        } else if (!this.zzfkk.equals(com_google_android_gms_internal_zzcme.zzfkk)) {
            return false;
        }
        if (this.zzjmo == null) {
            if (com_google_android_gms_internal_zzcme.zzjmo != null) {
                return false;
            }
        } else if (!this.zzjmo.equals(com_google_android_gms_internal_zzcme.zzjmo)) {
            return false;
        }
        return (this.zzpnc == null || this.zzpnc.isEmpty()) ? com_google_android_gms_internal_zzcme.zzpnc == null || com_google_android_gms_internal_zzcme.zzpnc.isEmpty() : this.zzpnc.equals(com_google_android_gms_internal_zzcme.zzpnc);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzjmo == null ? 0 : this.zzjmo.hashCode()) + (((this.zzfkk == null ? 0 : this.zzfkk.hashCode()) + (((this.zzjmn == null ? 0 : this.zzjmn.hashCode()) + (((this.zzjmm == null ? 0 : this.zzjmm.hashCode()) + (((this.zzjml == null ? 0 : this.zzjml.hashCode()) + (((this.zzjmk == null ? 0 : this.zzjmk.hashCode()) + (((this.zzjmj == null ? 0 : this.zzjmj.hashCode()) + (((this.zziya == null ? 0 : this.zziya.hashCode()) + (((((this.zzjmh == null ? 0 : this.zzjmh.hashCode()) + (((this.zzixs == null ? 0 : this.zzixs.hashCode()) + (((this.zzixw == null ? 0 : this.zzixw.hashCode()) + (((this.zzjmg == null ? 0 : this.zzjmg.hashCode()) + (((this.zzjmf == null ? 0 : this.zzjmf.hashCode()) + (((this.zzjme == null ? 0 : this.zzjme.hashCode()) + (((this.zzjmd == null ? 0 : this.zzjmd.hashCode()) + (((this.zzjmc == null ? 0 : this.zzjmc.hashCode()) + (((this.zzjmb == null ? 0 : this.zzjmb.hashCode()) + (((this.zzjma == null ? 0 : this.zzjma.hashCode()) + (((this.zzifm == null ? 0 : this.zzifm.hashCode()) + (((this.zzcn == null ? 0 : this.zzcn.hashCode()) + (((this.zzixt == null ? 0 : this.zzixt.hashCode()) + (((this.zzjlz == null ? 0 : this.zzjlz.hashCode()) + (((this.zzjly == null ? 0 : this.zzjly.hashCode()) + (((this.zzjlx == null ? 0 : this.zzjlx.hashCode()) + (((this.zzdb == null ? 0 : this.zzdb.hashCode()) + (((this.zzjlw == null ? 0 : this.zzjlw.hashCode()) + (((this.zzjlv == null ? 0 : this.zzjlv.hashCode()) + (((this.zzjlu == null ? 0 : this.zzjlu.hashCode()) + (((this.zzjlt == null ? 0 : this.zzjlt.hashCode()) + (((this.zzjls == null ? 0 : this.zzjls.hashCode()) + (((this.zzjlr == null ? 0 : this.zzjlr.hashCode()) + (((((((this.zzjlo == null ? 0 : this.zzjlo.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31) + zzfjq.hashCode(this.zzjlp)) * 31) + zzfjq.hashCode(this.zzjlq)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31) + zzfjq.hashCode(this.zzjmi)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
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
                    this.zzjlo = Integer.valueOf(com_google_android_gms_internal_zzfjj.zzcwi());
                    continue;
                case 18:
                    zzb = zzfjv.zzb(com_google_android_gms_internal_zzfjj, 18);
                    zzcvt = this.zzjlp == null ? 0 : this.zzjlp.length;
                    obj = new zzcmb[(zzb + zzcvt)];
                    if (zzcvt != 0) {
                        System.arraycopy(this.zzjlp, 0, obj, 0, zzcvt);
                    }
                    while (zzcvt < obj.length - 1) {
                        obj[zzcvt] = new zzcmb();
                        com_google_android_gms_internal_zzfjj.zza(obj[zzcvt]);
                        com_google_android_gms_internal_zzfjj.zzcvt();
                        zzcvt++;
                    }
                    obj[zzcvt] = new zzcmb();
                    com_google_android_gms_internal_zzfjj.zza(obj[zzcvt]);
                    this.zzjlp = obj;
                    continue;
                case 26:
                    zzb = zzfjv.zzb(com_google_android_gms_internal_zzfjj, 26);
                    zzcvt = this.zzjlq == null ? 0 : this.zzjlq.length;
                    obj = new zzcmg[(zzb + zzcvt)];
                    if (zzcvt != 0) {
                        System.arraycopy(this.zzjlq, 0, obj, 0, zzcvt);
                    }
                    while (zzcvt < obj.length - 1) {
                        obj[zzcvt] = new zzcmg();
                        com_google_android_gms_internal_zzfjj.zza(obj[zzcvt]);
                        com_google_android_gms_internal_zzfjj.zzcvt();
                        zzcvt++;
                    }
                    obj[zzcvt] = new zzcmg();
                    com_google_android_gms_internal_zzfjj.zza(obj[zzcvt]);
                    this.zzjlq = obj;
                    continue;
                case 32:
                    this.zzjlr = Long.valueOf(com_google_android_gms_internal_zzfjj.zzcwn());
                    continue;
                case 40:
                    this.zzjls = Long.valueOf(com_google_android_gms_internal_zzfjj.zzcwn());
                    continue;
                case 48:
                    this.zzjlt = Long.valueOf(com_google_android_gms_internal_zzfjj.zzcwn());
                    continue;
                case 56:
                    this.zzjlv = Long.valueOf(com_google_android_gms_internal_zzfjj.zzcwn());
                    continue;
                case 66:
                    this.zzjlw = com_google_android_gms_internal_zzfjj.readString();
                    continue;
                case TLRPC.LAYER /*74*/:
                    this.zzdb = com_google_android_gms_internal_zzfjj.readString();
                    continue;
                case 82:
                    this.zzjlx = com_google_android_gms_internal_zzfjj.readString();
                    continue;
                case 90:
                    this.zzjly = com_google_android_gms_internal_zzfjj.readString();
                    continue;
                case 96:
                    this.zzjlz = Integer.valueOf(com_google_android_gms_internal_zzfjj.zzcwi());
                    continue;
                case 106:
                    this.zzixt = com_google_android_gms_internal_zzfjj.readString();
                    continue;
                case 114:
                    this.zzcn = com_google_android_gms_internal_zzfjj.readString();
                    continue;
                case TsExtractor.TS_STREAM_TYPE_HDMV_DTS /*130*/:
                    this.zzifm = com_google_android_gms_internal_zzfjj.readString();
                    continue;
                case 136:
                    this.zzjma = Long.valueOf(com_google_android_gms_internal_zzfjj.zzcwn());
                    continue;
                case 144:
                    this.zzjmb = Long.valueOf(com_google_android_gms_internal_zzfjj.zzcwn());
                    continue;
                case 154:
                    this.zzjmc = com_google_android_gms_internal_zzfjj.readString();
                    continue;
                case 160:
                    this.zzjmd = Boolean.valueOf(com_google_android_gms_internal_zzfjj.zzcvz());
                    continue;
                case 170:
                    this.zzjme = com_google_android_gms_internal_zzfjj.readString();
                    continue;
                case 176:
                    this.zzjmf = Long.valueOf(com_google_android_gms_internal_zzfjj.zzcwn());
                    continue;
                case 184:
                    this.zzjmg = Integer.valueOf(com_google_android_gms_internal_zzfjj.zzcwi());
                    continue;
                case 194:
                    this.zzixw = com_google_android_gms_internal_zzfjj.readString();
                    continue;
                case 202:
                    this.zzixs = com_google_android_gms_internal_zzfjj.readString();
                    continue;
                case 208:
                    this.zzjlu = Long.valueOf(com_google_android_gms_internal_zzfjj.zzcwn());
                    continue;
                case 224:
                    this.zzjmh = Boolean.valueOf(com_google_android_gms_internal_zzfjj.zzcvz());
                    continue;
                case 234:
                    zzb = zzfjv.zzb(com_google_android_gms_internal_zzfjj, 234);
                    zzcvt = this.zzjmi == null ? 0 : this.zzjmi.length;
                    obj = new zzcma[(zzb + zzcvt)];
                    if (zzcvt != 0) {
                        System.arraycopy(this.zzjmi, 0, obj, 0, zzcvt);
                    }
                    while (zzcvt < obj.length - 1) {
                        obj[zzcvt] = new zzcma();
                        com_google_android_gms_internal_zzfjj.zza(obj[zzcvt]);
                        com_google_android_gms_internal_zzfjj.zzcvt();
                        zzcvt++;
                    }
                    obj[zzcvt] = new zzcma();
                    com_google_android_gms_internal_zzfjj.zza(obj[zzcvt]);
                    this.zzjmi = obj;
                    continue;
                case 242:
                    this.zziya = com_google_android_gms_internal_zzfjj.readString();
                    continue;
                case 248:
                    this.zzjmj = Integer.valueOf(com_google_android_gms_internal_zzfjj.zzcwi());
                    continue;
                case 256:
                    this.zzjmk = Integer.valueOf(com_google_android_gms_internal_zzfjj.zzcwi());
                    continue;
                case 264:
                    this.zzjml = Integer.valueOf(com_google_android_gms_internal_zzfjj.zzcwi());
                    continue;
                case 274:
                    this.zzjmm = com_google_android_gms_internal_zzfjj.readString();
                    continue;
                case 280:
                    this.zzjmn = Long.valueOf(com_google_android_gms_internal_zzfjj.zzcwn());
                    continue;
                case 288:
                    this.zzfkk = Long.valueOf(com_google_android_gms_internal_zzfjj.zzcwn());
                    continue;
                case 298:
                    this.zzjmo = com_google_android_gms_internal_zzfjj.readString();
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
        if (this.zzjlo != null) {
            com_google_android_gms_internal_zzfjk.zzaa(1, this.zzjlo.intValue());
        }
        if (this.zzjlp != null && this.zzjlp.length > 0) {
            for (zzfjs com_google_android_gms_internal_zzfjs : this.zzjlp) {
                if (com_google_android_gms_internal_zzfjs != null) {
                    com_google_android_gms_internal_zzfjk.zza(2, com_google_android_gms_internal_zzfjs);
                }
            }
        }
        if (this.zzjlq != null && this.zzjlq.length > 0) {
            for (zzfjs com_google_android_gms_internal_zzfjs2 : this.zzjlq) {
                if (com_google_android_gms_internal_zzfjs2 != null) {
                    com_google_android_gms_internal_zzfjk.zza(3, com_google_android_gms_internal_zzfjs2);
                }
            }
        }
        if (this.zzjlr != null) {
            com_google_android_gms_internal_zzfjk.zzf(4, this.zzjlr.longValue());
        }
        if (this.zzjls != null) {
            com_google_android_gms_internal_zzfjk.zzf(5, this.zzjls.longValue());
        }
        if (this.zzjlt != null) {
            com_google_android_gms_internal_zzfjk.zzf(6, this.zzjlt.longValue());
        }
        if (this.zzjlv != null) {
            com_google_android_gms_internal_zzfjk.zzf(7, this.zzjlv.longValue());
        }
        if (this.zzjlw != null) {
            com_google_android_gms_internal_zzfjk.zzn(8, this.zzjlw);
        }
        if (this.zzdb != null) {
            com_google_android_gms_internal_zzfjk.zzn(9, this.zzdb);
        }
        if (this.zzjlx != null) {
            com_google_android_gms_internal_zzfjk.zzn(10, this.zzjlx);
        }
        if (this.zzjly != null) {
            com_google_android_gms_internal_zzfjk.zzn(11, this.zzjly);
        }
        if (this.zzjlz != null) {
            com_google_android_gms_internal_zzfjk.zzaa(12, this.zzjlz.intValue());
        }
        if (this.zzixt != null) {
            com_google_android_gms_internal_zzfjk.zzn(13, this.zzixt);
        }
        if (this.zzcn != null) {
            com_google_android_gms_internal_zzfjk.zzn(14, this.zzcn);
        }
        if (this.zzifm != null) {
            com_google_android_gms_internal_zzfjk.zzn(16, this.zzifm);
        }
        if (this.zzjma != null) {
            com_google_android_gms_internal_zzfjk.zzf(17, this.zzjma.longValue());
        }
        if (this.zzjmb != null) {
            com_google_android_gms_internal_zzfjk.zzf(18, this.zzjmb.longValue());
        }
        if (this.zzjmc != null) {
            com_google_android_gms_internal_zzfjk.zzn(19, this.zzjmc);
        }
        if (this.zzjmd != null) {
            com_google_android_gms_internal_zzfjk.zzl(20, this.zzjmd.booleanValue());
        }
        if (this.zzjme != null) {
            com_google_android_gms_internal_zzfjk.zzn(21, this.zzjme);
        }
        if (this.zzjmf != null) {
            com_google_android_gms_internal_zzfjk.zzf(22, this.zzjmf.longValue());
        }
        if (this.zzjmg != null) {
            com_google_android_gms_internal_zzfjk.zzaa(23, this.zzjmg.intValue());
        }
        if (this.zzixw != null) {
            com_google_android_gms_internal_zzfjk.zzn(24, this.zzixw);
        }
        if (this.zzixs != null) {
            com_google_android_gms_internal_zzfjk.zzn(25, this.zzixs);
        }
        if (this.zzjlu != null) {
            com_google_android_gms_internal_zzfjk.zzf(26, this.zzjlu.longValue());
        }
        if (this.zzjmh != null) {
            com_google_android_gms_internal_zzfjk.zzl(28, this.zzjmh.booleanValue());
        }
        if (this.zzjmi != null && this.zzjmi.length > 0) {
            while (i < this.zzjmi.length) {
                zzfjs com_google_android_gms_internal_zzfjs3 = this.zzjmi[i];
                if (com_google_android_gms_internal_zzfjs3 != null) {
                    com_google_android_gms_internal_zzfjk.zza(29, com_google_android_gms_internal_zzfjs3);
                }
                i++;
            }
        }
        if (this.zziya != null) {
            com_google_android_gms_internal_zzfjk.zzn(30, this.zziya);
        }
        if (this.zzjmj != null) {
            com_google_android_gms_internal_zzfjk.zzaa(31, this.zzjmj.intValue());
        }
        if (this.zzjmk != null) {
            com_google_android_gms_internal_zzfjk.zzaa(32, this.zzjmk.intValue());
        }
        if (this.zzjml != null) {
            com_google_android_gms_internal_zzfjk.zzaa(33, this.zzjml.intValue());
        }
        if (this.zzjmm != null) {
            com_google_android_gms_internal_zzfjk.zzn(34, this.zzjmm);
        }
        if (this.zzjmn != null) {
            com_google_android_gms_internal_zzfjk.zzf(35, this.zzjmn.longValue());
        }
        if (this.zzfkk != null) {
            com_google_android_gms_internal_zzfjk.zzf(36, this.zzfkk.longValue());
        }
        if (this.zzjmo != null) {
            com_google_android_gms_internal_zzfjk.zzn(37, this.zzjmo);
        }
        super.zza(com_google_android_gms_internal_zzfjk);
    }

    protected final int zzq() {
        int i;
        int i2 = 0;
        int zzq = super.zzq();
        if (this.zzjlo != null) {
            zzq += zzfjk.zzad(1, this.zzjlo.intValue());
        }
        if (this.zzjlp != null && this.zzjlp.length > 0) {
            i = zzq;
            for (zzfjs com_google_android_gms_internal_zzfjs : this.zzjlp) {
                if (com_google_android_gms_internal_zzfjs != null) {
                    i += zzfjk.zzb(2, com_google_android_gms_internal_zzfjs);
                }
            }
            zzq = i;
        }
        if (this.zzjlq != null && this.zzjlq.length > 0) {
            i = zzq;
            for (zzfjs com_google_android_gms_internal_zzfjs2 : this.zzjlq) {
                if (com_google_android_gms_internal_zzfjs2 != null) {
                    i += zzfjk.zzb(3, com_google_android_gms_internal_zzfjs2);
                }
            }
            zzq = i;
        }
        if (this.zzjlr != null) {
            zzq += zzfjk.zzc(4, this.zzjlr.longValue());
        }
        if (this.zzjls != null) {
            zzq += zzfjk.zzc(5, this.zzjls.longValue());
        }
        if (this.zzjlt != null) {
            zzq += zzfjk.zzc(6, this.zzjlt.longValue());
        }
        if (this.zzjlv != null) {
            zzq += zzfjk.zzc(7, this.zzjlv.longValue());
        }
        if (this.zzjlw != null) {
            zzq += zzfjk.zzo(8, this.zzjlw);
        }
        if (this.zzdb != null) {
            zzq += zzfjk.zzo(9, this.zzdb);
        }
        if (this.zzjlx != null) {
            zzq += zzfjk.zzo(10, this.zzjlx);
        }
        if (this.zzjly != null) {
            zzq += zzfjk.zzo(11, this.zzjly);
        }
        if (this.zzjlz != null) {
            zzq += zzfjk.zzad(12, this.zzjlz.intValue());
        }
        if (this.zzixt != null) {
            zzq += zzfjk.zzo(13, this.zzixt);
        }
        if (this.zzcn != null) {
            zzq += zzfjk.zzo(14, this.zzcn);
        }
        if (this.zzifm != null) {
            zzq += zzfjk.zzo(16, this.zzifm);
        }
        if (this.zzjma != null) {
            zzq += zzfjk.zzc(17, this.zzjma.longValue());
        }
        if (this.zzjmb != null) {
            zzq += zzfjk.zzc(18, this.zzjmb.longValue());
        }
        if (this.zzjmc != null) {
            zzq += zzfjk.zzo(19, this.zzjmc);
        }
        if (this.zzjmd != null) {
            this.zzjmd.booleanValue();
            zzq += zzfjk.zzlg(20) + 1;
        }
        if (this.zzjme != null) {
            zzq += zzfjk.zzo(21, this.zzjme);
        }
        if (this.zzjmf != null) {
            zzq += zzfjk.zzc(22, this.zzjmf.longValue());
        }
        if (this.zzjmg != null) {
            zzq += zzfjk.zzad(23, this.zzjmg.intValue());
        }
        if (this.zzixw != null) {
            zzq += zzfjk.zzo(24, this.zzixw);
        }
        if (this.zzixs != null) {
            zzq += zzfjk.zzo(25, this.zzixs);
        }
        if (this.zzjlu != null) {
            zzq += zzfjk.zzc(26, this.zzjlu.longValue());
        }
        if (this.zzjmh != null) {
            this.zzjmh.booleanValue();
            zzq += zzfjk.zzlg(28) + 1;
        }
        if (this.zzjmi != null && this.zzjmi.length > 0) {
            while (i2 < this.zzjmi.length) {
                zzfjs com_google_android_gms_internal_zzfjs3 = this.zzjmi[i2];
                if (com_google_android_gms_internal_zzfjs3 != null) {
                    zzq += zzfjk.zzb(29, com_google_android_gms_internal_zzfjs3);
                }
                i2++;
            }
        }
        if (this.zziya != null) {
            zzq += zzfjk.zzo(30, this.zziya);
        }
        if (this.zzjmj != null) {
            zzq += zzfjk.zzad(31, this.zzjmj.intValue());
        }
        if (this.zzjmk != null) {
            zzq += zzfjk.zzad(32, this.zzjmk.intValue());
        }
        if (this.zzjml != null) {
            zzq += zzfjk.zzad(33, this.zzjml.intValue());
        }
        if (this.zzjmm != null) {
            zzq += zzfjk.zzo(34, this.zzjmm);
        }
        if (this.zzjmn != null) {
            zzq += zzfjk.zzc(35, this.zzjmn.longValue());
        }
        if (this.zzfkk != null) {
            zzq += zzfjk.zzc(36, this.zzfkk.longValue());
        }
        return this.zzjmo != null ? zzq + zzfjk.zzo(37, this.zzjmo) : zzq;
    }
}

package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;

public final class zzcjz extends adj<zzcjz> {
    private static volatile zzcjz[] zzbvC;
    public String zzaH;
    public String zzaY;
    public String zzbgW;
    public String zzboQ;
    public String zzboR;
    public String zzboU;
    public String zzboY;
    public Integer zzbvD;
    public zzcjw[] zzbvE;
    public zzckb[] zzbvF;
    public Long zzbvG;
    public Long zzbvH;
    public Long zzbvI;
    public Long zzbvJ;
    public Long zzbvK;
    public String zzbvL;
    public String zzbvM;
    public String zzbvN;
    public Integer zzbvO;
    public Long zzbvP;
    public Long zzbvQ;
    public String zzbvR;
    public Boolean zzbvS;
    public String zzbvT;
    public Long zzbvU;
    public Integer zzbvV;
    public Boolean zzbvW;
    public zzcjv[] zzbvX;
    public Integer zzbvY;
    private Integer zzbvZ;
    private Integer zzbwa;
    public Long zzbwb;
    public Long zzbwc;
    public String zzbwd;

    public zzcjz() {
        this.zzbvD = null;
        this.zzbvE = zzcjw.zzzB();
        this.zzbvF = zzckb.zzzE();
        this.zzbvG = null;
        this.zzbvH = null;
        this.zzbvI = null;
        this.zzbvJ = null;
        this.zzbvK = null;
        this.zzbvL = null;
        this.zzaY = null;
        this.zzbvM = null;
        this.zzbvN = null;
        this.zzbvO = null;
        this.zzboR = null;
        this.zzaH = null;
        this.zzbgW = null;
        this.zzbvP = null;
        this.zzbvQ = null;
        this.zzbvR = null;
        this.zzbvS = null;
        this.zzbvT = null;
        this.zzbvU = null;
        this.zzbvV = null;
        this.zzboU = null;
        this.zzboQ = null;
        this.zzbvW = null;
        this.zzbvX = zzcjv.zzzA();
        this.zzboY = null;
        this.zzbvY = null;
        this.zzbvZ = null;
        this.zzbwa = null;
        this.zzbwb = null;
        this.zzbwc = null;
        this.zzbwd = null;
        this.zzcso = null;
        this.zzcsx = -1;
    }

    public static zzcjz[] zzzD() {
        if (zzbvC == null) {
            synchronized (adn.zzcsw) {
                if (zzbvC == null) {
                    zzbvC = new zzcjz[0];
                }
            }
        }
        return zzbvC;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjz)) {
            return false;
        }
        zzcjz com_google_android_gms_internal_zzcjz = (zzcjz) obj;
        if (this.zzbvD == null) {
            if (com_google_android_gms_internal_zzcjz.zzbvD != null) {
                return false;
            }
        } else if (!this.zzbvD.equals(com_google_android_gms_internal_zzcjz.zzbvD)) {
            return false;
        }
        if (!adn.equals(this.zzbvE, com_google_android_gms_internal_zzcjz.zzbvE)) {
            return false;
        }
        if (!adn.equals(this.zzbvF, com_google_android_gms_internal_zzcjz.zzbvF)) {
            return false;
        }
        if (this.zzbvG == null) {
            if (com_google_android_gms_internal_zzcjz.zzbvG != null) {
                return false;
            }
        } else if (!this.zzbvG.equals(com_google_android_gms_internal_zzcjz.zzbvG)) {
            return false;
        }
        if (this.zzbvH == null) {
            if (com_google_android_gms_internal_zzcjz.zzbvH != null) {
                return false;
            }
        } else if (!this.zzbvH.equals(com_google_android_gms_internal_zzcjz.zzbvH)) {
            return false;
        }
        if (this.zzbvI == null) {
            if (com_google_android_gms_internal_zzcjz.zzbvI != null) {
                return false;
            }
        } else if (!this.zzbvI.equals(com_google_android_gms_internal_zzcjz.zzbvI)) {
            return false;
        }
        if (this.zzbvJ == null) {
            if (com_google_android_gms_internal_zzcjz.zzbvJ != null) {
                return false;
            }
        } else if (!this.zzbvJ.equals(com_google_android_gms_internal_zzcjz.zzbvJ)) {
            return false;
        }
        if (this.zzbvK == null) {
            if (com_google_android_gms_internal_zzcjz.zzbvK != null) {
                return false;
            }
        } else if (!this.zzbvK.equals(com_google_android_gms_internal_zzcjz.zzbvK)) {
            return false;
        }
        if (this.zzbvL == null) {
            if (com_google_android_gms_internal_zzcjz.zzbvL != null) {
                return false;
            }
        } else if (!this.zzbvL.equals(com_google_android_gms_internal_zzcjz.zzbvL)) {
            return false;
        }
        if (this.zzaY == null) {
            if (com_google_android_gms_internal_zzcjz.zzaY != null) {
                return false;
            }
        } else if (!this.zzaY.equals(com_google_android_gms_internal_zzcjz.zzaY)) {
            return false;
        }
        if (this.zzbvM == null) {
            if (com_google_android_gms_internal_zzcjz.zzbvM != null) {
                return false;
            }
        } else if (!this.zzbvM.equals(com_google_android_gms_internal_zzcjz.zzbvM)) {
            return false;
        }
        if (this.zzbvN == null) {
            if (com_google_android_gms_internal_zzcjz.zzbvN != null) {
                return false;
            }
        } else if (!this.zzbvN.equals(com_google_android_gms_internal_zzcjz.zzbvN)) {
            return false;
        }
        if (this.zzbvO == null) {
            if (com_google_android_gms_internal_zzcjz.zzbvO != null) {
                return false;
            }
        } else if (!this.zzbvO.equals(com_google_android_gms_internal_zzcjz.zzbvO)) {
            return false;
        }
        if (this.zzboR == null) {
            if (com_google_android_gms_internal_zzcjz.zzboR != null) {
                return false;
            }
        } else if (!this.zzboR.equals(com_google_android_gms_internal_zzcjz.zzboR)) {
            return false;
        }
        if (this.zzaH == null) {
            if (com_google_android_gms_internal_zzcjz.zzaH != null) {
                return false;
            }
        } else if (!this.zzaH.equals(com_google_android_gms_internal_zzcjz.zzaH)) {
            return false;
        }
        if (this.zzbgW == null) {
            if (com_google_android_gms_internal_zzcjz.zzbgW != null) {
                return false;
            }
        } else if (!this.zzbgW.equals(com_google_android_gms_internal_zzcjz.zzbgW)) {
            return false;
        }
        if (this.zzbvP == null) {
            if (com_google_android_gms_internal_zzcjz.zzbvP != null) {
                return false;
            }
        } else if (!this.zzbvP.equals(com_google_android_gms_internal_zzcjz.zzbvP)) {
            return false;
        }
        if (this.zzbvQ == null) {
            if (com_google_android_gms_internal_zzcjz.zzbvQ != null) {
                return false;
            }
        } else if (!this.zzbvQ.equals(com_google_android_gms_internal_zzcjz.zzbvQ)) {
            return false;
        }
        if (this.zzbvR == null) {
            if (com_google_android_gms_internal_zzcjz.zzbvR != null) {
                return false;
            }
        } else if (!this.zzbvR.equals(com_google_android_gms_internal_zzcjz.zzbvR)) {
            return false;
        }
        if (this.zzbvS == null) {
            if (com_google_android_gms_internal_zzcjz.zzbvS != null) {
                return false;
            }
        } else if (!this.zzbvS.equals(com_google_android_gms_internal_zzcjz.zzbvS)) {
            return false;
        }
        if (this.zzbvT == null) {
            if (com_google_android_gms_internal_zzcjz.zzbvT != null) {
                return false;
            }
        } else if (!this.zzbvT.equals(com_google_android_gms_internal_zzcjz.zzbvT)) {
            return false;
        }
        if (this.zzbvU == null) {
            if (com_google_android_gms_internal_zzcjz.zzbvU != null) {
                return false;
            }
        } else if (!this.zzbvU.equals(com_google_android_gms_internal_zzcjz.zzbvU)) {
            return false;
        }
        if (this.zzbvV == null) {
            if (com_google_android_gms_internal_zzcjz.zzbvV != null) {
                return false;
            }
        } else if (!this.zzbvV.equals(com_google_android_gms_internal_zzcjz.zzbvV)) {
            return false;
        }
        if (this.zzboU == null) {
            if (com_google_android_gms_internal_zzcjz.zzboU != null) {
                return false;
            }
        } else if (!this.zzboU.equals(com_google_android_gms_internal_zzcjz.zzboU)) {
            return false;
        }
        if (this.zzboQ == null) {
            if (com_google_android_gms_internal_zzcjz.zzboQ != null) {
                return false;
            }
        } else if (!this.zzboQ.equals(com_google_android_gms_internal_zzcjz.zzboQ)) {
            return false;
        }
        if (this.zzbvW == null) {
            if (com_google_android_gms_internal_zzcjz.zzbvW != null) {
                return false;
            }
        } else if (!this.zzbvW.equals(com_google_android_gms_internal_zzcjz.zzbvW)) {
            return false;
        }
        if (!adn.equals(this.zzbvX, com_google_android_gms_internal_zzcjz.zzbvX)) {
            return false;
        }
        if (this.zzboY == null) {
            if (com_google_android_gms_internal_zzcjz.zzboY != null) {
                return false;
            }
        } else if (!this.zzboY.equals(com_google_android_gms_internal_zzcjz.zzboY)) {
            return false;
        }
        if (this.zzbvY == null) {
            if (com_google_android_gms_internal_zzcjz.zzbvY != null) {
                return false;
            }
        } else if (!this.zzbvY.equals(com_google_android_gms_internal_zzcjz.zzbvY)) {
            return false;
        }
        if (this.zzbvZ == null) {
            if (com_google_android_gms_internal_zzcjz.zzbvZ != null) {
                return false;
            }
        } else if (!this.zzbvZ.equals(com_google_android_gms_internal_zzcjz.zzbvZ)) {
            return false;
        }
        if (this.zzbwa == null) {
            if (com_google_android_gms_internal_zzcjz.zzbwa != null) {
                return false;
            }
        } else if (!this.zzbwa.equals(com_google_android_gms_internal_zzcjz.zzbwa)) {
            return false;
        }
        if (this.zzbwb == null) {
            if (com_google_android_gms_internal_zzcjz.zzbwb != null) {
                return false;
            }
        } else if (!this.zzbwb.equals(com_google_android_gms_internal_zzcjz.zzbwb)) {
            return false;
        }
        if (this.zzbwc == null) {
            if (com_google_android_gms_internal_zzcjz.zzbwc != null) {
                return false;
            }
        } else if (!this.zzbwc.equals(com_google_android_gms_internal_zzcjz.zzbwc)) {
            return false;
        }
        if (this.zzbwd == null) {
            if (com_google_android_gms_internal_zzcjz.zzbwd != null) {
                return false;
            }
        } else if (!this.zzbwd.equals(com_google_android_gms_internal_zzcjz.zzbwd)) {
            return false;
        }
        return (this.zzcso == null || this.zzcso.isEmpty()) ? com_google_android_gms_internal_zzcjz.zzcso == null || com_google_android_gms_internal_zzcjz.zzcso.isEmpty() : this.zzcso.equals(com_google_android_gms_internal_zzcjz.zzcso);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzbwd == null ? 0 : this.zzbwd.hashCode()) + (((this.zzbwc == null ? 0 : this.zzbwc.hashCode()) + (((this.zzbwb == null ? 0 : this.zzbwb.hashCode()) + (((this.zzbwa == null ? 0 : this.zzbwa.hashCode()) + (((this.zzbvZ == null ? 0 : this.zzbvZ.hashCode()) + (((this.zzbvY == null ? 0 : this.zzbvY.hashCode()) + (((this.zzboY == null ? 0 : this.zzboY.hashCode()) + (((((this.zzbvW == null ? 0 : this.zzbvW.hashCode()) + (((this.zzboQ == null ? 0 : this.zzboQ.hashCode()) + (((this.zzboU == null ? 0 : this.zzboU.hashCode()) + (((this.zzbvV == null ? 0 : this.zzbvV.hashCode()) + (((this.zzbvU == null ? 0 : this.zzbvU.hashCode()) + (((this.zzbvT == null ? 0 : this.zzbvT.hashCode()) + (((this.zzbvS == null ? 0 : this.zzbvS.hashCode()) + (((this.zzbvR == null ? 0 : this.zzbvR.hashCode()) + (((this.zzbvQ == null ? 0 : this.zzbvQ.hashCode()) + (((this.zzbvP == null ? 0 : this.zzbvP.hashCode()) + (((this.zzbgW == null ? 0 : this.zzbgW.hashCode()) + (((this.zzaH == null ? 0 : this.zzaH.hashCode()) + (((this.zzboR == null ? 0 : this.zzboR.hashCode()) + (((this.zzbvO == null ? 0 : this.zzbvO.hashCode()) + (((this.zzbvN == null ? 0 : this.zzbvN.hashCode()) + (((this.zzbvM == null ? 0 : this.zzbvM.hashCode()) + (((this.zzaY == null ? 0 : this.zzaY.hashCode()) + (((this.zzbvL == null ? 0 : this.zzbvL.hashCode()) + (((this.zzbvK == null ? 0 : this.zzbvK.hashCode()) + (((this.zzbvJ == null ? 0 : this.zzbvJ.hashCode()) + (((this.zzbvI == null ? 0 : this.zzbvI.hashCode()) + (((this.zzbvH == null ? 0 : this.zzbvH.hashCode()) + (((this.zzbvG == null ? 0 : this.zzbvG.hashCode()) + (((((((this.zzbvD == null ? 0 : this.zzbvD.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31) + adn.hashCode(this.zzbvE)) * 31) + adn.hashCode(this.zzbvF)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31) + adn.hashCode(this.zzbvX)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
        if (!(this.zzcso == null || this.zzcso.isEmpty())) {
            i = this.zzcso.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ adp zza(adg com_google_android_gms_internal_adg) throws IOException {
        while (true) {
            int zzLA = com_google_android_gms_internal_adg.zzLA();
            int zzb;
            Object obj;
            switch (zzLA) {
                case 0:
                    break;
                case 8:
                    this.zzbvD = Integer.valueOf(com_google_android_gms_internal_adg.zzLF());
                    continue;
                case 18:
                    zzb = ads.zzb(com_google_android_gms_internal_adg, 18);
                    zzLA = this.zzbvE == null ? 0 : this.zzbvE.length;
                    obj = new zzcjw[(zzb + zzLA)];
                    if (zzLA != 0) {
                        System.arraycopy(this.zzbvE, 0, obj, 0, zzLA);
                    }
                    while (zzLA < obj.length - 1) {
                        obj[zzLA] = new zzcjw();
                        com_google_android_gms_internal_adg.zza(obj[zzLA]);
                        com_google_android_gms_internal_adg.zzLA();
                        zzLA++;
                    }
                    obj[zzLA] = new zzcjw();
                    com_google_android_gms_internal_adg.zza(obj[zzLA]);
                    this.zzbvE = obj;
                    continue;
                case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                    zzb = ads.zzb(com_google_android_gms_internal_adg, 26);
                    zzLA = this.zzbvF == null ? 0 : this.zzbvF.length;
                    obj = new zzckb[(zzb + zzLA)];
                    if (zzLA != 0) {
                        System.arraycopy(this.zzbvF, 0, obj, 0, zzLA);
                    }
                    while (zzLA < obj.length - 1) {
                        obj[zzLA] = new zzckb();
                        com_google_android_gms_internal_adg.zza(obj[zzLA]);
                        com_google_android_gms_internal_adg.zzLA();
                        zzLA++;
                    }
                    obj[zzLA] = new zzckb();
                    com_google_android_gms_internal_adg.zza(obj[zzLA]);
                    this.zzbvF = obj;
                    continue;
                case 32:
                    this.zzbvG = Long.valueOf(com_google_android_gms_internal_adg.zzLG());
                    continue;
                case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                    this.zzbvH = Long.valueOf(com_google_android_gms_internal_adg.zzLG());
                    continue;
                case 48:
                    this.zzbvI = Long.valueOf(com_google_android_gms_internal_adg.zzLG());
                    continue;
                case 56:
                    this.zzbvK = Long.valueOf(com_google_android_gms_internal_adg.zzLG());
                    continue;
                case 66:
                    this.zzbvL = com_google_android_gms_internal_adg.readString();
                    continue;
                case 74:
                    this.zzaY = com_google_android_gms_internal_adg.readString();
                    continue;
                case 82:
                    this.zzbvM = com_google_android_gms_internal_adg.readString();
                    continue;
                case 90:
                    this.zzbvN = com_google_android_gms_internal_adg.readString();
                    continue;
                case 96:
                    this.zzbvO = Integer.valueOf(com_google_android_gms_internal_adg.zzLF());
                    continue;
                case 106:
                    this.zzboR = com_google_android_gms_internal_adg.readString();
                    continue;
                case 114:
                    this.zzaH = com_google_android_gms_internal_adg.readString();
                    continue;
                case TsExtractor.TS_STREAM_TYPE_HDMV_DTS /*130*/:
                    this.zzbgW = com_google_android_gms_internal_adg.readString();
                    continue;
                case 136:
                    this.zzbvP = Long.valueOf(com_google_android_gms_internal_adg.zzLG());
                    continue;
                case 144:
                    this.zzbvQ = Long.valueOf(com_google_android_gms_internal_adg.zzLG());
                    continue;
                case 154:
                    this.zzbvR = com_google_android_gms_internal_adg.readString();
                    continue;
                case 160:
                    this.zzbvS = Boolean.valueOf(com_google_android_gms_internal_adg.zzLD());
                    continue;
                case 170:
                    this.zzbvT = com_google_android_gms_internal_adg.readString();
                    continue;
                case 176:
                    this.zzbvU = Long.valueOf(com_google_android_gms_internal_adg.zzLG());
                    continue;
                case 184:
                    this.zzbvV = Integer.valueOf(com_google_android_gms_internal_adg.zzLF());
                    continue;
                case 194:
                    this.zzboU = com_google_android_gms_internal_adg.readString();
                    continue;
                case 202:
                    this.zzboQ = com_google_android_gms_internal_adg.readString();
                    continue;
                case 208:
                    this.zzbvJ = Long.valueOf(com_google_android_gms_internal_adg.zzLG());
                    continue;
                case 224:
                    this.zzbvW = Boolean.valueOf(com_google_android_gms_internal_adg.zzLD());
                    continue;
                case 234:
                    zzb = ads.zzb(com_google_android_gms_internal_adg, 234);
                    zzLA = this.zzbvX == null ? 0 : this.zzbvX.length;
                    obj = new zzcjv[(zzb + zzLA)];
                    if (zzLA != 0) {
                        System.arraycopy(this.zzbvX, 0, obj, 0, zzLA);
                    }
                    while (zzLA < obj.length - 1) {
                        obj[zzLA] = new zzcjv();
                        com_google_android_gms_internal_adg.zza(obj[zzLA]);
                        com_google_android_gms_internal_adg.zzLA();
                        zzLA++;
                    }
                    obj[zzLA] = new zzcjv();
                    com_google_android_gms_internal_adg.zza(obj[zzLA]);
                    this.zzbvX = obj;
                    continue;
                case 242:
                    this.zzboY = com_google_android_gms_internal_adg.readString();
                    continue;
                case 248:
                    this.zzbvY = Integer.valueOf(com_google_android_gms_internal_adg.zzLF());
                    continue;
                case 256:
                    this.zzbvZ = Integer.valueOf(com_google_android_gms_internal_adg.zzLF());
                    continue;
                case 264:
                    this.zzbwa = Integer.valueOf(com_google_android_gms_internal_adg.zzLF());
                    continue;
                case 280:
                    this.zzbwb = Long.valueOf(com_google_android_gms_internal_adg.zzLG());
                    continue;
                case 288:
                    this.zzbwc = Long.valueOf(com_google_android_gms_internal_adg.zzLG());
                    continue;
                case 298:
                    this.zzbwd = com_google_android_gms_internal_adg.readString();
                    continue;
                default:
                    if (!super.zza(com_google_android_gms_internal_adg, zzLA)) {
                        break;
                    }
                    continue;
            }
            return this;
        }
    }

    public final void zza(adh com_google_android_gms_internal_adh) throws IOException {
        int i = 0;
        if (this.zzbvD != null) {
            com_google_android_gms_internal_adh.zzr(1, this.zzbvD.intValue());
        }
        if (this.zzbvE != null && this.zzbvE.length > 0) {
            for (adp com_google_android_gms_internal_adp : this.zzbvE) {
                if (com_google_android_gms_internal_adp != null) {
                    com_google_android_gms_internal_adh.zza(2, com_google_android_gms_internal_adp);
                }
            }
        }
        if (this.zzbvF != null && this.zzbvF.length > 0) {
            for (adp com_google_android_gms_internal_adp2 : this.zzbvF) {
                if (com_google_android_gms_internal_adp2 != null) {
                    com_google_android_gms_internal_adh.zza(3, com_google_android_gms_internal_adp2);
                }
            }
        }
        if (this.zzbvG != null) {
            com_google_android_gms_internal_adh.zzb(4, this.zzbvG.longValue());
        }
        if (this.zzbvH != null) {
            com_google_android_gms_internal_adh.zzb(5, this.zzbvH.longValue());
        }
        if (this.zzbvI != null) {
            com_google_android_gms_internal_adh.zzb(6, this.zzbvI.longValue());
        }
        if (this.zzbvK != null) {
            com_google_android_gms_internal_adh.zzb(7, this.zzbvK.longValue());
        }
        if (this.zzbvL != null) {
            com_google_android_gms_internal_adh.zzl(8, this.zzbvL);
        }
        if (this.zzaY != null) {
            com_google_android_gms_internal_adh.zzl(9, this.zzaY);
        }
        if (this.zzbvM != null) {
            com_google_android_gms_internal_adh.zzl(10, this.zzbvM);
        }
        if (this.zzbvN != null) {
            com_google_android_gms_internal_adh.zzl(11, this.zzbvN);
        }
        if (this.zzbvO != null) {
            com_google_android_gms_internal_adh.zzr(12, this.zzbvO.intValue());
        }
        if (this.zzboR != null) {
            com_google_android_gms_internal_adh.zzl(13, this.zzboR);
        }
        if (this.zzaH != null) {
            com_google_android_gms_internal_adh.zzl(14, this.zzaH);
        }
        if (this.zzbgW != null) {
            com_google_android_gms_internal_adh.zzl(16, this.zzbgW);
        }
        if (this.zzbvP != null) {
            com_google_android_gms_internal_adh.zzb(17, this.zzbvP.longValue());
        }
        if (this.zzbvQ != null) {
            com_google_android_gms_internal_adh.zzb(18, this.zzbvQ.longValue());
        }
        if (this.zzbvR != null) {
            com_google_android_gms_internal_adh.zzl(19, this.zzbvR);
        }
        if (this.zzbvS != null) {
            com_google_android_gms_internal_adh.zzk(20, this.zzbvS.booleanValue());
        }
        if (this.zzbvT != null) {
            com_google_android_gms_internal_adh.zzl(21, this.zzbvT);
        }
        if (this.zzbvU != null) {
            com_google_android_gms_internal_adh.zzb(22, this.zzbvU.longValue());
        }
        if (this.zzbvV != null) {
            com_google_android_gms_internal_adh.zzr(23, this.zzbvV.intValue());
        }
        if (this.zzboU != null) {
            com_google_android_gms_internal_adh.zzl(24, this.zzboU);
        }
        if (this.zzboQ != null) {
            com_google_android_gms_internal_adh.zzl(25, this.zzboQ);
        }
        if (this.zzbvJ != null) {
            com_google_android_gms_internal_adh.zzb(26, this.zzbvJ.longValue());
        }
        if (this.zzbvW != null) {
            com_google_android_gms_internal_adh.zzk(28, this.zzbvW.booleanValue());
        }
        if (this.zzbvX != null && this.zzbvX.length > 0) {
            while (i < this.zzbvX.length) {
                adp com_google_android_gms_internal_adp3 = this.zzbvX[i];
                if (com_google_android_gms_internal_adp3 != null) {
                    com_google_android_gms_internal_adh.zza(29, com_google_android_gms_internal_adp3);
                }
                i++;
            }
        }
        if (this.zzboY != null) {
            com_google_android_gms_internal_adh.zzl(30, this.zzboY);
        }
        if (this.zzbvY != null) {
            com_google_android_gms_internal_adh.zzr(31, this.zzbvY.intValue());
        }
        if (this.zzbvZ != null) {
            com_google_android_gms_internal_adh.zzr(32, this.zzbvZ.intValue());
        }
        if (this.zzbwa != null) {
            com_google_android_gms_internal_adh.zzr(33, this.zzbwa.intValue());
        }
        if (this.zzbwb != null) {
            com_google_android_gms_internal_adh.zzb(35, this.zzbwb.longValue());
        }
        if (this.zzbwc != null) {
            com_google_android_gms_internal_adh.zzb(36, this.zzbwc.longValue());
        }
        if (this.zzbwd != null) {
            com_google_android_gms_internal_adh.zzl(37, this.zzbwd);
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int i;
        int i2 = 0;
        int zzn = super.zzn();
        if (this.zzbvD != null) {
            zzn += adh.zzs(1, this.zzbvD.intValue());
        }
        if (this.zzbvE != null && this.zzbvE.length > 0) {
            i = zzn;
            for (adp com_google_android_gms_internal_adp : this.zzbvE) {
                if (com_google_android_gms_internal_adp != null) {
                    i += adh.zzb(2, com_google_android_gms_internal_adp);
                }
            }
            zzn = i;
        }
        if (this.zzbvF != null && this.zzbvF.length > 0) {
            i = zzn;
            for (adp com_google_android_gms_internal_adp2 : this.zzbvF) {
                if (com_google_android_gms_internal_adp2 != null) {
                    i += adh.zzb(3, com_google_android_gms_internal_adp2);
                }
            }
            zzn = i;
        }
        if (this.zzbvG != null) {
            zzn += adh.zze(4, this.zzbvG.longValue());
        }
        if (this.zzbvH != null) {
            zzn += adh.zze(5, this.zzbvH.longValue());
        }
        if (this.zzbvI != null) {
            zzn += adh.zze(6, this.zzbvI.longValue());
        }
        if (this.zzbvK != null) {
            zzn += adh.zze(7, this.zzbvK.longValue());
        }
        if (this.zzbvL != null) {
            zzn += adh.zzm(8, this.zzbvL);
        }
        if (this.zzaY != null) {
            zzn += adh.zzm(9, this.zzaY);
        }
        if (this.zzbvM != null) {
            zzn += adh.zzm(10, this.zzbvM);
        }
        if (this.zzbvN != null) {
            zzn += adh.zzm(11, this.zzbvN);
        }
        if (this.zzbvO != null) {
            zzn += adh.zzs(12, this.zzbvO.intValue());
        }
        if (this.zzboR != null) {
            zzn += adh.zzm(13, this.zzboR);
        }
        if (this.zzaH != null) {
            zzn += adh.zzm(14, this.zzaH);
        }
        if (this.zzbgW != null) {
            zzn += adh.zzm(16, this.zzbgW);
        }
        if (this.zzbvP != null) {
            zzn += adh.zze(17, this.zzbvP.longValue());
        }
        if (this.zzbvQ != null) {
            zzn += adh.zze(18, this.zzbvQ.longValue());
        }
        if (this.zzbvR != null) {
            zzn += adh.zzm(19, this.zzbvR);
        }
        if (this.zzbvS != null) {
            this.zzbvS.booleanValue();
            zzn += adh.zzct(20) + 1;
        }
        if (this.zzbvT != null) {
            zzn += adh.zzm(21, this.zzbvT);
        }
        if (this.zzbvU != null) {
            zzn += adh.zze(22, this.zzbvU.longValue());
        }
        if (this.zzbvV != null) {
            zzn += adh.zzs(23, this.zzbvV.intValue());
        }
        if (this.zzboU != null) {
            zzn += adh.zzm(24, this.zzboU);
        }
        if (this.zzboQ != null) {
            zzn += adh.zzm(25, this.zzboQ);
        }
        if (this.zzbvJ != null) {
            zzn += adh.zze(26, this.zzbvJ.longValue());
        }
        if (this.zzbvW != null) {
            this.zzbvW.booleanValue();
            zzn += adh.zzct(28) + 1;
        }
        if (this.zzbvX != null && this.zzbvX.length > 0) {
            while (i2 < this.zzbvX.length) {
                adp com_google_android_gms_internal_adp3 = this.zzbvX[i2];
                if (com_google_android_gms_internal_adp3 != null) {
                    zzn += adh.zzb(29, com_google_android_gms_internal_adp3);
                }
                i2++;
            }
        }
        if (this.zzboY != null) {
            zzn += adh.zzm(30, this.zzboY);
        }
        if (this.zzbvY != null) {
            zzn += adh.zzs(31, this.zzbvY.intValue());
        }
        if (this.zzbvZ != null) {
            zzn += adh.zzs(32, this.zzbvZ.intValue());
        }
        if (this.zzbwa != null) {
            zzn += adh.zzs(33, this.zzbwa.intValue());
        }
        if (this.zzbwb != null) {
            zzn += adh.zze(35, this.zzbwb.longValue());
        }
        if (this.zzbwc != null) {
            zzn += adh.zze(36, this.zzbwc.longValue());
        }
        return this.zzbwd != null ? zzn + adh.zzm(37, this.zzbwd) : zzn;
    }
}

package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;

public final class zzcjy extends ada<zzcjy> {
    private static volatile zzcjy[] zzbvC;
    public String zzaJ;
    public String zzba;
    public String zzbgW;
    public String zzboQ;
    public String zzboR;
    public String zzboU;
    public String zzboY;
    public Integer zzbvD;
    public zzcjv[] zzbvE;
    public zzcka[] zzbvF;
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
    public zzcju[] zzbvX;
    public Integer zzbvY;
    private Integer zzbvZ;
    private Integer zzbwa;
    public Long zzbwb;
    public Long zzbwc;
    public String zzbwd;

    public zzcjy() {
        this.zzbvD = null;
        this.zzbvE = zzcjv.zzzB();
        this.zzbvF = zzcka.zzzE();
        this.zzbvG = null;
        this.zzbvH = null;
        this.zzbvI = null;
        this.zzbvJ = null;
        this.zzbvK = null;
        this.zzbvL = null;
        this.zzba = null;
        this.zzbvM = null;
        this.zzbvN = null;
        this.zzbvO = null;
        this.zzboR = null;
        this.zzaJ = null;
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
        this.zzbvX = zzcju.zzzA();
        this.zzboY = null;
        this.zzbvY = null;
        this.zzbvZ = null;
        this.zzbwa = null;
        this.zzbwb = null;
        this.zzbwc = null;
        this.zzbwd = null;
        this.zzcrZ = null;
        this.zzcsi = -1;
    }

    public static zzcjy[] zzzD() {
        if (zzbvC == null) {
            synchronized (ade.zzcsh) {
                if (zzbvC == null) {
                    zzbvC = new zzcjy[0];
                }
            }
        }
        return zzbvC;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjy)) {
            return false;
        }
        zzcjy com_google_android_gms_internal_zzcjy = (zzcjy) obj;
        if (this.zzbvD == null) {
            if (com_google_android_gms_internal_zzcjy.zzbvD != null) {
                return false;
            }
        } else if (!this.zzbvD.equals(com_google_android_gms_internal_zzcjy.zzbvD)) {
            return false;
        }
        if (!ade.equals(this.zzbvE, com_google_android_gms_internal_zzcjy.zzbvE)) {
            return false;
        }
        if (!ade.equals(this.zzbvF, com_google_android_gms_internal_zzcjy.zzbvF)) {
            return false;
        }
        if (this.zzbvG == null) {
            if (com_google_android_gms_internal_zzcjy.zzbvG != null) {
                return false;
            }
        } else if (!this.zzbvG.equals(com_google_android_gms_internal_zzcjy.zzbvG)) {
            return false;
        }
        if (this.zzbvH == null) {
            if (com_google_android_gms_internal_zzcjy.zzbvH != null) {
                return false;
            }
        } else if (!this.zzbvH.equals(com_google_android_gms_internal_zzcjy.zzbvH)) {
            return false;
        }
        if (this.zzbvI == null) {
            if (com_google_android_gms_internal_zzcjy.zzbvI != null) {
                return false;
            }
        } else if (!this.zzbvI.equals(com_google_android_gms_internal_zzcjy.zzbvI)) {
            return false;
        }
        if (this.zzbvJ == null) {
            if (com_google_android_gms_internal_zzcjy.zzbvJ != null) {
                return false;
            }
        } else if (!this.zzbvJ.equals(com_google_android_gms_internal_zzcjy.zzbvJ)) {
            return false;
        }
        if (this.zzbvK == null) {
            if (com_google_android_gms_internal_zzcjy.zzbvK != null) {
                return false;
            }
        } else if (!this.zzbvK.equals(com_google_android_gms_internal_zzcjy.zzbvK)) {
            return false;
        }
        if (this.zzbvL == null) {
            if (com_google_android_gms_internal_zzcjy.zzbvL != null) {
                return false;
            }
        } else if (!this.zzbvL.equals(com_google_android_gms_internal_zzcjy.zzbvL)) {
            return false;
        }
        if (this.zzba == null) {
            if (com_google_android_gms_internal_zzcjy.zzba != null) {
                return false;
            }
        } else if (!this.zzba.equals(com_google_android_gms_internal_zzcjy.zzba)) {
            return false;
        }
        if (this.zzbvM == null) {
            if (com_google_android_gms_internal_zzcjy.zzbvM != null) {
                return false;
            }
        } else if (!this.zzbvM.equals(com_google_android_gms_internal_zzcjy.zzbvM)) {
            return false;
        }
        if (this.zzbvN == null) {
            if (com_google_android_gms_internal_zzcjy.zzbvN != null) {
                return false;
            }
        } else if (!this.zzbvN.equals(com_google_android_gms_internal_zzcjy.zzbvN)) {
            return false;
        }
        if (this.zzbvO == null) {
            if (com_google_android_gms_internal_zzcjy.zzbvO != null) {
                return false;
            }
        } else if (!this.zzbvO.equals(com_google_android_gms_internal_zzcjy.zzbvO)) {
            return false;
        }
        if (this.zzboR == null) {
            if (com_google_android_gms_internal_zzcjy.zzboR != null) {
                return false;
            }
        } else if (!this.zzboR.equals(com_google_android_gms_internal_zzcjy.zzboR)) {
            return false;
        }
        if (this.zzaJ == null) {
            if (com_google_android_gms_internal_zzcjy.zzaJ != null) {
                return false;
            }
        } else if (!this.zzaJ.equals(com_google_android_gms_internal_zzcjy.zzaJ)) {
            return false;
        }
        if (this.zzbgW == null) {
            if (com_google_android_gms_internal_zzcjy.zzbgW != null) {
                return false;
            }
        } else if (!this.zzbgW.equals(com_google_android_gms_internal_zzcjy.zzbgW)) {
            return false;
        }
        if (this.zzbvP == null) {
            if (com_google_android_gms_internal_zzcjy.zzbvP != null) {
                return false;
            }
        } else if (!this.zzbvP.equals(com_google_android_gms_internal_zzcjy.zzbvP)) {
            return false;
        }
        if (this.zzbvQ == null) {
            if (com_google_android_gms_internal_zzcjy.zzbvQ != null) {
                return false;
            }
        } else if (!this.zzbvQ.equals(com_google_android_gms_internal_zzcjy.zzbvQ)) {
            return false;
        }
        if (this.zzbvR == null) {
            if (com_google_android_gms_internal_zzcjy.zzbvR != null) {
                return false;
            }
        } else if (!this.zzbvR.equals(com_google_android_gms_internal_zzcjy.zzbvR)) {
            return false;
        }
        if (this.zzbvS == null) {
            if (com_google_android_gms_internal_zzcjy.zzbvS != null) {
                return false;
            }
        } else if (!this.zzbvS.equals(com_google_android_gms_internal_zzcjy.zzbvS)) {
            return false;
        }
        if (this.zzbvT == null) {
            if (com_google_android_gms_internal_zzcjy.zzbvT != null) {
                return false;
            }
        } else if (!this.zzbvT.equals(com_google_android_gms_internal_zzcjy.zzbvT)) {
            return false;
        }
        if (this.zzbvU == null) {
            if (com_google_android_gms_internal_zzcjy.zzbvU != null) {
                return false;
            }
        } else if (!this.zzbvU.equals(com_google_android_gms_internal_zzcjy.zzbvU)) {
            return false;
        }
        if (this.zzbvV == null) {
            if (com_google_android_gms_internal_zzcjy.zzbvV != null) {
                return false;
            }
        } else if (!this.zzbvV.equals(com_google_android_gms_internal_zzcjy.zzbvV)) {
            return false;
        }
        if (this.zzboU == null) {
            if (com_google_android_gms_internal_zzcjy.zzboU != null) {
                return false;
            }
        } else if (!this.zzboU.equals(com_google_android_gms_internal_zzcjy.zzboU)) {
            return false;
        }
        if (this.zzboQ == null) {
            if (com_google_android_gms_internal_zzcjy.zzboQ != null) {
                return false;
            }
        } else if (!this.zzboQ.equals(com_google_android_gms_internal_zzcjy.zzboQ)) {
            return false;
        }
        if (this.zzbvW == null) {
            if (com_google_android_gms_internal_zzcjy.zzbvW != null) {
                return false;
            }
        } else if (!this.zzbvW.equals(com_google_android_gms_internal_zzcjy.zzbvW)) {
            return false;
        }
        if (!ade.equals(this.zzbvX, com_google_android_gms_internal_zzcjy.zzbvX)) {
            return false;
        }
        if (this.zzboY == null) {
            if (com_google_android_gms_internal_zzcjy.zzboY != null) {
                return false;
            }
        } else if (!this.zzboY.equals(com_google_android_gms_internal_zzcjy.zzboY)) {
            return false;
        }
        if (this.zzbvY == null) {
            if (com_google_android_gms_internal_zzcjy.zzbvY != null) {
                return false;
            }
        } else if (!this.zzbvY.equals(com_google_android_gms_internal_zzcjy.zzbvY)) {
            return false;
        }
        if (this.zzbvZ == null) {
            if (com_google_android_gms_internal_zzcjy.zzbvZ != null) {
                return false;
            }
        } else if (!this.zzbvZ.equals(com_google_android_gms_internal_zzcjy.zzbvZ)) {
            return false;
        }
        if (this.zzbwa == null) {
            if (com_google_android_gms_internal_zzcjy.zzbwa != null) {
                return false;
            }
        } else if (!this.zzbwa.equals(com_google_android_gms_internal_zzcjy.zzbwa)) {
            return false;
        }
        if (this.zzbwb == null) {
            if (com_google_android_gms_internal_zzcjy.zzbwb != null) {
                return false;
            }
        } else if (!this.zzbwb.equals(com_google_android_gms_internal_zzcjy.zzbwb)) {
            return false;
        }
        if (this.zzbwc == null) {
            if (com_google_android_gms_internal_zzcjy.zzbwc != null) {
                return false;
            }
        } else if (!this.zzbwc.equals(com_google_android_gms_internal_zzcjy.zzbwc)) {
            return false;
        }
        if (this.zzbwd == null) {
            if (com_google_android_gms_internal_zzcjy.zzbwd != null) {
                return false;
            }
        } else if (!this.zzbwd.equals(com_google_android_gms_internal_zzcjy.zzbwd)) {
            return false;
        }
        return (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? com_google_android_gms_internal_zzcjy.zzcrZ == null || com_google_android_gms_internal_zzcjy.zzcrZ.isEmpty() : this.zzcrZ.equals(com_google_android_gms_internal_zzcjy.zzcrZ);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzbwd == null ? 0 : this.zzbwd.hashCode()) + (((this.zzbwc == null ? 0 : this.zzbwc.hashCode()) + (((this.zzbwb == null ? 0 : this.zzbwb.hashCode()) + (((this.zzbwa == null ? 0 : this.zzbwa.hashCode()) + (((this.zzbvZ == null ? 0 : this.zzbvZ.hashCode()) + (((this.zzbvY == null ? 0 : this.zzbvY.hashCode()) + (((this.zzboY == null ? 0 : this.zzboY.hashCode()) + (((((this.zzbvW == null ? 0 : this.zzbvW.hashCode()) + (((this.zzboQ == null ? 0 : this.zzboQ.hashCode()) + (((this.zzboU == null ? 0 : this.zzboU.hashCode()) + (((this.zzbvV == null ? 0 : this.zzbvV.hashCode()) + (((this.zzbvU == null ? 0 : this.zzbvU.hashCode()) + (((this.zzbvT == null ? 0 : this.zzbvT.hashCode()) + (((this.zzbvS == null ? 0 : this.zzbvS.hashCode()) + (((this.zzbvR == null ? 0 : this.zzbvR.hashCode()) + (((this.zzbvQ == null ? 0 : this.zzbvQ.hashCode()) + (((this.zzbvP == null ? 0 : this.zzbvP.hashCode()) + (((this.zzbgW == null ? 0 : this.zzbgW.hashCode()) + (((this.zzaJ == null ? 0 : this.zzaJ.hashCode()) + (((this.zzboR == null ? 0 : this.zzboR.hashCode()) + (((this.zzbvO == null ? 0 : this.zzbvO.hashCode()) + (((this.zzbvN == null ? 0 : this.zzbvN.hashCode()) + (((this.zzbvM == null ? 0 : this.zzbvM.hashCode()) + (((this.zzba == null ? 0 : this.zzba.hashCode()) + (((this.zzbvL == null ? 0 : this.zzbvL.hashCode()) + (((this.zzbvK == null ? 0 : this.zzbvK.hashCode()) + (((this.zzbvJ == null ? 0 : this.zzbvJ.hashCode()) + (((this.zzbvI == null ? 0 : this.zzbvI.hashCode()) + (((this.zzbvH == null ? 0 : this.zzbvH.hashCode()) + (((this.zzbvG == null ? 0 : this.zzbvG.hashCode()) + (((((((this.zzbvD == null ? 0 : this.zzbvD.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31) + ade.hashCode(this.zzbvE)) * 31) + ade.hashCode(this.zzbvF)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31) + ade.hashCode(this.zzbvX)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
        if (!(this.zzcrZ == null || this.zzcrZ.isEmpty())) {
            i = this.zzcrZ.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ adg zza(acx com_google_android_gms_internal_acx) throws IOException {
        while (true) {
            int zzLy = com_google_android_gms_internal_acx.zzLy();
            int zzb;
            Object obj;
            switch (zzLy) {
                case 0:
                    break;
                case 8:
                    this.zzbvD = Integer.valueOf(com_google_android_gms_internal_acx.zzLD());
                    continue;
                case 18:
                    zzb = adj.zzb(com_google_android_gms_internal_acx, 18);
                    zzLy = this.zzbvE == null ? 0 : this.zzbvE.length;
                    obj = new zzcjv[(zzb + zzLy)];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzbvE, 0, obj, 0, zzLy);
                    }
                    while (zzLy < obj.length - 1) {
                        obj[zzLy] = new zzcjv();
                        com_google_android_gms_internal_acx.zza(obj[zzLy]);
                        com_google_android_gms_internal_acx.zzLy();
                        zzLy++;
                    }
                    obj[zzLy] = new zzcjv();
                    com_google_android_gms_internal_acx.zza(obj[zzLy]);
                    this.zzbvE = obj;
                    continue;
                case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                    zzb = adj.zzb(com_google_android_gms_internal_acx, 26);
                    zzLy = this.zzbvF == null ? 0 : this.zzbvF.length;
                    obj = new zzcka[(zzb + zzLy)];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzbvF, 0, obj, 0, zzLy);
                    }
                    while (zzLy < obj.length - 1) {
                        obj[zzLy] = new zzcka();
                        com_google_android_gms_internal_acx.zza(obj[zzLy]);
                        com_google_android_gms_internal_acx.zzLy();
                        zzLy++;
                    }
                    obj[zzLy] = new zzcka();
                    com_google_android_gms_internal_acx.zza(obj[zzLy]);
                    this.zzbvF = obj;
                    continue;
                case 32:
                    this.zzbvG = Long.valueOf(com_google_android_gms_internal_acx.zzLE());
                    continue;
                case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                    this.zzbvH = Long.valueOf(com_google_android_gms_internal_acx.zzLE());
                    continue;
                case 48:
                    this.zzbvI = Long.valueOf(com_google_android_gms_internal_acx.zzLE());
                    continue;
                case 56:
                    this.zzbvK = Long.valueOf(com_google_android_gms_internal_acx.zzLE());
                    continue;
                case SecretChatHelper.CURRENT_SECRET_CHAT_LAYER /*66*/:
                    this.zzbvL = com_google_android_gms_internal_acx.readString();
                    continue;
                case 74:
                    this.zzba = com_google_android_gms_internal_acx.readString();
                    continue;
                case 82:
                    this.zzbvM = com_google_android_gms_internal_acx.readString();
                    continue;
                case 90:
                    this.zzbvN = com_google_android_gms_internal_acx.readString();
                    continue;
                case 96:
                    this.zzbvO = Integer.valueOf(com_google_android_gms_internal_acx.zzLD());
                    continue;
                case 106:
                    this.zzboR = com_google_android_gms_internal_acx.readString();
                    continue;
                case 114:
                    this.zzaJ = com_google_android_gms_internal_acx.readString();
                    continue;
                case TsExtractor.TS_STREAM_TYPE_HDMV_DTS /*130*/:
                    this.zzbgW = com_google_android_gms_internal_acx.readString();
                    continue;
                case 136:
                    this.zzbvP = Long.valueOf(com_google_android_gms_internal_acx.zzLE());
                    continue;
                case 144:
                    this.zzbvQ = Long.valueOf(com_google_android_gms_internal_acx.zzLE());
                    continue;
                case 154:
                    this.zzbvR = com_google_android_gms_internal_acx.readString();
                    continue;
                case 160:
                    this.zzbvS = Boolean.valueOf(com_google_android_gms_internal_acx.zzLB());
                    continue;
                case 170:
                    this.zzbvT = com_google_android_gms_internal_acx.readString();
                    continue;
                case 176:
                    this.zzbvU = Long.valueOf(com_google_android_gms_internal_acx.zzLE());
                    continue;
                case 184:
                    this.zzbvV = Integer.valueOf(com_google_android_gms_internal_acx.zzLD());
                    continue;
                case 194:
                    this.zzboU = com_google_android_gms_internal_acx.readString();
                    continue;
                case 202:
                    this.zzboQ = com_google_android_gms_internal_acx.readString();
                    continue;
                case 208:
                    this.zzbvJ = Long.valueOf(com_google_android_gms_internal_acx.zzLE());
                    continue;
                case 224:
                    this.zzbvW = Boolean.valueOf(com_google_android_gms_internal_acx.zzLB());
                    continue;
                case 234:
                    zzb = adj.zzb(com_google_android_gms_internal_acx, 234);
                    zzLy = this.zzbvX == null ? 0 : this.zzbvX.length;
                    obj = new zzcju[(zzb + zzLy)];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzbvX, 0, obj, 0, zzLy);
                    }
                    while (zzLy < obj.length - 1) {
                        obj[zzLy] = new zzcju();
                        com_google_android_gms_internal_acx.zza(obj[zzLy]);
                        com_google_android_gms_internal_acx.zzLy();
                        zzLy++;
                    }
                    obj[zzLy] = new zzcju();
                    com_google_android_gms_internal_acx.zza(obj[zzLy]);
                    this.zzbvX = obj;
                    continue;
                case 242:
                    this.zzboY = com_google_android_gms_internal_acx.readString();
                    continue;
                case 248:
                    this.zzbvY = Integer.valueOf(com_google_android_gms_internal_acx.zzLD());
                    continue;
                case 256:
                    this.zzbvZ = Integer.valueOf(com_google_android_gms_internal_acx.zzLD());
                    continue;
                case 264:
                    this.zzbwa = Integer.valueOf(com_google_android_gms_internal_acx.zzLD());
                    continue;
                case 280:
                    this.zzbwb = Long.valueOf(com_google_android_gms_internal_acx.zzLE());
                    continue;
                case 288:
                    this.zzbwc = Long.valueOf(com_google_android_gms_internal_acx.zzLE());
                    continue;
                case 298:
                    this.zzbwd = com_google_android_gms_internal_acx.readString();
                    continue;
                default:
                    if (!super.zza(com_google_android_gms_internal_acx, zzLy)) {
                        break;
                    }
                    continue;
            }
            return this;
        }
    }

    public final void zza(acy com_google_android_gms_internal_acy) throws IOException {
        int i = 0;
        if (this.zzbvD != null) {
            com_google_android_gms_internal_acy.zzr(1, this.zzbvD.intValue());
        }
        if (this.zzbvE != null && this.zzbvE.length > 0) {
            for (adg com_google_android_gms_internal_adg : this.zzbvE) {
                if (com_google_android_gms_internal_adg != null) {
                    com_google_android_gms_internal_acy.zza(2, com_google_android_gms_internal_adg);
                }
            }
        }
        if (this.zzbvF != null && this.zzbvF.length > 0) {
            for (adg com_google_android_gms_internal_adg2 : this.zzbvF) {
                if (com_google_android_gms_internal_adg2 != null) {
                    com_google_android_gms_internal_acy.zza(3, com_google_android_gms_internal_adg2);
                }
            }
        }
        if (this.zzbvG != null) {
            com_google_android_gms_internal_acy.zzb(4, this.zzbvG.longValue());
        }
        if (this.zzbvH != null) {
            com_google_android_gms_internal_acy.zzb(5, this.zzbvH.longValue());
        }
        if (this.zzbvI != null) {
            com_google_android_gms_internal_acy.zzb(6, this.zzbvI.longValue());
        }
        if (this.zzbvK != null) {
            com_google_android_gms_internal_acy.zzb(7, this.zzbvK.longValue());
        }
        if (this.zzbvL != null) {
            com_google_android_gms_internal_acy.zzl(8, this.zzbvL);
        }
        if (this.zzba != null) {
            com_google_android_gms_internal_acy.zzl(9, this.zzba);
        }
        if (this.zzbvM != null) {
            com_google_android_gms_internal_acy.zzl(10, this.zzbvM);
        }
        if (this.zzbvN != null) {
            com_google_android_gms_internal_acy.zzl(11, this.zzbvN);
        }
        if (this.zzbvO != null) {
            com_google_android_gms_internal_acy.zzr(12, this.zzbvO.intValue());
        }
        if (this.zzboR != null) {
            com_google_android_gms_internal_acy.zzl(13, this.zzboR);
        }
        if (this.zzaJ != null) {
            com_google_android_gms_internal_acy.zzl(14, this.zzaJ);
        }
        if (this.zzbgW != null) {
            com_google_android_gms_internal_acy.zzl(16, this.zzbgW);
        }
        if (this.zzbvP != null) {
            com_google_android_gms_internal_acy.zzb(17, this.zzbvP.longValue());
        }
        if (this.zzbvQ != null) {
            com_google_android_gms_internal_acy.zzb(18, this.zzbvQ.longValue());
        }
        if (this.zzbvR != null) {
            com_google_android_gms_internal_acy.zzl(19, this.zzbvR);
        }
        if (this.zzbvS != null) {
            com_google_android_gms_internal_acy.zzk(20, this.zzbvS.booleanValue());
        }
        if (this.zzbvT != null) {
            com_google_android_gms_internal_acy.zzl(21, this.zzbvT);
        }
        if (this.zzbvU != null) {
            com_google_android_gms_internal_acy.zzb(22, this.zzbvU.longValue());
        }
        if (this.zzbvV != null) {
            com_google_android_gms_internal_acy.zzr(23, this.zzbvV.intValue());
        }
        if (this.zzboU != null) {
            com_google_android_gms_internal_acy.zzl(24, this.zzboU);
        }
        if (this.zzboQ != null) {
            com_google_android_gms_internal_acy.zzl(25, this.zzboQ);
        }
        if (this.zzbvJ != null) {
            com_google_android_gms_internal_acy.zzb(26, this.zzbvJ.longValue());
        }
        if (this.zzbvW != null) {
            com_google_android_gms_internal_acy.zzk(28, this.zzbvW.booleanValue());
        }
        if (this.zzbvX != null && this.zzbvX.length > 0) {
            while (i < this.zzbvX.length) {
                adg com_google_android_gms_internal_adg3 = this.zzbvX[i];
                if (com_google_android_gms_internal_adg3 != null) {
                    com_google_android_gms_internal_acy.zza(29, com_google_android_gms_internal_adg3);
                }
                i++;
            }
        }
        if (this.zzboY != null) {
            com_google_android_gms_internal_acy.zzl(30, this.zzboY);
        }
        if (this.zzbvY != null) {
            com_google_android_gms_internal_acy.zzr(31, this.zzbvY.intValue());
        }
        if (this.zzbvZ != null) {
            com_google_android_gms_internal_acy.zzr(32, this.zzbvZ.intValue());
        }
        if (this.zzbwa != null) {
            com_google_android_gms_internal_acy.zzr(33, this.zzbwa.intValue());
        }
        if (this.zzbwb != null) {
            com_google_android_gms_internal_acy.zzb(35, this.zzbwb.longValue());
        }
        if (this.zzbwc != null) {
            com_google_android_gms_internal_acy.zzb(36, this.zzbwc.longValue());
        }
        if (this.zzbwd != null) {
            com_google_android_gms_internal_acy.zzl(37, this.zzbwd);
        }
        super.zza(com_google_android_gms_internal_acy);
    }

    protected final int zzn() {
        int i;
        int i2 = 0;
        int zzn = super.zzn();
        if (this.zzbvD != null) {
            zzn += acy.zzs(1, this.zzbvD.intValue());
        }
        if (this.zzbvE != null && this.zzbvE.length > 0) {
            i = zzn;
            for (adg com_google_android_gms_internal_adg : this.zzbvE) {
                if (com_google_android_gms_internal_adg != null) {
                    i += acy.zzb(2, com_google_android_gms_internal_adg);
                }
            }
            zzn = i;
        }
        if (this.zzbvF != null && this.zzbvF.length > 0) {
            i = zzn;
            for (adg com_google_android_gms_internal_adg2 : this.zzbvF) {
                if (com_google_android_gms_internal_adg2 != null) {
                    i += acy.zzb(3, com_google_android_gms_internal_adg2);
                }
            }
            zzn = i;
        }
        if (this.zzbvG != null) {
            zzn += acy.zze(4, this.zzbvG.longValue());
        }
        if (this.zzbvH != null) {
            zzn += acy.zze(5, this.zzbvH.longValue());
        }
        if (this.zzbvI != null) {
            zzn += acy.zze(6, this.zzbvI.longValue());
        }
        if (this.zzbvK != null) {
            zzn += acy.zze(7, this.zzbvK.longValue());
        }
        if (this.zzbvL != null) {
            zzn += acy.zzm(8, this.zzbvL);
        }
        if (this.zzba != null) {
            zzn += acy.zzm(9, this.zzba);
        }
        if (this.zzbvM != null) {
            zzn += acy.zzm(10, this.zzbvM);
        }
        if (this.zzbvN != null) {
            zzn += acy.zzm(11, this.zzbvN);
        }
        if (this.zzbvO != null) {
            zzn += acy.zzs(12, this.zzbvO.intValue());
        }
        if (this.zzboR != null) {
            zzn += acy.zzm(13, this.zzboR);
        }
        if (this.zzaJ != null) {
            zzn += acy.zzm(14, this.zzaJ);
        }
        if (this.zzbgW != null) {
            zzn += acy.zzm(16, this.zzbgW);
        }
        if (this.zzbvP != null) {
            zzn += acy.zze(17, this.zzbvP.longValue());
        }
        if (this.zzbvQ != null) {
            zzn += acy.zze(18, this.zzbvQ.longValue());
        }
        if (this.zzbvR != null) {
            zzn += acy.zzm(19, this.zzbvR);
        }
        if (this.zzbvS != null) {
            this.zzbvS.booleanValue();
            zzn += acy.zzct(20) + 1;
        }
        if (this.zzbvT != null) {
            zzn += acy.zzm(21, this.zzbvT);
        }
        if (this.zzbvU != null) {
            zzn += acy.zze(22, this.zzbvU.longValue());
        }
        if (this.zzbvV != null) {
            zzn += acy.zzs(23, this.zzbvV.intValue());
        }
        if (this.zzboU != null) {
            zzn += acy.zzm(24, this.zzboU);
        }
        if (this.zzboQ != null) {
            zzn += acy.zzm(25, this.zzboQ);
        }
        if (this.zzbvJ != null) {
            zzn += acy.zze(26, this.zzbvJ.longValue());
        }
        if (this.zzbvW != null) {
            this.zzbvW.booleanValue();
            zzn += acy.zzct(28) + 1;
        }
        if (this.zzbvX != null && this.zzbvX.length > 0) {
            while (i2 < this.zzbvX.length) {
                adg com_google_android_gms_internal_adg3 = this.zzbvX[i2];
                if (com_google_android_gms_internal_adg3 != null) {
                    zzn += acy.zzb(29, com_google_android_gms_internal_adg3);
                }
                i2++;
            }
        }
        if (this.zzboY != null) {
            zzn += acy.zzm(30, this.zzboY);
        }
        if (this.zzbvY != null) {
            zzn += acy.zzs(31, this.zzbvY.intValue());
        }
        if (this.zzbvZ != null) {
            zzn += acy.zzs(32, this.zzbvZ.intValue());
        }
        if (this.zzbwa != null) {
            zzn += acy.zzs(33, this.zzbwa.intValue());
        }
        if (this.zzbwb != null) {
            zzn += acy.zze(35, this.zzbwb.longValue());
        }
        if (this.zzbwc != null) {
            zzn += acy.zze(36, this.zzbwc.longValue());
        }
        return this.zzbwd != null ? zzn + acy.zzm(37, this.zzbwd) : zzn;
    }
}

package com.google.android.gms.internal;

import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;
import java.util.Arrays;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;

public final class aea extends ada<aea> implements Cloneable {
    private String tag;
    private boolean zzccZ;
    private aec zzcmI;
    public long zzctB;
    public long zzctC;
    private long zzctD;
    public int zzctE;
    private aeb[] zzctF;
    private byte[] zzctG;
    private ady zzctH;
    public byte[] zzctI;
    private String zzctJ;
    private String zzctK;
    private adx zzctL;
    private String zzctM;
    public long zzctN;
    private adz zzctO;
    public byte[] zzctP;
    private String zzctQ;
    private int zzctR;
    private int[] zzctS;
    private long zzctT;
    public int zzrD;

    public aea() {
        this.zzctB = 0;
        this.zzctC = 0;
        this.zzctD = 0;
        this.tag = "";
        this.zzctE = 0;
        this.zzrD = 0;
        this.zzccZ = false;
        this.zzctF = aeb.zzMc();
        this.zzctG = adj.zzcst;
        this.zzctH = null;
        this.zzctI = adj.zzcst;
        this.zzctJ = "";
        this.zzctK = "";
        this.zzctL = null;
        this.zzctM = "";
        this.zzctN = 180000;
        this.zzctO = null;
        this.zzctP = adj.zzcst;
        this.zzctQ = "";
        this.zzctR = 0;
        this.zzctS = adj.zzcsn;
        this.zzctT = 0;
        this.zzcmI = null;
        this.zzcrZ = null;
        this.zzcsi = -1;
    }

    private final aea zzMb() {
        try {
            aea com_google_android_gms_internal_aea = (aea) super.zzLL();
            if (this.zzctF != null && this.zzctF.length > 0) {
                com_google_android_gms_internal_aea.zzctF = new aeb[this.zzctF.length];
                for (int i = 0; i < this.zzctF.length; i++) {
                    if (this.zzctF[i] != null) {
                        com_google_android_gms_internal_aea.zzctF[i] = (aeb) this.zzctF[i].clone();
                    }
                }
            }
            if (this.zzctH != null) {
                com_google_android_gms_internal_aea.zzctH = (ady) this.zzctH.clone();
            }
            if (this.zzctL != null) {
                com_google_android_gms_internal_aea.zzctL = (adx) this.zzctL.clone();
            }
            if (this.zzctO != null) {
                com_google_android_gms_internal_aea.zzctO = (adz) this.zzctO.clone();
            }
            if (this.zzctS != null && this.zzctS.length > 0) {
                com_google_android_gms_internal_aea.zzctS = (int[]) this.zzctS.clone();
            }
            if (this.zzcmI != null) {
                com_google_android_gms_internal_aea.zzcmI = (aec) this.zzcmI.clone();
            }
            return com_google_android_gms_internal_aea;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public final /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzMb();
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof aea)) {
            return false;
        }
        aea com_google_android_gms_internal_aea = (aea) obj;
        if (this.zzctB != com_google_android_gms_internal_aea.zzctB) {
            return false;
        }
        if (this.zzctC != com_google_android_gms_internal_aea.zzctC) {
            return false;
        }
        if (this.zzctD != com_google_android_gms_internal_aea.zzctD) {
            return false;
        }
        if (this.tag == null) {
            if (com_google_android_gms_internal_aea.tag != null) {
                return false;
            }
        } else if (!this.tag.equals(com_google_android_gms_internal_aea.tag)) {
            return false;
        }
        if (this.zzctE != com_google_android_gms_internal_aea.zzctE) {
            return false;
        }
        if (this.zzrD != com_google_android_gms_internal_aea.zzrD) {
            return false;
        }
        if (this.zzccZ != com_google_android_gms_internal_aea.zzccZ) {
            return false;
        }
        if (!ade.equals(this.zzctF, com_google_android_gms_internal_aea.zzctF)) {
            return false;
        }
        if (!Arrays.equals(this.zzctG, com_google_android_gms_internal_aea.zzctG)) {
            return false;
        }
        if (this.zzctH == null) {
            if (com_google_android_gms_internal_aea.zzctH != null) {
                return false;
            }
        } else if (!this.zzctH.equals(com_google_android_gms_internal_aea.zzctH)) {
            return false;
        }
        if (!Arrays.equals(this.zzctI, com_google_android_gms_internal_aea.zzctI)) {
            return false;
        }
        if (this.zzctJ == null) {
            if (com_google_android_gms_internal_aea.zzctJ != null) {
                return false;
            }
        } else if (!this.zzctJ.equals(com_google_android_gms_internal_aea.zzctJ)) {
            return false;
        }
        if (this.zzctK == null) {
            if (com_google_android_gms_internal_aea.zzctK != null) {
                return false;
            }
        } else if (!this.zzctK.equals(com_google_android_gms_internal_aea.zzctK)) {
            return false;
        }
        if (this.zzctL == null) {
            if (com_google_android_gms_internal_aea.zzctL != null) {
                return false;
            }
        } else if (!this.zzctL.equals(com_google_android_gms_internal_aea.zzctL)) {
            return false;
        }
        if (this.zzctM == null) {
            if (com_google_android_gms_internal_aea.zzctM != null) {
                return false;
            }
        } else if (!this.zzctM.equals(com_google_android_gms_internal_aea.zzctM)) {
            return false;
        }
        if (this.zzctN != com_google_android_gms_internal_aea.zzctN) {
            return false;
        }
        if (this.zzctO == null) {
            if (com_google_android_gms_internal_aea.zzctO != null) {
                return false;
            }
        } else if (!this.zzctO.equals(com_google_android_gms_internal_aea.zzctO)) {
            return false;
        }
        if (!Arrays.equals(this.zzctP, com_google_android_gms_internal_aea.zzctP)) {
            return false;
        }
        if (this.zzctQ == null) {
            if (com_google_android_gms_internal_aea.zzctQ != null) {
                return false;
            }
        } else if (!this.zzctQ.equals(com_google_android_gms_internal_aea.zzctQ)) {
            return false;
        }
        if (this.zzctR != com_google_android_gms_internal_aea.zzctR) {
            return false;
        }
        if (!ade.equals(this.zzctS, com_google_android_gms_internal_aea.zzctS)) {
            return false;
        }
        if (this.zzctT != com_google_android_gms_internal_aea.zzctT) {
            return false;
        }
        if (this.zzcmI == null) {
            if (com_google_android_gms_internal_aea.zzcmI != null) {
                return false;
            }
        } else if (!this.zzcmI.equals(com_google_android_gms_internal_aea.zzcmI)) {
            return false;
        }
        return (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? com_google_android_gms_internal_aea.zzcrZ == null || com_google_android_gms_internal_aea.zzcrZ.isEmpty() : this.zzcrZ.equals(com_google_android_gms_internal_aea.zzcrZ);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzcmI == null ? 0 : this.zzcmI.hashCode()) + (((((((((this.zzctQ == null ? 0 : this.zzctQ.hashCode()) + (((((this.zzctO == null ? 0 : this.zzctO.hashCode()) + (((((this.zzctM == null ? 0 : this.zzctM.hashCode()) + (((this.zzctL == null ? 0 : this.zzctL.hashCode()) + (((this.zzctK == null ? 0 : this.zzctK.hashCode()) + (((this.zzctJ == null ? 0 : this.zzctJ.hashCode()) + (((((this.zzctH == null ? 0 : this.zzctH.hashCode()) + (((((((this.zzccZ ? 1231 : 1237) + (((((((this.tag == null ? 0 : this.tag.hashCode()) + ((((((((getClass().getName().hashCode() + 527) * 31) + ((int) (this.zzctB ^ (this.zzctB >>> 32)))) * 31) + ((int) (this.zzctC ^ (this.zzctC >>> 32)))) * 31) + ((int) (this.zzctD ^ (this.zzctD >>> 32)))) * 31)) * 31) + this.zzctE) * 31) + this.zzrD) * 31)) * 31) + ade.hashCode(this.zzctF)) * 31) + Arrays.hashCode(this.zzctG)) * 31)) * 31) + Arrays.hashCode(this.zzctI)) * 31)) * 31)) * 31)) * 31)) * 31) + ((int) (this.zzctN ^ (this.zzctN >>> 32)))) * 31)) * 31) + Arrays.hashCode(this.zzctP)) * 31)) * 31) + this.zzctR) * 31) + ade.hashCode(this.zzctS)) * 31) + ((int) (this.zzctT ^ (this.zzctT >>> 32)))) * 31)) * 31;
        if (!(this.zzcrZ == null || this.zzcrZ.isEmpty())) {
            i = this.zzcrZ.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ ada zzLL() throws CloneNotSupportedException {
        return (aea) clone();
    }

    public final /* synthetic */ adg zzLM() throws CloneNotSupportedException {
        return (aea) clone();
    }

    public final /* synthetic */ adg zza(acx com_google_android_gms_internal_acx) throws IOException {
        while (true) {
            int zzLy = com_google_android_gms_internal_acx.zzLy();
            int zzb;
            Object obj;
            int zzLA;
            switch (zzLy) {
                case 0:
                    break;
                case 8:
                    this.zzctB = com_google_android_gms_internal_acx.zzLz();
                    continue;
                case 18:
                    this.tag = com_google_android_gms_internal_acx.readString();
                    continue;
                case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                    zzb = adj.zzb(com_google_android_gms_internal_acx, 26);
                    zzLy = this.zzctF == null ? 0 : this.zzctF.length;
                    obj = new aeb[(zzb + zzLy)];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzctF, 0, obj, 0, zzLy);
                    }
                    while (zzLy < obj.length - 1) {
                        obj[zzLy] = new aeb();
                        com_google_android_gms_internal_acx.zza(obj[zzLy]);
                        com_google_android_gms_internal_acx.zzLy();
                        zzLy++;
                    }
                    obj[zzLy] = new aeb();
                    com_google_android_gms_internal_acx.zza(obj[zzLy]);
                    this.zzctF = obj;
                    continue;
                case 34:
                    this.zzctG = com_google_android_gms_internal_acx.readBytes();
                    continue;
                case 50:
                    this.zzctI = com_google_android_gms_internal_acx.readBytes();
                    continue;
                case 58:
                    if (this.zzctL == null) {
                        this.zzctL = new adx();
                    }
                    com_google_android_gms_internal_acx.zza(this.zzctL);
                    continue;
                case SecretChatHelper.CURRENT_SECRET_CHAT_LAYER /*66*/:
                    this.zzctJ = com_google_android_gms_internal_acx.readString();
                    continue;
                case 74:
                    if (this.zzctH == null) {
                        this.zzctH = new ady();
                    }
                    com_google_android_gms_internal_acx.zza(this.zzctH);
                    continue;
                case 80:
                    this.zzccZ = com_google_android_gms_internal_acx.zzLB();
                    continue;
                case 88:
                    this.zzctE = com_google_android_gms_internal_acx.zzLA();
                    continue;
                case 96:
                    this.zzrD = com_google_android_gms_internal_acx.zzLA();
                    continue;
                case 106:
                    this.zzctK = com_google_android_gms_internal_acx.readString();
                    continue;
                case 114:
                    this.zzctM = com_google_android_gms_internal_acx.readString();
                    continue;
                case 120:
                    this.zzctN = com_google_android_gms_internal_acx.zzLC();
                    continue;
                case TsExtractor.TS_STREAM_TYPE_HDMV_DTS /*130*/:
                    if (this.zzctO == null) {
                        this.zzctO = new adz();
                    }
                    com_google_android_gms_internal_acx.zza(this.zzctO);
                    continue;
                case 136:
                    this.zzctC = com_google_android_gms_internal_acx.zzLz();
                    continue;
                case 146:
                    this.zzctP = com_google_android_gms_internal_acx.readBytes();
                    continue;
                case 152:
                    zzb = com_google_android_gms_internal_acx.getPosition();
                    zzLA = com_google_android_gms_internal_acx.zzLA();
                    switch (zzLA) {
                        case 0:
                        case 1:
                        case 2:
                            this.zzctR = zzLA;
                            break;
                        default:
                            com_google_android_gms_internal_acx.zzcp(zzb);
                            zza(com_google_android_gms_internal_acx, zzLy);
                            continue;
                    }
                case 160:
                    zzb = adj.zzb(com_google_android_gms_internal_acx, 160);
                    zzLy = this.zzctS == null ? 0 : this.zzctS.length;
                    obj = new int[(zzb + zzLy)];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzctS, 0, obj, 0, zzLy);
                    }
                    while (zzLy < obj.length - 1) {
                        obj[zzLy] = com_google_android_gms_internal_acx.zzLA();
                        com_google_android_gms_internal_acx.zzLy();
                        zzLy++;
                    }
                    obj[zzLy] = com_google_android_gms_internal_acx.zzLA();
                    this.zzctS = obj;
                    continue;
                case 162:
                    zzLA = com_google_android_gms_internal_acx.zzcn(com_google_android_gms_internal_acx.zzLD());
                    zzb = com_google_android_gms_internal_acx.getPosition();
                    zzLy = 0;
                    while (com_google_android_gms_internal_acx.zzLI() > 0) {
                        com_google_android_gms_internal_acx.zzLA();
                        zzLy++;
                    }
                    com_google_android_gms_internal_acx.zzcp(zzb);
                    zzb = this.zzctS == null ? 0 : this.zzctS.length;
                    Object obj2 = new int[(zzLy + zzb)];
                    if (zzb != 0) {
                        System.arraycopy(this.zzctS, 0, obj2, 0, zzb);
                    }
                    while (zzb < obj2.length) {
                        obj2[zzb] = com_google_android_gms_internal_acx.zzLA();
                        zzb++;
                    }
                    this.zzctS = obj2;
                    com_google_android_gms_internal_acx.zzco(zzLA);
                    continue;
                case 168:
                    this.zzctD = com_google_android_gms_internal_acx.zzLz();
                    continue;
                case 176:
                    this.zzctT = com_google_android_gms_internal_acx.zzLz();
                    continue;
                case 186:
                    if (this.zzcmI == null) {
                        this.zzcmI = new aec();
                    }
                    com_google_android_gms_internal_acx.zza(this.zzcmI);
                    continue;
                case 194:
                    this.zzctQ = com_google_android_gms_internal_acx.readString();
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
        if (this.zzctB != 0) {
            com_google_android_gms_internal_acy.zzb(1, this.zzctB);
        }
        if (!(this.tag == null || this.tag.equals(""))) {
            com_google_android_gms_internal_acy.zzl(2, this.tag);
        }
        if (this.zzctF != null && this.zzctF.length > 0) {
            for (adg com_google_android_gms_internal_adg : this.zzctF) {
                if (com_google_android_gms_internal_adg != null) {
                    com_google_android_gms_internal_acy.zza(3, com_google_android_gms_internal_adg);
                }
            }
        }
        if (!Arrays.equals(this.zzctG, adj.zzcst)) {
            com_google_android_gms_internal_acy.zzb(4, this.zzctG);
        }
        if (!Arrays.equals(this.zzctI, adj.zzcst)) {
            com_google_android_gms_internal_acy.zzb(6, this.zzctI);
        }
        if (this.zzctL != null) {
            com_google_android_gms_internal_acy.zza(7, this.zzctL);
        }
        if (!(this.zzctJ == null || this.zzctJ.equals(""))) {
            com_google_android_gms_internal_acy.zzl(8, this.zzctJ);
        }
        if (this.zzctH != null) {
            com_google_android_gms_internal_acy.zza(9, this.zzctH);
        }
        if (this.zzccZ) {
            com_google_android_gms_internal_acy.zzk(10, this.zzccZ);
        }
        if (this.zzctE != 0) {
            com_google_android_gms_internal_acy.zzr(11, this.zzctE);
        }
        if (this.zzrD != 0) {
            com_google_android_gms_internal_acy.zzr(12, this.zzrD);
        }
        if (!(this.zzctK == null || this.zzctK.equals(""))) {
            com_google_android_gms_internal_acy.zzl(13, this.zzctK);
        }
        if (!(this.zzctM == null || this.zzctM.equals(""))) {
            com_google_android_gms_internal_acy.zzl(14, this.zzctM);
        }
        if (this.zzctN != 180000) {
            com_google_android_gms_internal_acy.zzd(15, this.zzctN);
        }
        if (this.zzctO != null) {
            com_google_android_gms_internal_acy.zza(16, this.zzctO);
        }
        if (this.zzctC != 0) {
            com_google_android_gms_internal_acy.zzb(17, this.zzctC);
        }
        if (!Arrays.equals(this.zzctP, adj.zzcst)) {
            com_google_android_gms_internal_acy.zzb(18, this.zzctP);
        }
        if (this.zzctR != 0) {
            com_google_android_gms_internal_acy.zzr(19, this.zzctR);
        }
        if (this.zzctS != null && this.zzctS.length > 0) {
            while (i < this.zzctS.length) {
                com_google_android_gms_internal_acy.zzr(20, this.zzctS[i]);
                i++;
            }
        }
        if (this.zzctD != 0) {
            com_google_android_gms_internal_acy.zzb(21, this.zzctD);
        }
        if (this.zzctT != 0) {
            com_google_android_gms_internal_acy.zzb(22, this.zzctT);
        }
        if (this.zzcmI != null) {
            com_google_android_gms_internal_acy.zza(23, this.zzcmI);
        }
        if (!(this.zzctQ == null || this.zzctQ.equals(""))) {
            com_google_android_gms_internal_acy.zzl(24, this.zzctQ);
        }
        super.zza(com_google_android_gms_internal_acy);
    }

    protected final int zzn() {
        int i;
        int i2 = 0;
        int zzn = super.zzn();
        if (this.zzctB != 0) {
            zzn += acy.zze(1, this.zzctB);
        }
        if (!(this.tag == null || this.tag.equals(""))) {
            zzn += acy.zzm(2, this.tag);
        }
        if (this.zzctF != null && this.zzctF.length > 0) {
            i = zzn;
            for (adg com_google_android_gms_internal_adg : this.zzctF) {
                if (com_google_android_gms_internal_adg != null) {
                    i += acy.zzb(3, com_google_android_gms_internal_adg);
                }
            }
            zzn = i;
        }
        if (!Arrays.equals(this.zzctG, adj.zzcst)) {
            zzn += acy.zzc(4, this.zzctG);
        }
        if (!Arrays.equals(this.zzctI, adj.zzcst)) {
            zzn += acy.zzc(6, this.zzctI);
        }
        if (this.zzctL != null) {
            zzn += acy.zzb(7, this.zzctL);
        }
        if (!(this.zzctJ == null || this.zzctJ.equals(""))) {
            zzn += acy.zzm(8, this.zzctJ);
        }
        if (this.zzctH != null) {
            zzn += acy.zzb(9, this.zzctH);
        }
        if (this.zzccZ) {
            zzn += acy.zzct(10) + 1;
        }
        if (this.zzctE != 0) {
            zzn += acy.zzs(11, this.zzctE);
        }
        if (this.zzrD != 0) {
            zzn += acy.zzs(12, this.zzrD);
        }
        if (!(this.zzctK == null || this.zzctK.equals(""))) {
            zzn += acy.zzm(13, this.zzctK);
        }
        if (!(this.zzctM == null || this.zzctM.equals(""))) {
            zzn += acy.zzm(14, this.zzctM);
        }
        if (this.zzctN != 180000) {
            zzn += acy.zzf(15, this.zzctN);
        }
        if (this.zzctO != null) {
            zzn += acy.zzb(16, this.zzctO);
        }
        if (this.zzctC != 0) {
            zzn += acy.zze(17, this.zzctC);
        }
        if (!Arrays.equals(this.zzctP, adj.zzcst)) {
            zzn += acy.zzc(18, this.zzctP);
        }
        if (this.zzctR != 0) {
            zzn += acy.zzs(19, this.zzctR);
        }
        if (this.zzctS != null && this.zzctS.length > 0) {
            i = 0;
            while (i2 < this.zzctS.length) {
                i += acy.zzcr(this.zzctS[i2]);
                i2++;
            }
            zzn = (zzn + i) + (this.zzctS.length * 2);
        }
        if (this.zzctD != 0) {
            zzn += acy.zze(21, this.zzctD);
        }
        if (this.zzctT != 0) {
            zzn += acy.zze(22, this.zzctT);
        }
        if (this.zzcmI != null) {
            zzn += acy.zzb(23, this.zzcmI);
        }
        return (this.zzctQ == null || this.zzctQ.equals("")) ? zzn : zzn + acy.zzm(24, this.zzctQ);
    }
}

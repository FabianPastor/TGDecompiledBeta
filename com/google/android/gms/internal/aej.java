package com.google.android.gms.internal;

import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;
import java.util.Arrays;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;

public final class aej extends adj<aej> implements Cloneable {
    private String tag;
    private boolean zzccZ;
    private ael zzcmM;
    public long zzctF;
    public long zzctG;
    private long zzctH;
    public int zzctI;
    private aek[] zzctJ;
    private byte[] zzctK;
    private aeh zzctL;
    public byte[] zzctM;
    private String zzctN;
    private String zzctO;
    private aeg zzctP;
    private String zzctQ;
    public long zzctR;
    private aei zzctS;
    public byte[] zzctT;
    private String zzctU;
    private int zzctV;
    private int[] zzctW;
    private long zzctX;
    public int zzrB;

    public aej() {
        this.zzctF = 0;
        this.zzctG = 0;
        this.zzctH = 0;
        this.tag = "";
        this.zzctI = 0;
        this.zzrB = 0;
        this.zzccZ = false;
        this.zzctJ = aek.zzMf();
        this.zzctK = ads.zzcsx;
        this.zzctL = null;
        this.zzctM = ads.zzcsx;
        this.zzctN = "";
        this.zzctO = "";
        this.zzctP = null;
        this.zzctQ = "";
        this.zzctR = 180000;
        this.zzctS = null;
        this.zzctT = ads.zzcsx;
        this.zzctU = "";
        this.zzctV = 0;
        this.zzctW = ads.zzcsr;
        this.zzctX = 0;
        this.zzcmM = null;
        this.zzcsd = null;
        this.zzcsm = -1;
    }

    private final aej zzMe() {
        try {
            aej com_google_android_gms_internal_aej = (aej) super.zzLO();
            if (this.zzctJ != null && this.zzctJ.length > 0) {
                com_google_android_gms_internal_aej.zzctJ = new aek[this.zzctJ.length];
                for (int i = 0; i < this.zzctJ.length; i++) {
                    if (this.zzctJ[i] != null) {
                        com_google_android_gms_internal_aej.zzctJ[i] = (aek) this.zzctJ[i].clone();
                    }
                }
            }
            if (this.zzctL != null) {
                com_google_android_gms_internal_aej.zzctL = (aeh) this.zzctL.clone();
            }
            if (this.zzctP != null) {
                com_google_android_gms_internal_aej.zzctP = (aeg) this.zzctP.clone();
            }
            if (this.zzctS != null) {
                com_google_android_gms_internal_aej.zzctS = (aei) this.zzctS.clone();
            }
            if (this.zzctW != null && this.zzctW.length > 0) {
                com_google_android_gms_internal_aej.zzctW = (int[]) this.zzctW.clone();
            }
            if (this.zzcmM != null) {
                com_google_android_gms_internal_aej.zzcmM = (ael) this.zzcmM.clone();
            }
            return com_google_android_gms_internal_aej;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public final /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzMe();
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof aej)) {
            return false;
        }
        aej com_google_android_gms_internal_aej = (aej) obj;
        if (this.zzctF != com_google_android_gms_internal_aej.zzctF) {
            return false;
        }
        if (this.zzctG != com_google_android_gms_internal_aej.zzctG) {
            return false;
        }
        if (this.zzctH != com_google_android_gms_internal_aej.zzctH) {
            return false;
        }
        if (this.tag == null) {
            if (com_google_android_gms_internal_aej.tag != null) {
                return false;
            }
        } else if (!this.tag.equals(com_google_android_gms_internal_aej.tag)) {
            return false;
        }
        if (this.zzctI != com_google_android_gms_internal_aej.zzctI) {
            return false;
        }
        if (this.zzrB != com_google_android_gms_internal_aej.zzrB) {
            return false;
        }
        if (this.zzccZ != com_google_android_gms_internal_aej.zzccZ) {
            return false;
        }
        if (!adn.equals(this.zzctJ, com_google_android_gms_internal_aej.zzctJ)) {
            return false;
        }
        if (!Arrays.equals(this.zzctK, com_google_android_gms_internal_aej.zzctK)) {
            return false;
        }
        if (this.zzctL == null) {
            if (com_google_android_gms_internal_aej.zzctL != null) {
                return false;
            }
        } else if (!this.zzctL.equals(com_google_android_gms_internal_aej.zzctL)) {
            return false;
        }
        if (!Arrays.equals(this.zzctM, com_google_android_gms_internal_aej.zzctM)) {
            return false;
        }
        if (this.zzctN == null) {
            if (com_google_android_gms_internal_aej.zzctN != null) {
                return false;
            }
        } else if (!this.zzctN.equals(com_google_android_gms_internal_aej.zzctN)) {
            return false;
        }
        if (this.zzctO == null) {
            if (com_google_android_gms_internal_aej.zzctO != null) {
                return false;
            }
        } else if (!this.zzctO.equals(com_google_android_gms_internal_aej.zzctO)) {
            return false;
        }
        if (this.zzctP == null) {
            if (com_google_android_gms_internal_aej.zzctP != null) {
                return false;
            }
        } else if (!this.zzctP.equals(com_google_android_gms_internal_aej.zzctP)) {
            return false;
        }
        if (this.zzctQ == null) {
            if (com_google_android_gms_internal_aej.zzctQ != null) {
                return false;
            }
        } else if (!this.zzctQ.equals(com_google_android_gms_internal_aej.zzctQ)) {
            return false;
        }
        if (this.zzctR != com_google_android_gms_internal_aej.zzctR) {
            return false;
        }
        if (this.zzctS == null) {
            if (com_google_android_gms_internal_aej.zzctS != null) {
                return false;
            }
        } else if (!this.zzctS.equals(com_google_android_gms_internal_aej.zzctS)) {
            return false;
        }
        if (!Arrays.equals(this.zzctT, com_google_android_gms_internal_aej.zzctT)) {
            return false;
        }
        if (this.zzctU == null) {
            if (com_google_android_gms_internal_aej.zzctU != null) {
                return false;
            }
        } else if (!this.zzctU.equals(com_google_android_gms_internal_aej.zzctU)) {
            return false;
        }
        if (this.zzctV != com_google_android_gms_internal_aej.zzctV) {
            return false;
        }
        if (!adn.equals(this.zzctW, com_google_android_gms_internal_aej.zzctW)) {
            return false;
        }
        if (this.zzctX != com_google_android_gms_internal_aej.zzctX) {
            return false;
        }
        if (this.zzcmM == null) {
            if (com_google_android_gms_internal_aej.zzcmM != null) {
                return false;
            }
        } else if (!this.zzcmM.equals(com_google_android_gms_internal_aej.zzcmM)) {
            return false;
        }
        return (this.zzcsd == null || this.zzcsd.isEmpty()) ? com_google_android_gms_internal_aej.zzcsd == null || com_google_android_gms_internal_aej.zzcsd.isEmpty() : this.zzcsd.equals(com_google_android_gms_internal_aej.zzcsd);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzcmM == null ? 0 : this.zzcmM.hashCode()) + (((((((((this.zzctU == null ? 0 : this.zzctU.hashCode()) + (((((this.zzctS == null ? 0 : this.zzctS.hashCode()) + (((((this.zzctQ == null ? 0 : this.zzctQ.hashCode()) + (((this.zzctP == null ? 0 : this.zzctP.hashCode()) + (((this.zzctO == null ? 0 : this.zzctO.hashCode()) + (((this.zzctN == null ? 0 : this.zzctN.hashCode()) + (((((this.zzctL == null ? 0 : this.zzctL.hashCode()) + (((((((this.zzccZ ? 1231 : 1237) + (((((((this.tag == null ? 0 : this.tag.hashCode()) + ((((((((getClass().getName().hashCode() + 527) * 31) + ((int) (this.zzctF ^ (this.zzctF >>> 32)))) * 31) + ((int) (this.zzctG ^ (this.zzctG >>> 32)))) * 31) + ((int) (this.zzctH ^ (this.zzctH >>> 32)))) * 31)) * 31) + this.zzctI) * 31) + this.zzrB) * 31)) * 31) + adn.hashCode(this.zzctJ)) * 31) + Arrays.hashCode(this.zzctK)) * 31)) * 31) + Arrays.hashCode(this.zzctM)) * 31)) * 31)) * 31)) * 31)) * 31) + ((int) (this.zzctR ^ (this.zzctR >>> 32)))) * 31)) * 31) + Arrays.hashCode(this.zzctT)) * 31)) * 31) + this.zzctV) * 31) + adn.hashCode(this.zzctW)) * 31) + ((int) (this.zzctX ^ (this.zzctX >>> 32)))) * 31)) * 31;
        if (!(this.zzcsd == null || this.zzcsd.isEmpty())) {
            i = this.zzcsd.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ adj zzLO() throws CloneNotSupportedException {
        return (aej) clone();
    }

    public final /* synthetic */ adp zzLP() throws CloneNotSupportedException {
        return (aej) clone();
    }

    public final /* synthetic */ adp zza(adg com_google_android_gms_internal_adg) throws IOException {
        while (true) {
            int zzLB = com_google_android_gms_internal_adg.zzLB();
            int zzb;
            Object obj;
            int zzLD;
            switch (zzLB) {
                case 0:
                    break;
                case 8:
                    this.zzctF = com_google_android_gms_internal_adg.zzLC();
                    continue;
                case 18:
                    this.tag = com_google_android_gms_internal_adg.readString();
                    continue;
                case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                    zzb = ads.zzb(com_google_android_gms_internal_adg, 26);
                    zzLB = this.zzctJ == null ? 0 : this.zzctJ.length;
                    obj = new aek[(zzb + zzLB)];
                    if (zzLB != 0) {
                        System.arraycopy(this.zzctJ, 0, obj, 0, zzLB);
                    }
                    while (zzLB < obj.length - 1) {
                        obj[zzLB] = new aek();
                        com_google_android_gms_internal_adg.zza(obj[zzLB]);
                        com_google_android_gms_internal_adg.zzLB();
                        zzLB++;
                    }
                    obj[zzLB] = new aek();
                    com_google_android_gms_internal_adg.zza(obj[zzLB]);
                    this.zzctJ = obj;
                    continue;
                case 34:
                    this.zzctK = com_google_android_gms_internal_adg.readBytes();
                    continue;
                case 50:
                    this.zzctM = com_google_android_gms_internal_adg.readBytes();
                    continue;
                case 58:
                    if (this.zzctP == null) {
                        this.zzctP = new aeg();
                    }
                    com_google_android_gms_internal_adg.zza(this.zzctP);
                    continue;
                case SecretChatHelper.CURRENT_SECRET_CHAT_LAYER /*66*/:
                    this.zzctN = com_google_android_gms_internal_adg.readString();
                    continue;
                case 74:
                    if (this.zzctL == null) {
                        this.zzctL = new aeh();
                    }
                    com_google_android_gms_internal_adg.zza(this.zzctL);
                    continue;
                case 80:
                    this.zzccZ = com_google_android_gms_internal_adg.zzLE();
                    continue;
                case 88:
                    this.zzctI = com_google_android_gms_internal_adg.zzLD();
                    continue;
                case 96:
                    this.zzrB = com_google_android_gms_internal_adg.zzLD();
                    continue;
                case 106:
                    this.zzctO = com_google_android_gms_internal_adg.readString();
                    continue;
                case 114:
                    this.zzctQ = com_google_android_gms_internal_adg.readString();
                    continue;
                case 120:
                    this.zzctR = com_google_android_gms_internal_adg.zzLF();
                    continue;
                case TsExtractor.TS_STREAM_TYPE_HDMV_DTS /*130*/:
                    if (this.zzctS == null) {
                        this.zzctS = new aei();
                    }
                    com_google_android_gms_internal_adg.zza(this.zzctS);
                    continue;
                case 136:
                    this.zzctG = com_google_android_gms_internal_adg.zzLC();
                    continue;
                case 146:
                    this.zzctT = com_google_android_gms_internal_adg.readBytes();
                    continue;
                case 152:
                    zzb = com_google_android_gms_internal_adg.getPosition();
                    zzLD = com_google_android_gms_internal_adg.zzLD();
                    switch (zzLD) {
                        case 0:
                        case 1:
                        case 2:
                            this.zzctV = zzLD;
                            break;
                        default:
                            com_google_android_gms_internal_adg.zzcp(zzb);
                            zza(com_google_android_gms_internal_adg, zzLB);
                            continue;
                    }
                case 160:
                    zzb = ads.zzb(com_google_android_gms_internal_adg, 160);
                    zzLB = this.zzctW == null ? 0 : this.zzctW.length;
                    obj = new int[(zzb + zzLB)];
                    if (zzLB != 0) {
                        System.arraycopy(this.zzctW, 0, obj, 0, zzLB);
                    }
                    while (zzLB < obj.length - 1) {
                        obj[zzLB] = com_google_android_gms_internal_adg.zzLD();
                        com_google_android_gms_internal_adg.zzLB();
                        zzLB++;
                    }
                    obj[zzLB] = com_google_android_gms_internal_adg.zzLD();
                    this.zzctW = obj;
                    continue;
                case 162:
                    zzLD = com_google_android_gms_internal_adg.zzcn(com_google_android_gms_internal_adg.zzLG());
                    zzb = com_google_android_gms_internal_adg.getPosition();
                    zzLB = 0;
                    while (com_google_android_gms_internal_adg.zzLL() > 0) {
                        com_google_android_gms_internal_adg.zzLD();
                        zzLB++;
                    }
                    com_google_android_gms_internal_adg.zzcp(zzb);
                    zzb = this.zzctW == null ? 0 : this.zzctW.length;
                    Object obj2 = new int[(zzLB + zzb)];
                    if (zzb != 0) {
                        System.arraycopy(this.zzctW, 0, obj2, 0, zzb);
                    }
                    while (zzb < obj2.length) {
                        obj2[zzb] = com_google_android_gms_internal_adg.zzLD();
                        zzb++;
                    }
                    this.zzctW = obj2;
                    com_google_android_gms_internal_adg.zzco(zzLD);
                    continue;
                case 168:
                    this.zzctH = com_google_android_gms_internal_adg.zzLC();
                    continue;
                case 176:
                    this.zzctX = com_google_android_gms_internal_adg.zzLC();
                    continue;
                case 186:
                    if (this.zzcmM == null) {
                        this.zzcmM = new ael();
                    }
                    com_google_android_gms_internal_adg.zza(this.zzcmM);
                    continue;
                case 194:
                    this.zzctU = com_google_android_gms_internal_adg.readString();
                    continue;
                default:
                    if (!super.zza(com_google_android_gms_internal_adg, zzLB)) {
                        break;
                    }
                    continue;
            }
            return this;
        }
    }

    public final void zza(adh com_google_android_gms_internal_adh) throws IOException {
        int i = 0;
        if (this.zzctF != 0) {
            com_google_android_gms_internal_adh.zzb(1, this.zzctF);
        }
        if (!(this.tag == null || this.tag.equals(""))) {
            com_google_android_gms_internal_adh.zzl(2, this.tag);
        }
        if (this.zzctJ != null && this.zzctJ.length > 0) {
            for (adp com_google_android_gms_internal_adp : this.zzctJ) {
                if (com_google_android_gms_internal_adp != null) {
                    com_google_android_gms_internal_adh.zza(3, com_google_android_gms_internal_adp);
                }
            }
        }
        if (!Arrays.equals(this.zzctK, ads.zzcsx)) {
            com_google_android_gms_internal_adh.zzb(4, this.zzctK);
        }
        if (!Arrays.equals(this.zzctM, ads.zzcsx)) {
            com_google_android_gms_internal_adh.zzb(6, this.zzctM);
        }
        if (this.zzctP != null) {
            com_google_android_gms_internal_adh.zza(7, this.zzctP);
        }
        if (!(this.zzctN == null || this.zzctN.equals(""))) {
            com_google_android_gms_internal_adh.zzl(8, this.zzctN);
        }
        if (this.zzctL != null) {
            com_google_android_gms_internal_adh.zza(9, this.zzctL);
        }
        if (this.zzccZ) {
            com_google_android_gms_internal_adh.zzk(10, this.zzccZ);
        }
        if (this.zzctI != 0) {
            com_google_android_gms_internal_adh.zzr(11, this.zzctI);
        }
        if (this.zzrB != 0) {
            com_google_android_gms_internal_adh.zzr(12, this.zzrB);
        }
        if (!(this.zzctO == null || this.zzctO.equals(""))) {
            com_google_android_gms_internal_adh.zzl(13, this.zzctO);
        }
        if (!(this.zzctQ == null || this.zzctQ.equals(""))) {
            com_google_android_gms_internal_adh.zzl(14, this.zzctQ);
        }
        if (this.zzctR != 180000) {
            com_google_android_gms_internal_adh.zzd(15, this.zzctR);
        }
        if (this.zzctS != null) {
            com_google_android_gms_internal_adh.zza(16, this.zzctS);
        }
        if (this.zzctG != 0) {
            com_google_android_gms_internal_adh.zzb(17, this.zzctG);
        }
        if (!Arrays.equals(this.zzctT, ads.zzcsx)) {
            com_google_android_gms_internal_adh.zzb(18, this.zzctT);
        }
        if (this.zzctV != 0) {
            com_google_android_gms_internal_adh.zzr(19, this.zzctV);
        }
        if (this.zzctW != null && this.zzctW.length > 0) {
            while (i < this.zzctW.length) {
                com_google_android_gms_internal_adh.zzr(20, this.zzctW[i]);
                i++;
            }
        }
        if (this.zzctH != 0) {
            com_google_android_gms_internal_adh.zzb(21, this.zzctH);
        }
        if (this.zzctX != 0) {
            com_google_android_gms_internal_adh.zzb(22, this.zzctX);
        }
        if (this.zzcmM != null) {
            com_google_android_gms_internal_adh.zza(23, this.zzcmM);
        }
        if (!(this.zzctU == null || this.zzctU.equals(""))) {
            com_google_android_gms_internal_adh.zzl(24, this.zzctU);
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int i;
        int i2 = 0;
        int zzn = super.zzn();
        if (this.zzctF != 0) {
            zzn += adh.zze(1, this.zzctF);
        }
        if (!(this.tag == null || this.tag.equals(""))) {
            zzn += adh.zzm(2, this.tag);
        }
        if (this.zzctJ != null && this.zzctJ.length > 0) {
            i = zzn;
            for (adp com_google_android_gms_internal_adp : this.zzctJ) {
                if (com_google_android_gms_internal_adp != null) {
                    i += adh.zzb(3, com_google_android_gms_internal_adp);
                }
            }
            zzn = i;
        }
        if (!Arrays.equals(this.zzctK, ads.zzcsx)) {
            zzn += adh.zzc(4, this.zzctK);
        }
        if (!Arrays.equals(this.zzctM, ads.zzcsx)) {
            zzn += adh.zzc(6, this.zzctM);
        }
        if (this.zzctP != null) {
            zzn += adh.zzb(7, this.zzctP);
        }
        if (!(this.zzctN == null || this.zzctN.equals(""))) {
            zzn += adh.zzm(8, this.zzctN);
        }
        if (this.zzctL != null) {
            zzn += adh.zzb(9, this.zzctL);
        }
        if (this.zzccZ) {
            zzn += adh.zzct(10) + 1;
        }
        if (this.zzctI != 0) {
            zzn += adh.zzs(11, this.zzctI);
        }
        if (this.zzrB != 0) {
            zzn += adh.zzs(12, this.zzrB);
        }
        if (!(this.zzctO == null || this.zzctO.equals(""))) {
            zzn += adh.zzm(13, this.zzctO);
        }
        if (!(this.zzctQ == null || this.zzctQ.equals(""))) {
            zzn += adh.zzm(14, this.zzctQ);
        }
        if (this.zzctR != 180000) {
            zzn += adh.zzf(15, this.zzctR);
        }
        if (this.zzctS != null) {
            zzn += adh.zzb(16, this.zzctS);
        }
        if (this.zzctG != 0) {
            zzn += adh.zze(17, this.zzctG);
        }
        if (!Arrays.equals(this.zzctT, ads.zzcsx)) {
            zzn += adh.zzc(18, this.zzctT);
        }
        if (this.zzctV != 0) {
            zzn += adh.zzs(19, this.zzctV);
        }
        if (this.zzctW != null && this.zzctW.length > 0) {
            i = 0;
            while (i2 < this.zzctW.length) {
                i += adh.zzcr(this.zzctW[i2]);
                i2++;
            }
            zzn = (zzn + i) + (this.zzctW.length * 2);
        }
        if (this.zzctH != 0) {
            zzn += adh.zze(21, this.zzctH);
        }
        if (this.zzctX != 0) {
            zzn += adh.zze(22, this.zzctX);
        }
        if (this.zzcmM != null) {
            zzn += adh.zzb(23, this.zzcmM);
        }
        return (this.zzctU == null || this.zzctU.equals("")) ? zzn : zzn + adh.zzm(24, this.zzctU);
    }
}

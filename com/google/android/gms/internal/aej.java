package com.google.android.gms.internal;

import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;

public final class aej extends adj<aej> implements Cloneable {
    private String tag;
    private boolean zzccZ;
    private ael zzcmX;
    public long zzctQ;
    public long zzctR;
    private long zzctS;
    public int zzctT;
    private aek[] zzctU;
    private byte[] zzctV;
    private aeh zzctW;
    public byte[] zzctX;
    private String zzctY;
    private String zzctZ;
    private aeg zzcua;
    private String zzcub;
    public long zzcuc;
    private aei zzcud;
    public byte[] zzcue;
    private String zzcuf;
    private int zzcug;
    private int[] zzcuh;
    private long zzcui;
    public int zzrB;

    public aej() {
        this.zzctQ = 0;
        this.zzctR = 0;
        this.zzctS = 0;
        this.tag = "";
        this.zzctT = 0;
        this.zzrB = 0;
        this.zzccZ = false;
        this.zzctU = aek.zzMe();
        this.zzctV = ads.zzcsI;
        this.zzctW = null;
        this.zzctX = ads.zzcsI;
        this.zzctY = "";
        this.zzctZ = "";
        this.zzcua = null;
        this.zzcub = "";
        this.zzcuc = 180000;
        this.zzcud = null;
        this.zzcue = ads.zzcsI;
        this.zzcuf = "";
        this.zzcug = 0;
        this.zzcuh = ads.zzcsC;
        this.zzcui = 0;
        this.zzcmX = null;
        this.zzcso = null;
        this.zzcsx = -1;
    }

    private final aej zzMd() {
        try {
            aej com_google_android_gms_internal_aej = (aej) super.zzLN();
            if (this.zzctU != null && this.zzctU.length > 0) {
                com_google_android_gms_internal_aej.zzctU = new aek[this.zzctU.length];
                for (int i = 0; i < this.zzctU.length; i++) {
                    if (this.zzctU[i] != null) {
                        com_google_android_gms_internal_aej.zzctU[i] = (aek) this.zzctU[i].clone();
                    }
                }
            }
            if (this.zzctW != null) {
                com_google_android_gms_internal_aej.zzctW = (aeh) this.zzctW.clone();
            }
            if (this.zzcua != null) {
                com_google_android_gms_internal_aej.zzcua = (aeg) this.zzcua.clone();
            }
            if (this.zzcud != null) {
                com_google_android_gms_internal_aej.zzcud = (aei) this.zzcud.clone();
            }
            if (this.zzcuh != null && this.zzcuh.length > 0) {
                com_google_android_gms_internal_aej.zzcuh = (int[]) this.zzcuh.clone();
            }
            if (this.zzcmX != null) {
                com_google_android_gms_internal_aej.zzcmX = (ael) this.zzcmX.clone();
            }
            return com_google_android_gms_internal_aej;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public final /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzMd();
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof aej)) {
            return false;
        }
        aej com_google_android_gms_internal_aej = (aej) obj;
        if (this.zzctQ != com_google_android_gms_internal_aej.zzctQ) {
            return false;
        }
        if (this.zzctR != com_google_android_gms_internal_aej.zzctR) {
            return false;
        }
        if (this.zzctS != com_google_android_gms_internal_aej.zzctS) {
            return false;
        }
        if (this.tag == null) {
            if (com_google_android_gms_internal_aej.tag != null) {
                return false;
            }
        } else if (!this.tag.equals(com_google_android_gms_internal_aej.tag)) {
            return false;
        }
        if (this.zzctT != com_google_android_gms_internal_aej.zzctT) {
            return false;
        }
        if (this.zzrB != com_google_android_gms_internal_aej.zzrB) {
            return false;
        }
        if (this.zzccZ != com_google_android_gms_internal_aej.zzccZ) {
            return false;
        }
        if (!adn.equals(this.zzctU, com_google_android_gms_internal_aej.zzctU)) {
            return false;
        }
        if (!Arrays.equals(this.zzctV, com_google_android_gms_internal_aej.zzctV)) {
            return false;
        }
        if (this.zzctW == null) {
            if (com_google_android_gms_internal_aej.zzctW != null) {
                return false;
            }
        } else if (!this.zzctW.equals(com_google_android_gms_internal_aej.zzctW)) {
            return false;
        }
        if (!Arrays.equals(this.zzctX, com_google_android_gms_internal_aej.zzctX)) {
            return false;
        }
        if (this.zzctY == null) {
            if (com_google_android_gms_internal_aej.zzctY != null) {
                return false;
            }
        } else if (!this.zzctY.equals(com_google_android_gms_internal_aej.zzctY)) {
            return false;
        }
        if (this.zzctZ == null) {
            if (com_google_android_gms_internal_aej.zzctZ != null) {
                return false;
            }
        } else if (!this.zzctZ.equals(com_google_android_gms_internal_aej.zzctZ)) {
            return false;
        }
        if (this.zzcua == null) {
            if (com_google_android_gms_internal_aej.zzcua != null) {
                return false;
            }
        } else if (!this.zzcua.equals(com_google_android_gms_internal_aej.zzcua)) {
            return false;
        }
        if (this.zzcub == null) {
            if (com_google_android_gms_internal_aej.zzcub != null) {
                return false;
            }
        } else if (!this.zzcub.equals(com_google_android_gms_internal_aej.zzcub)) {
            return false;
        }
        if (this.zzcuc != com_google_android_gms_internal_aej.zzcuc) {
            return false;
        }
        if (this.zzcud == null) {
            if (com_google_android_gms_internal_aej.zzcud != null) {
                return false;
            }
        } else if (!this.zzcud.equals(com_google_android_gms_internal_aej.zzcud)) {
            return false;
        }
        if (!Arrays.equals(this.zzcue, com_google_android_gms_internal_aej.zzcue)) {
            return false;
        }
        if (this.zzcuf == null) {
            if (com_google_android_gms_internal_aej.zzcuf != null) {
                return false;
            }
        } else if (!this.zzcuf.equals(com_google_android_gms_internal_aej.zzcuf)) {
            return false;
        }
        if (this.zzcug != com_google_android_gms_internal_aej.zzcug) {
            return false;
        }
        if (!adn.equals(this.zzcuh, com_google_android_gms_internal_aej.zzcuh)) {
            return false;
        }
        if (this.zzcui != com_google_android_gms_internal_aej.zzcui) {
            return false;
        }
        if (this.zzcmX == null) {
            if (com_google_android_gms_internal_aej.zzcmX != null) {
                return false;
            }
        } else if (!this.zzcmX.equals(com_google_android_gms_internal_aej.zzcmX)) {
            return false;
        }
        return (this.zzcso == null || this.zzcso.isEmpty()) ? com_google_android_gms_internal_aej.zzcso == null || com_google_android_gms_internal_aej.zzcso.isEmpty() : this.zzcso.equals(com_google_android_gms_internal_aej.zzcso);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzcmX == null ? 0 : this.zzcmX.hashCode()) + (((((((((this.zzcuf == null ? 0 : this.zzcuf.hashCode()) + (((((this.zzcud == null ? 0 : this.zzcud.hashCode()) + (((((this.zzcub == null ? 0 : this.zzcub.hashCode()) + (((this.zzcua == null ? 0 : this.zzcua.hashCode()) + (((this.zzctZ == null ? 0 : this.zzctZ.hashCode()) + (((this.zzctY == null ? 0 : this.zzctY.hashCode()) + (((((this.zzctW == null ? 0 : this.zzctW.hashCode()) + (((((((this.zzccZ ? 1231 : 1237) + (((((((this.tag == null ? 0 : this.tag.hashCode()) + ((((((((getClass().getName().hashCode() + 527) * 31) + ((int) (this.zzctQ ^ (this.zzctQ >>> 32)))) * 31) + ((int) (this.zzctR ^ (this.zzctR >>> 32)))) * 31) + ((int) (this.zzctS ^ (this.zzctS >>> 32)))) * 31)) * 31) + this.zzctT) * 31) + this.zzrB) * 31)) * 31) + adn.hashCode(this.zzctU)) * 31) + Arrays.hashCode(this.zzctV)) * 31)) * 31) + Arrays.hashCode(this.zzctX)) * 31)) * 31)) * 31)) * 31)) * 31) + ((int) (this.zzcuc ^ (this.zzcuc >>> 32)))) * 31)) * 31) + Arrays.hashCode(this.zzcue)) * 31)) * 31) + this.zzcug) * 31) + adn.hashCode(this.zzcuh)) * 31) + ((int) (this.zzcui ^ (this.zzcui >>> 32)))) * 31)) * 31;
        if (!(this.zzcso == null || this.zzcso.isEmpty())) {
            i = this.zzcso.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ adj zzLN() throws CloneNotSupportedException {
        return (aej) clone();
    }

    public final /* synthetic */ adp zzLO() throws CloneNotSupportedException {
        return (aej) clone();
    }

    public final /* synthetic */ adp zza(adg com_google_android_gms_internal_adg) throws IOException {
        while (true) {
            int zzLA = com_google_android_gms_internal_adg.zzLA();
            int zzb;
            Object obj;
            int zzLC;
            switch (zzLA) {
                case 0:
                    break;
                case 8:
                    this.zzctQ = com_google_android_gms_internal_adg.zzLB();
                    continue;
                case 18:
                    this.tag = com_google_android_gms_internal_adg.readString();
                    continue;
                case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                    zzb = ads.zzb(com_google_android_gms_internal_adg, 26);
                    zzLA = this.zzctU == null ? 0 : this.zzctU.length;
                    obj = new aek[(zzb + zzLA)];
                    if (zzLA != 0) {
                        System.arraycopy(this.zzctU, 0, obj, 0, zzLA);
                    }
                    while (zzLA < obj.length - 1) {
                        obj[zzLA] = new aek();
                        com_google_android_gms_internal_adg.zza(obj[zzLA]);
                        com_google_android_gms_internal_adg.zzLA();
                        zzLA++;
                    }
                    obj[zzLA] = new aek();
                    com_google_android_gms_internal_adg.zza(obj[zzLA]);
                    this.zzctU = obj;
                    continue;
                case 34:
                    this.zzctV = com_google_android_gms_internal_adg.readBytes();
                    continue;
                case 50:
                    this.zzctX = com_google_android_gms_internal_adg.readBytes();
                    continue;
                case 58:
                    if (this.zzcua == null) {
                        this.zzcua = new aeg();
                    }
                    com_google_android_gms_internal_adg.zza(this.zzcua);
                    continue;
                case 66:
                    this.zzctY = com_google_android_gms_internal_adg.readString();
                    continue;
                case 74:
                    if (this.zzctW == null) {
                        this.zzctW = new aeh();
                    }
                    com_google_android_gms_internal_adg.zza(this.zzctW);
                    continue;
                case 80:
                    this.zzccZ = com_google_android_gms_internal_adg.zzLD();
                    continue;
                case 88:
                    this.zzctT = com_google_android_gms_internal_adg.zzLC();
                    continue;
                case 96:
                    this.zzrB = com_google_android_gms_internal_adg.zzLC();
                    continue;
                case 106:
                    this.zzctZ = com_google_android_gms_internal_adg.readString();
                    continue;
                case 114:
                    this.zzcub = com_google_android_gms_internal_adg.readString();
                    continue;
                case 120:
                    this.zzcuc = com_google_android_gms_internal_adg.zzLE();
                    continue;
                case TsExtractor.TS_STREAM_TYPE_HDMV_DTS /*130*/:
                    if (this.zzcud == null) {
                        this.zzcud = new aei();
                    }
                    com_google_android_gms_internal_adg.zza(this.zzcud);
                    continue;
                case 136:
                    this.zzctR = com_google_android_gms_internal_adg.zzLB();
                    continue;
                case 146:
                    this.zzcue = com_google_android_gms_internal_adg.readBytes();
                    continue;
                case 152:
                    zzb = com_google_android_gms_internal_adg.getPosition();
                    zzLC = com_google_android_gms_internal_adg.zzLC();
                    switch (zzLC) {
                        case 0:
                        case 1:
                        case 2:
                            this.zzcug = zzLC;
                            break;
                        default:
                            com_google_android_gms_internal_adg.zzcp(zzb);
                            zza(com_google_android_gms_internal_adg, zzLA);
                            continue;
                    }
                case 160:
                    zzb = ads.zzb(com_google_android_gms_internal_adg, 160);
                    zzLA = this.zzcuh == null ? 0 : this.zzcuh.length;
                    obj = new int[(zzb + zzLA)];
                    if (zzLA != 0) {
                        System.arraycopy(this.zzcuh, 0, obj, 0, zzLA);
                    }
                    while (zzLA < obj.length - 1) {
                        obj[zzLA] = com_google_android_gms_internal_adg.zzLC();
                        com_google_android_gms_internal_adg.zzLA();
                        zzLA++;
                    }
                    obj[zzLA] = com_google_android_gms_internal_adg.zzLC();
                    this.zzcuh = obj;
                    continue;
                case 162:
                    zzLC = com_google_android_gms_internal_adg.zzcn(com_google_android_gms_internal_adg.zzLF());
                    zzb = com_google_android_gms_internal_adg.getPosition();
                    zzLA = 0;
                    while (com_google_android_gms_internal_adg.zzLK() > 0) {
                        com_google_android_gms_internal_adg.zzLC();
                        zzLA++;
                    }
                    com_google_android_gms_internal_adg.zzcp(zzb);
                    zzb = this.zzcuh == null ? 0 : this.zzcuh.length;
                    Object obj2 = new int[(zzLA + zzb)];
                    if (zzb != 0) {
                        System.arraycopy(this.zzcuh, 0, obj2, 0, zzb);
                    }
                    while (zzb < obj2.length) {
                        obj2[zzb] = com_google_android_gms_internal_adg.zzLC();
                        zzb++;
                    }
                    this.zzcuh = obj2;
                    com_google_android_gms_internal_adg.zzco(zzLC);
                    continue;
                case 168:
                    this.zzctS = com_google_android_gms_internal_adg.zzLB();
                    continue;
                case 176:
                    this.zzcui = com_google_android_gms_internal_adg.zzLB();
                    continue;
                case 186:
                    if (this.zzcmX == null) {
                        this.zzcmX = new ael();
                    }
                    com_google_android_gms_internal_adg.zza(this.zzcmX);
                    continue;
                case 194:
                    this.zzcuf = com_google_android_gms_internal_adg.readString();
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
        if (this.zzctQ != 0) {
            com_google_android_gms_internal_adh.zzb(1, this.zzctQ);
        }
        if (!(this.tag == null || this.tag.equals(""))) {
            com_google_android_gms_internal_adh.zzl(2, this.tag);
        }
        if (this.zzctU != null && this.zzctU.length > 0) {
            for (adp com_google_android_gms_internal_adp : this.zzctU) {
                if (com_google_android_gms_internal_adp != null) {
                    com_google_android_gms_internal_adh.zza(3, com_google_android_gms_internal_adp);
                }
            }
        }
        if (!Arrays.equals(this.zzctV, ads.zzcsI)) {
            com_google_android_gms_internal_adh.zzb(4, this.zzctV);
        }
        if (!Arrays.equals(this.zzctX, ads.zzcsI)) {
            com_google_android_gms_internal_adh.zzb(6, this.zzctX);
        }
        if (this.zzcua != null) {
            com_google_android_gms_internal_adh.zza(7, this.zzcua);
        }
        if (!(this.zzctY == null || this.zzctY.equals(""))) {
            com_google_android_gms_internal_adh.zzl(8, this.zzctY);
        }
        if (this.zzctW != null) {
            com_google_android_gms_internal_adh.zza(9, this.zzctW);
        }
        if (this.zzccZ) {
            com_google_android_gms_internal_adh.zzk(10, this.zzccZ);
        }
        if (this.zzctT != 0) {
            com_google_android_gms_internal_adh.zzr(11, this.zzctT);
        }
        if (this.zzrB != 0) {
            com_google_android_gms_internal_adh.zzr(12, this.zzrB);
        }
        if (!(this.zzctZ == null || this.zzctZ.equals(""))) {
            com_google_android_gms_internal_adh.zzl(13, this.zzctZ);
        }
        if (!(this.zzcub == null || this.zzcub.equals(""))) {
            com_google_android_gms_internal_adh.zzl(14, this.zzcub);
        }
        if (this.zzcuc != 180000) {
            com_google_android_gms_internal_adh.zzd(15, this.zzcuc);
        }
        if (this.zzcud != null) {
            com_google_android_gms_internal_adh.zza(16, this.zzcud);
        }
        if (this.zzctR != 0) {
            com_google_android_gms_internal_adh.zzb(17, this.zzctR);
        }
        if (!Arrays.equals(this.zzcue, ads.zzcsI)) {
            com_google_android_gms_internal_adh.zzb(18, this.zzcue);
        }
        if (this.zzcug != 0) {
            com_google_android_gms_internal_adh.zzr(19, this.zzcug);
        }
        if (this.zzcuh != null && this.zzcuh.length > 0) {
            while (i < this.zzcuh.length) {
                com_google_android_gms_internal_adh.zzr(20, this.zzcuh[i]);
                i++;
            }
        }
        if (this.zzctS != 0) {
            com_google_android_gms_internal_adh.zzb(21, this.zzctS);
        }
        if (this.zzcui != 0) {
            com_google_android_gms_internal_adh.zzb(22, this.zzcui);
        }
        if (this.zzcmX != null) {
            com_google_android_gms_internal_adh.zza(23, this.zzcmX);
        }
        if (!(this.zzcuf == null || this.zzcuf.equals(""))) {
            com_google_android_gms_internal_adh.zzl(24, this.zzcuf);
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int i;
        int i2 = 0;
        int zzn = super.zzn();
        if (this.zzctQ != 0) {
            zzn += adh.zze(1, this.zzctQ);
        }
        if (!(this.tag == null || this.tag.equals(""))) {
            zzn += adh.zzm(2, this.tag);
        }
        if (this.zzctU != null && this.zzctU.length > 0) {
            i = zzn;
            for (adp com_google_android_gms_internal_adp : this.zzctU) {
                if (com_google_android_gms_internal_adp != null) {
                    i += adh.zzb(3, com_google_android_gms_internal_adp);
                }
            }
            zzn = i;
        }
        if (!Arrays.equals(this.zzctV, ads.zzcsI)) {
            zzn += adh.zzc(4, this.zzctV);
        }
        if (!Arrays.equals(this.zzctX, ads.zzcsI)) {
            zzn += adh.zzc(6, this.zzctX);
        }
        if (this.zzcua != null) {
            zzn += adh.zzb(7, this.zzcua);
        }
        if (!(this.zzctY == null || this.zzctY.equals(""))) {
            zzn += adh.zzm(8, this.zzctY);
        }
        if (this.zzctW != null) {
            zzn += adh.zzb(9, this.zzctW);
        }
        if (this.zzccZ) {
            zzn += adh.zzct(10) + 1;
        }
        if (this.zzctT != 0) {
            zzn += adh.zzs(11, this.zzctT);
        }
        if (this.zzrB != 0) {
            zzn += adh.zzs(12, this.zzrB);
        }
        if (!(this.zzctZ == null || this.zzctZ.equals(""))) {
            zzn += adh.zzm(13, this.zzctZ);
        }
        if (!(this.zzcub == null || this.zzcub.equals(""))) {
            zzn += adh.zzm(14, this.zzcub);
        }
        if (this.zzcuc != 180000) {
            zzn += adh.zzf(15, this.zzcuc);
        }
        if (this.zzcud != null) {
            zzn += adh.zzb(16, this.zzcud);
        }
        if (this.zzctR != 0) {
            zzn += adh.zze(17, this.zzctR);
        }
        if (!Arrays.equals(this.zzcue, ads.zzcsI)) {
            zzn += adh.zzc(18, this.zzcue);
        }
        if (this.zzcug != 0) {
            zzn += adh.zzs(19, this.zzcug);
        }
        if (this.zzcuh != null && this.zzcuh.length > 0) {
            i = 0;
            while (i2 < this.zzcuh.length) {
                i += adh.zzcr(this.zzcuh[i2]);
                i2++;
            }
            zzn = (zzn + i) + (this.zzcuh.length * 2);
        }
        if (this.zzctS != 0) {
            zzn += adh.zze(21, this.zzctS);
        }
        if (this.zzcui != 0) {
            zzn += adh.zze(22, this.zzcui);
        }
        if (this.zzcmX != null) {
            zzn += adh.zzb(23, this.zzcmX);
        }
        return (this.zzcuf == null || this.zzcuf.equals("")) ? zzn : zzn + adh.zzm(24, this.zzcuf);
    }
}

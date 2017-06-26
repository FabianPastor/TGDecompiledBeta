package com.google.android.gms.internal;

import java.io.IOException;

public final class aeb extends ada<aeb> implements Cloneable {
    private static volatile aeb[] zzctU;
    private String key;
    private String value;

    public aeb() {
        this.key = "";
        this.value = "";
        this.zzcrZ = null;
        this.zzcsi = -1;
    }

    public static aeb[] zzMc() {
        if (zzctU == null) {
            synchronized (ade.zzcsh) {
                if (zzctU == null) {
                    zzctU = new aeb[0];
                }
            }
        }
        return zzctU;
    }

    private aeb zzMd() {
        try {
            return (aeb) super.zzLL();
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
        if (!(obj instanceof aeb)) {
            return false;
        }
        aeb com_google_android_gms_internal_aeb = (aeb) obj;
        if (this.key == null) {
            if (com_google_android_gms_internal_aeb.key != null) {
                return false;
            }
        } else if (!this.key.equals(com_google_android_gms_internal_aeb.key)) {
            return false;
        }
        if (this.value == null) {
            if (com_google_android_gms_internal_aeb.value != null) {
                return false;
            }
        } else if (!this.value.equals(com_google_android_gms_internal_aeb.value)) {
            return false;
        }
        return (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? com_google_android_gms_internal_aeb.zzcrZ == null || com_google_android_gms_internal_aeb.zzcrZ.isEmpty() : this.zzcrZ.equals(com_google_android_gms_internal_aeb.zzcrZ);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.value == null ? 0 : this.value.hashCode()) + (((this.key == null ? 0 : this.key.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31;
        if (!(this.zzcrZ == null || this.zzcrZ.isEmpty())) {
            i = this.zzcrZ.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ ada zzLL() throws CloneNotSupportedException {
        return (aeb) clone();
    }

    public final /* synthetic */ adg zzLM() throws CloneNotSupportedException {
        return (aeb) clone();
    }

    public final /* synthetic */ adg zza(acx com_google_android_gms_internal_acx) throws IOException {
        while (true) {
            int zzLy = com_google_android_gms_internal_acx.zzLy();
            switch (zzLy) {
                case 0:
                    break;
                case 10:
                    this.key = com_google_android_gms_internal_acx.readString();
                    continue;
                case 18:
                    this.value = com_google_android_gms_internal_acx.readString();
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
        if (!(this.key == null || this.key.equals(""))) {
            com_google_android_gms_internal_acy.zzl(1, this.key);
        }
        if (!(this.value == null || this.value.equals(""))) {
            com_google_android_gms_internal_acy.zzl(2, this.value);
        }
        super.zza(com_google_android_gms_internal_acy);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (!(this.key == null || this.key.equals(""))) {
            zzn += acy.zzm(1, this.key);
        }
        return (this.value == null || this.value.equals("")) ? zzn : zzn + acy.zzm(2, this.value);
    }
}

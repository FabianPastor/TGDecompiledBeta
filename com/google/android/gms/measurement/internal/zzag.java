package com.google.android.gms.measurement.internal;

import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzvk.zzf;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

class zzag {
    final boolean apN;
    final int asn;
    final boolean aso;
    final String asp;
    final List<String> asq;
    final String asr;

    public zzag(zzf com_google_android_gms_internal_zzvk_zzf) {
        boolean z;
        boolean z2 = false;
        zzac.zzy(com_google_android_gms_internal_zzvk_zzf);
        if (com_google_android_gms_internal_zzvk_zzf.asW == null || com_google_android_gms_internal_zzvk_zzf.asW.intValue() == 0) {
            z = false;
        } else {
            if (com_google_android_gms_internal_zzvk_zzf.asW.intValue() == 6) {
                if (com_google_android_gms_internal_zzvk_zzf.asZ == null || com_google_android_gms_internal_zzvk_zzf.asZ.length == 0) {
                    z = false;
                }
            } else if (com_google_android_gms_internal_zzvk_zzf.asX == null) {
                z = false;
            }
            z = true;
        }
        if (z) {
            this.asn = com_google_android_gms_internal_zzvk_zzf.asW.intValue();
            if (com_google_android_gms_internal_zzvk_zzf.asY != null && com_google_android_gms_internal_zzvk_zzf.asY.booleanValue()) {
                z2 = true;
            }
            this.aso = z2;
            if (this.aso || this.asn == 1 || this.asn == 6) {
                this.asp = com_google_android_gms_internal_zzvk_zzf.asX;
            } else {
                this.asp = com_google_android_gms_internal_zzvk_zzf.asX.toUpperCase(Locale.ENGLISH);
            }
            this.asq = com_google_android_gms_internal_zzvk_zzf.asZ == null ? null : zza(com_google_android_gms_internal_zzvk_zzf.asZ, this.aso);
            if (this.asn == 1) {
                this.asr = this.asp;
            } else {
                this.asr = null;
            }
        } else {
            this.asn = 0;
            this.aso = false;
            this.asp = null;
            this.asq = null;
            this.asr = null;
        }
        this.apN = z;
    }

    private List<String> zza(String[] strArr, boolean z) {
        if (z) {
            return Arrays.asList(strArr);
        }
        List<String> arrayList = new ArrayList();
        for (String toUpperCase : strArr) {
            arrayList.add(toUpperCase.toUpperCase(Locale.ENGLISH));
        }
        return arrayList;
    }

    public Boolean zzmw(String str) {
        if (!this.apN || str == null) {
            return null;
        }
        if (!(this.aso || this.asn == 1)) {
            str = str.toUpperCase(Locale.ENGLISH);
        }
        switch (this.asn) {
            case 1:
                return Boolean.valueOf(Pattern.compile(this.asr, this.aso ? 0 : 66).matcher(str).matches());
            case 2:
                return Boolean.valueOf(str.startsWith(this.asp));
            case 3:
                return Boolean.valueOf(str.endsWith(this.asp));
            case 4:
                return Boolean.valueOf(str.contains(this.asp));
            case 5:
                return Boolean.valueOf(str.equals(this.asp));
            case 6:
                return Boolean.valueOf(this.asq.contains(str));
            default:
                return null;
        }
    }
}

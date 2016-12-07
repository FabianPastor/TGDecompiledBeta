package com.google.android.gms.measurement.internal;

import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzvk.zzd;
import java.math.BigDecimal;

class zzs {
    final int apJ;
    BigDecimal apK;
    BigDecimal apL;
    BigDecimal apM;
    final boolean apN;

    public zzs(zzd com_google_android_gms_internal_zzvk_zzd) {
        zzac.zzy(com_google_android_gms_internal_zzvk_zzd);
        boolean z = true;
        if (com_google_android_gms_internal_zzvk_zzd.asO == null || com_google_android_gms_internal_zzvk_zzd.asO.intValue() == 0) {
            z = false;
        } else if (com_google_android_gms_internal_zzvk_zzd.asO.intValue() != 4) {
            if (com_google_android_gms_internal_zzvk_zzd.asQ == null) {
                z = false;
            }
        } else if (com_google_android_gms_internal_zzvk_zzd.asR == null || com_google_android_gms_internal_zzvk_zzd.asS == null) {
            z = false;
        }
        if (z) {
            this.apJ = com_google_android_gms_internal_zzvk_zzd.asO.intValue();
            if (com_google_android_gms_internal_zzvk_zzd.asO.intValue() == 4) {
                if (!(zzal.zznj(com_google_android_gms_internal_zzvk_zzd.asR) && zzal.zznj(com_google_android_gms_internal_zzvk_zzd.asS))) {
                    z = false;
                }
                try {
                    this.apL = new BigDecimal(com_google_android_gms_internal_zzvk_zzd.asR);
                    this.apM = new BigDecimal(com_google_android_gms_internal_zzvk_zzd.asS);
                } catch (NumberFormatException e) {
                    z = false;
                }
            } else {
                if (!zzal.zznj(com_google_android_gms_internal_zzvk_zzd.asQ)) {
                    z = false;
                }
                try {
                    this.apK = new BigDecimal(com_google_android_gms_internal_zzvk_zzd.asQ);
                } catch (NumberFormatException e2) {
                    z = false;
                }
            }
        } else {
            this.apJ = 0;
        }
        this.apN = z;
    }

    private Boolean zza(BigDecimal bigDecimal) {
        boolean z = true;
        if (!this.apN) {
            return null;
        }
        if (bigDecimal == null) {
            return null;
        }
        switch (this.apJ) {
            case 1:
                if (bigDecimal.compareTo(this.apK) != -1) {
                    z = false;
                }
                return Boolean.valueOf(z);
            case 2:
                if (bigDecimal.compareTo(this.apK) != 1) {
                    z = false;
                }
                return Boolean.valueOf(z);
            case 3:
                if (bigDecimal.compareTo(this.apK) != 0) {
                    z = false;
                }
                return Boolean.valueOf(z);
            case 4:
                if (bigDecimal.compareTo(this.apL) == -1 || bigDecimal.compareTo(this.apM) == 1) {
                    z = false;
                }
                return Boolean.valueOf(z);
            default:
                return null;
        }
    }

    public Boolean zzbn(long j) {
        try {
            return zza(new BigDecimal(j));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Boolean zzj(double d) {
        boolean z = true;
        if (!this.apN) {
            return null;
        }
        try {
            BigDecimal bigDecimal = new BigDecimal(d);
            switch (this.apJ) {
                case 1:
                    if (bigDecimal.compareTo(this.apK) != -1) {
                        z = false;
                    }
                    return Boolean.valueOf(z);
                case 2:
                    if (bigDecimal.compareTo(this.apK) != 1) {
                        z = false;
                    }
                    return Boolean.valueOf(z);
                case 3:
                    if (!(bigDecimal.compareTo(this.apK.subtract(new BigDecimal(Math.ulp(d)).multiply(new BigDecimal(2)))) == 1 && bigDecimal.compareTo(this.apK.add(new BigDecimal(Math.ulp(d)).multiply(new BigDecimal(2)))) == -1)) {
                        z = false;
                    }
                    return Boolean.valueOf(z);
                case 4:
                    if (bigDecimal.compareTo(this.apL) == -1 || bigDecimal.compareTo(this.apM) == 1) {
                        z = false;
                    }
                    return Boolean.valueOf(z);
                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Boolean zzmk(String str) {
        Boolean bool = null;
        if (zzal.zznj(str)) {
            try {
                bool = zza(new BigDecimal(str));
            } catch (NumberFormatException e) {
            }
        }
        return bool;
    }
}

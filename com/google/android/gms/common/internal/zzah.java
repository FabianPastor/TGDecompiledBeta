package com.google.android.gms.common.internal;

import android.content.ComponentName;
import android.content.Intent;
import java.util.Arrays;

public final class zzah {
    private final ComponentName mComponentName = null;
    private final String zzdrp;
    private final String zzgak;
    private final int zzgal;

    public zzah(String str, String str2, int i) {
        this.zzdrp = zzbq.zzgm(str);
        this.zzgak = zzbq.zzgm(str2);
        this.zzgal = i;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzah)) {
            return false;
        }
        zzah com_google_android_gms_common_internal_zzah = (zzah) obj;
        return zzbg.equal(this.zzdrp, com_google_android_gms_common_internal_zzah.zzdrp) && zzbg.equal(this.zzgak, com_google_android_gms_common_internal_zzah.zzgak) && zzbg.equal(this.mComponentName, com_google_android_gms_common_internal_zzah.mComponentName) && this.zzgal == com_google_android_gms_common_internal_zzah.zzgal;
    }

    public final ComponentName getComponentName() {
        return this.mComponentName;
    }

    public final String getPackage() {
        return this.zzgak;
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{this.zzdrp, this.zzgak, this.mComponentName, Integer.valueOf(this.zzgal)});
    }

    public final String toString() {
        return this.zzdrp == null ? this.mComponentName.flattenToString() : this.zzdrp;
    }

    public final int zzalk() {
        return this.zzgal;
    }

    public final Intent zzall() {
        return this.zzdrp != null ? new Intent(this.zzdrp).setPackage(this.zzgak) : new Intent().setComponent(this.mComponentName);
    }
}

package com.google.android.gms.internal;

import java.math.BigDecimal;

public final class zzapv extends Number {
    private final String value;

    public zzapv(String str) {
        this.value = str;
    }

    public double doubleValue() {
        return Double.parseDouble(this.value);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzapv)) {
            return false;
        }
        zzapv com_google_android_gms_internal_zzapv = (zzapv) obj;
        return this.value == com_google_android_gms_internal_zzapv.value || this.value.equals(com_google_android_gms_internal_zzapv.value);
    }

    public float floatValue() {
        return Float.parseFloat(this.value);
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    public int intValue() {
        try {
            return Integer.parseInt(this.value);
        } catch (NumberFormatException e) {
            try {
                return (int) Long.parseLong(this.value);
            } catch (NumberFormatException e2) {
                return new BigDecimal(this.value).intValue();
            }
        }
    }

    public long longValue() {
        try {
            return Long.parseLong(this.value);
        } catch (NumberFormatException e) {
            return new BigDecimal(this.value).longValue();
        }
    }

    public String toString() {
        return this.value;
    }
}

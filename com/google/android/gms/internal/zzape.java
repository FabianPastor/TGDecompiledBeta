package com.google.android.gms.internal;

import java.math.BigInteger;

public final class zzape extends zzaoy {
    private static final Class<?>[] bow = new Class[]{Integer.TYPE, Long.TYPE, Short.TYPE, Float.TYPE, Double.TYPE, Byte.TYPE, Boolean.TYPE, Character.TYPE, Integer.class, Long.class, Short.class, Float.class, Double.class, Byte.class, Boolean.class, Character.class};
    private Object value;

    public zzape(Boolean bool) {
        setValue(bool);
    }

    public zzape(Number number) {
        setValue(number);
    }

    zzape(Object obj) {
        setValue(obj);
    }

    public zzape(String str) {
        setValue(str);
    }

    private static boolean zza(zzape com_google_android_gms_internal_zzape) {
        if (!(com_google_android_gms_internal_zzape.value instanceof Number)) {
            return false;
        }
        Number number = (Number) com_google_android_gms_internal_zzape.value;
        return (number instanceof BigInteger) || (number instanceof Long) || (number instanceof Integer) || (number instanceof Short) || (number instanceof Byte);
    }

    private static boolean zzcm(Object obj) {
        if (obj instanceof String) {
            return true;
        }
        Class cls = obj.getClass();
        for (Class isAssignableFrom : bow) {
            if (isAssignableFrom.isAssignableFrom(cls)) {
                return true;
            }
        }
        return false;
    }

    public Number aT() {
        return this.value instanceof String ? new zzapv((String) this.value) : (Number) this.value;
    }

    public String aU() {
        return be() ? aT().toString() : bd() ? bc().toString() : (String) this.value;
    }

    Boolean bc() {
        return (Boolean) this.value;
    }

    public boolean bd() {
        return this.value instanceof Boolean;
    }

    public boolean be() {
        return this.value instanceof Number;
    }

    public boolean bf() {
        return this.value instanceof String;
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        zzape com_google_android_gms_internal_zzape = (zzape) obj;
        if (this.value == null) {
            return com_google_android_gms_internal_zzape.value == null;
        } else {
            if (zza(this) && zza(com_google_android_gms_internal_zzape)) {
                return aT().longValue() == com_google_android_gms_internal_zzape.aT().longValue();
            } else {
                if (!(this.value instanceof Number) || !(com_google_android_gms_internal_zzape.value instanceof Number)) {
                    return this.value.equals(com_google_android_gms_internal_zzape.value);
                }
                double doubleValue = aT().doubleValue();
                double doubleValue2 = com_google_android_gms_internal_zzape.aT().doubleValue();
                if (doubleValue == doubleValue2 || (Double.isNaN(doubleValue) && Double.isNaN(doubleValue2))) {
                    z = true;
                }
                return z;
            }
        }
    }

    public boolean getAsBoolean() {
        return bd() ? bc().booleanValue() : Boolean.parseBoolean(aU());
    }

    public double getAsDouble() {
        return be() ? aT().doubleValue() : Double.parseDouble(aU());
    }

    public int getAsInt() {
        return be() ? aT().intValue() : Integer.parseInt(aU());
    }

    public long getAsLong() {
        return be() ? aT().longValue() : Long.parseLong(aU());
    }

    public int hashCode() {
        if (this.value == null) {
            return 31;
        }
        long longValue;
        if (zza(this)) {
            longValue = aT().longValue();
            return (int) (longValue ^ (longValue >>> 32));
        } else if (!(this.value instanceof Number)) {
            return this.value.hashCode();
        } else {
            longValue = Double.doubleToLongBits(aT().doubleValue());
            return (int) (longValue ^ (longValue >>> 32));
        }
    }

    void setValue(Object obj) {
        if (obj instanceof Character) {
            this.value = String.valueOf(((Character) obj).charValue());
            return;
        }
        boolean z = (obj instanceof Number) || zzcm(obj);
        zzapq.zzbt(z);
        this.value = obj;
    }
}

package com.google.android.gms.internal;

import java.math.BigInteger;

public final class zzaon extends zzaoh {
    private static final Class<?>[] blf = new Class[]{Integer.TYPE, Long.TYPE, Short.TYPE, Float.TYPE, Double.TYPE, Byte.TYPE, Boolean.TYPE, Character.TYPE, Integer.class, Long.class, Short.class, Float.class, Double.class, Byte.class, Boolean.class, Character.class};
    private Object value;

    public zzaon(Boolean bool) {
        setValue(bool);
    }

    public zzaon(Number number) {
        setValue(number);
    }

    zzaon(Object obj) {
        setValue(obj);
    }

    public zzaon(String str) {
        setValue(str);
    }

    private static boolean zza(zzaon com_google_android_gms_internal_zzaon) {
        if (!(com_google_android_gms_internal_zzaon.value instanceof Number)) {
            return false;
        }
        Number number = (Number) com_google_android_gms_internal_zzaon.value;
        return (number instanceof BigInteger) || (number instanceof Long) || (number instanceof Integer) || (number instanceof Short) || (number instanceof Byte);
    }

    private static boolean zzcn(Object obj) {
        if (obj instanceof String) {
            return true;
        }
        Class cls = obj.getClass();
        for (Class isAssignableFrom : blf) {
            if (isAssignableFrom.isAssignableFrom(cls)) {
                return true;
            }
        }
        return false;
    }

    public Number aQ() {
        return this.value instanceof String ? new zzape((String) this.value) : (Number) this.value;
    }

    public String aR() {
        return bb() ? aQ().toString() : ba() ? aZ().toString() : (String) this.value;
    }

    Boolean aZ() {
        return (Boolean) this.value;
    }

    public boolean ba() {
        return this.value instanceof Boolean;
    }

    public boolean bb() {
        return this.value instanceof Number;
    }

    public boolean bc() {
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
        zzaon com_google_android_gms_internal_zzaon = (zzaon) obj;
        if (this.value == null) {
            return com_google_android_gms_internal_zzaon.value == null;
        } else {
            if (zza(this) && zza(com_google_android_gms_internal_zzaon)) {
                return aQ().longValue() == com_google_android_gms_internal_zzaon.aQ().longValue();
            } else {
                if (!(this.value instanceof Number) || !(com_google_android_gms_internal_zzaon.value instanceof Number)) {
                    return this.value.equals(com_google_android_gms_internal_zzaon.value);
                }
                double doubleValue = aQ().doubleValue();
                double doubleValue2 = com_google_android_gms_internal_zzaon.aQ().doubleValue();
                if (doubleValue == doubleValue2 || (Double.isNaN(doubleValue) && Double.isNaN(doubleValue2))) {
                    z = true;
                }
                return z;
            }
        }
    }

    public boolean getAsBoolean() {
        return ba() ? aZ().booleanValue() : Boolean.parseBoolean(aR());
    }

    public double getAsDouble() {
        return bb() ? aQ().doubleValue() : Double.parseDouble(aR());
    }

    public int getAsInt() {
        return bb() ? aQ().intValue() : Integer.parseInt(aR());
    }

    public long getAsLong() {
        return bb() ? aQ().longValue() : Long.parseLong(aR());
    }

    public int hashCode() {
        if (this.value == null) {
            return 31;
        }
        long longValue;
        if (zza(this)) {
            longValue = aQ().longValue();
            return (int) (longValue ^ (longValue >>> 32));
        } else if (!(this.value instanceof Number)) {
            return this.value.hashCode();
        } else {
            longValue = Double.doubleToLongBits(aQ().doubleValue());
            return (int) (longValue ^ (longValue >>> 32));
        }
    }

    void setValue(Object obj) {
        if (obj instanceof Character) {
            this.value = String.valueOf(((Character) obj).charValue());
            return;
        }
        boolean z = (obj instanceof Number) || zzcn(obj);
        zzaoz.zzbs(z);
        this.value = obj;
    }
}

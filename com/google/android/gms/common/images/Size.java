package com.google.android.gms.common.images;

public final class Size {
    private final int zzajw;
    private final int zzajx;

    public Size(int i, int i2) {
        this.zzajw = i;
        this.zzajx = i2;
    }

    public static Size parseSize(String str) throws NumberFormatException {
        if (str == null) {
            throw new IllegalArgumentException("string must not be null");
        }
        int indexOf = str.indexOf(42);
        if (indexOf < 0) {
            indexOf = str.indexOf(120);
        }
        if (indexOf < 0) {
            throw zzhp(str);
        }
        try {
            return new Size(Integer.parseInt(str.substring(0, indexOf)), Integer.parseInt(str.substring(indexOf + 1)));
        } catch (NumberFormatException e) {
            throw zzhp(str);
        }
    }

    private static NumberFormatException zzhp(String str) {
        throw new NumberFormatException(new StringBuilder(String.valueOf(str).length() + 16).append("Invalid Size: \"").append(str).append("\"").toString());
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Size)) {
            return false;
        }
        Size size = (Size) obj;
        if (!(this.zzajw == size.zzajw && this.zzajx == size.zzajx)) {
            z = false;
        }
        return z;
    }

    public int getHeight() {
        return this.zzajx;
    }

    public int getWidth() {
        return this.zzajw;
    }

    public int hashCode() {
        return this.zzajx ^ ((this.zzajw << 16) | (this.zzajw >>> 16));
    }

    public String toString() {
        int i = this.zzajw;
        return i + "x" + this.zzajx;
    }
}

package com.google.android.gms.common.images;

public final class Size {
    private final int zzrG;
    private final int zzrH;

    public Size(int i, int i2) {
        this.zzrG = i;
        this.zzrH = i2;
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
            throw zzdm(str);
        }
        try {
            return new Size(Integer.parseInt(str.substring(0, indexOf)), Integer.parseInt(str.substring(indexOf + 1)));
        } catch (NumberFormatException e) {
            throw zzdm(str);
        }
    }

    private static NumberFormatException zzdm(String str) {
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
        if (!(this.zzrG == size.zzrG && this.zzrH == size.zzrH)) {
            z = false;
        }
        return z;
    }

    public int getHeight() {
        return this.zzrH;
    }

    public int getWidth() {
        return this.zzrG;
    }

    public int hashCode() {
        return this.zzrH ^ ((this.zzrG << 16) | (this.zzrG >>> 16));
    }

    public String toString() {
        int i = this.zzrG;
        return i + "x" + this.zzrH;
    }
}

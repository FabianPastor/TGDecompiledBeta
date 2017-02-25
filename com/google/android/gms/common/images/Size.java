package com.google.android.gms.common.images;

public final class Size {
    private final int zzrC;
    private final int zzrD;

    public Size(int i, int i2) {
        this.zzrC = i;
        this.zzrD = i2;
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
            throw zzdi(str);
        }
        try {
            return new Size(Integer.parseInt(str.substring(0, indexOf)), Integer.parseInt(str.substring(indexOf + 1)));
        } catch (NumberFormatException e) {
            throw zzdi(str);
        }
    }

    private static NumberFormatException zzdi(String str) {
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
        if (!(this.zzrC == size.zzrC && this.zzrD == size.zzrD)) {
            z = false;
        }
        return z;
    }

    public int getHeight() {
        return this.zzrD;
    }

    public int getWidth() {
        return this.zzrC;
    }

    public int hashCode() {
        return this.zzrD ^ ((this.zzrC << 16) | (this.zzrC >>> 16));
    }

    public String toString() {
        int i = this.zzrC;
        return i + "x" + this.zzrD;
    }
}

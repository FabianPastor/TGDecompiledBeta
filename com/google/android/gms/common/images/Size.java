package com.google.android.gms.common.images;

public final class Size {
    private final int zzakh;
    private final int zzaki;

    public Size(int i, int i2) {
        this.zzakh = i;
        this.zzaki = i2;
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
            throw zzhr(str);
        }
        try {
            return new Size(Integer.parseInt(str.substring(0, indexOf)), Integer.parseInt(str.substring(indexOf + 1)));
        } catch (NumberFormatException e) {
            throw zzhr(str);
        }
    }

    private static NumberFormatException zzhr(String str) {
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
        if (!(this.zzakh == size.zzakh && this.zzaki == size.zzaki)) {
            z = false;
        }
        return z;
    }

    public int getHeight() {
        return this.zzaki;
    }

    public int getWidth() {
        return this.zzakh;
    }

    public int hashCode() {
        return this.zzaki ^ ((this.zzakh << 16) | (this.zzakh >>> 16));
    }

    public String toString() {
        int i = this.zzakh;
        return i + "x" + this.zzaki;
    }
}

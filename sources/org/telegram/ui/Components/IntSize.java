package org.telegram.ui.Components;
/* loaded from: classes3.dex */
public class IntSize {
    public int height;
    public int width;

    public IntSize() {
    }

    public IntSize(int i, int i2) {
        this.width = i;
        this.height = i2;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || IntSize.class != obj.getClass()) {
            return false;
        }
        IntSize intSize = (IntSize) obj;
        return this.width == intSize.width && this.height == intSize.height;
    }

    public int hashCode() {
        return (this.width * 31) + this.height;
    }

    public String toString() {
        return "IntSize(" + this.width + ", " + this.height + ")";
    }
}

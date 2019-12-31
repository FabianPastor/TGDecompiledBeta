package org.telegram.ui.Components;

public class IntSize {
    public int height;
    public int width;

    public IntSize(IntSize intSize) {
        this.width = intSize.width;
        this.height = intSize.height;
    }

    public IntSize(int i, int i2) {
        this.width = i;
        this.height = i2;
    }

    public void set(int i, int i2) {
        this.width = i;
        this.height = i2;
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || IntSize.class != obj.getClass()) {
            return false;
        }
        IntSize intSize = (IntSize) obj;
        if (this.width != intSize.width) {
            return false;
        }
        if (this.height != intSize.height) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        return (this.width * 31) + this.height;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("IntSize(");
        stringBuilder.append(this.width);
        stringBuilder.append(", ");
        stringBuilder.append(this.height);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}

package org.telegram.ui.Components;

public class IntSize {
    public int height;
    public int width;

    public IntSize() {
    }

    public IntSize(IntSize size) {
        this.width = size.width;
        this.height = size.height;
    }

    public IntSize(int width2, int height2) {
        this.width = width2;
        this.height = height2;
    }

    public void set(int width2, int height2) {
        this.width = width2;
        this.height = height2;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IntSize intSize = (IntSize) o;
        if (this.width == intSize.width && this.height == intSize.height) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (this.width * 31) + this.height;
    }

    public String toString() {
        return "IntSize(" + this.width + ", " + this.height + ")";
    }
}
